use actix_web::{
    HttpRequest,
    http::{StatusCode, header},
};
use base64::{Engine, engine::general_purpose::STANDARD};
use chrono::{DateTime, Duration, FixedOffset, Utc};
use models::{
    account, account_oauth, authorization_code,
    enums::{ClientAuthMethod, GrantType, Scope, TokenType},
    oauth_client, token,
};
use sea_orm::{
    ActiveModelTrait, ColumnTrait, ConnectionTrait, EntityTrait, QueryFilter, QuerySelect, Set,
    TransactionTrait, sea_query::Expr,
};
use uuid::Uuid;

use crate::service::{account as accounts, crypto, oauth};
use crate::{
    database,
    dto::oidc::{IdTokenClaims, RevocationRequest, TokenRequest, TokenResponse, UserInfoResponse},
    error::{ApiError, ApiResult},
    state::AppState,
};

fn invalid_grant() -> ApiError {
    ApiError::protocol("invalid_grant", "Invalid, expired or revoked credential")
}
fn invalid_client() -> ApiError {
    ApiError::new(
        StatusCode::UNAUTHORIZED,
        "invalid_client",
        "Client authentication failed",
    )
}

fn decode_basic(value: &str) -> ApiResult<String> {
    let pairs = url::form_urlencoded::parse(format!("value={value}").as_bytes())
        .into_owned()
        .collect::<Vec<_>>();
    if pairs.len() != 1 {
        return Err(invalid_client());
    }
    Ok(pairs[0].1.clone())
}

async fn authenticate_client(
    state: &AppState,
    request: &HttpRequest,
    body_id: Option<&str>,
    body_secret: Option<&str>,
) -> ApiResult<oauth_client::Model> {
    let (id, secret, method) = if let Some(header) = request.headers().get(header::AUTHORIZATION) {
        if body_secret.is_some() {
            return Err(invalid_client());
        }
        let header = header.to_str().map_err(|_| invalid_client())?;
        let (scheme, value) = header.split_once(' ').ok_or_else(invalid_client)?;
        if !scheme.eq_ignore_ascii_case("Basic") || value.len() > 2048 {
            return Err(invalid_client());
        }
        let decoded = String::from_utf8(STANDARD.decode(value).map_err(|_| invalid_client())?)
            .map_err(|_| invalid_client())?;
        let (id, secret) = decoded.split_once(':').ok_or_else(invalid_client)?;
        let id = decode_basic(id)?;
        if body_id.is_some_and(|body_id| body_id != id) {
            return Err(invalid_client());
        }
        (
            id,
            Some(decode_basic(secret)?),
            ClientAuthMethod::ClientSecretBasic,
        )
    } else {
        (
            body_id.ok_or_else(invalid_client)?.to_owned(),
            body_secret.map(str::to_owned),
            if body_secret.is_some() {
                ClientAuthMethod::ClientSecretPost
            } else {
                ClientAuthMethod::None
            },
        )
    };
    let id = Uuid::parse_str(&id).map_err(|_| invalid_client())?;
    let (client, _) = oauth::active_client(&*state.database, id)
        .await
        .map_err(|error| {
            if error.status.is_server_error() {
                error
            } else {
                invalid_client()
            }
        })?;
    if client.token_endpoint_auth_method != method {
        return Err(invalid_client());
    }
    if method != ClientAuthMethod::None {
        let secret = secret.as_deref().ok_or_else(invalid_client)?;
        if secret.len() > 512
            || !client
                .client_secret_hash
                .as_deref()
                .is_some_and(|hash| crypto::equal(hash, &crypto::hash(secret)))
        {
            return Err(invalid_client());
        }
    }
    Ok(client)
}

async fn revoke_family(db: &impl ConnectionTrait, family_id: Uuid) -> ApiResult<()> {
    let now = Utc::now().fixed_offset();
    token::Entity::update_many()
        .col_expr(token::Column::IsRevoked, Expr::value(true))
        .col_expr(token::Column::RevokedAt, Expr::value(now))
        .col_expr(token::Column::UpdatedAt, Expr::value(now))
        .filter(token::Column::TokenFamilyId.eq(family_id))
        .exec(db)
        .await?;
    Ok(())
}

async fn authorization(
    db: &impl ConnectionTrait,
    account_id: Uuid,
    grant_id: Option<Uuid>,
    client: &oauth_client::Model,
    scopes: &[Scope],
) -> ApiResult<account::Model> {
    let account = accounts::active(db, account_id).await.map_err(|error| {
        if error.status.is_server_error() {
            error
        } else {
            invalid_grant()
        }
    })?;
    let grant = account_oauth::Entity::find_by_id(grant_id.ok_or_else(invalid_grant)?)
        .filter(account_oauth::Column::AccountId.eq(account_id))
        .filter(account_oauth::Column::OauthId.eq(client.oauth_id))
        .filter(account_oauth::Column::RevokedAt.is_null())
        .one(db)
        .await?
        .ok_or_else(invalid_grant)?;
    if scopes
        .iter()
        .any(|s| !grant.scope.contains(s) || !client.allowed_scopes.contains(s))
    {
        return Err(invalid_grant());
    }
    Ok(account)
}

struct Issue<'a> {
    account: &'a account::Model,
    client: &'a oauth_client::Model,
    grant_id: Option<Uuid>,
    scope: Vec<Scope>,
    nonce: Option<String>,
    auth_time: DateTime<FixedOffset>,
    family_id: Uuid,
    code_id: Option<Uuid>,
    previous_id: Option<Uuid>,
    refresh_expires_at: Option<DateTime<FixedOffset>>,
}

async fn issue(
    state: &AppState,
    db: &impl ConnectionTrait,
    input: Issue<'_>,
) -> ApiResult<TokenResponse> {
    let now = Utc::now().fixed_offset();
    let access = crypto::secret();
    let refresh = input
        .refresh_expires_at
        .filter(|_| input.scope.contains(&Scope::OfflineAccess))
        .map(|_| crypto::secret());
    let id_token = if input.scope.contains(&Scope::Openid) {
        let claims = IdTokenClaims {
            user: super::claims(db, input.account, &input.scope).await?,
            iss: state.config.oidc.issuer.clone(),
            aud: input.client.id.to_string(),
            iat: now.timestamp(),
            exp: now.timestamp() + state.config.oidc.access_token_ttl_seconds,
            auth_time: input.auth_time.timestamp(),
            nonce: input.nonce.clone(),
        };
        Some(state.signing_key.sign(&claims)?)
    } else {
        None
    };
    let result = TokenResponse {
        access_token: access.clone(),
        token_type: "Bearer",
        expires_in: state.config.oidc.access_token_ttl_seconds,
        scope: super::scope_string(&input.scope),
        id_token,
        refresh_token: refresh.clone(),
    };
    token::ActiveModel {
        id: Set(Uuid::new_v4()),
        account_id: Set(input.account.id),
        oauth_id: Set(Some(input.client.oauth_id)),
        oauth_client_id: Set(Some(input.client.id)),
        scope: Set(input.scope),
        token_type: Set(TokenType::Bearer),
        grant_type: Set(if input.previous_id.is_some() {
            GrantType::RefreshToken
        } else {
            GrantType::AuthorizationCode
        }),
        account_oauth_id: Set(input.grant_id),
        token_family_id: Set(Some(input.family_id)),
        authorization_code_id: Set(input.code_id),
        nonce: Set(input.nonce),
        auth_time: Set(input.auth_time),
        access_token_hash: Set(crypto::hash(&access)),
        refresh_token_hash: Set(refresh.as_deref().map(crypto::hash)),
        previous_token_id: Set(input.previous_id),
        is_revoked: Set(false),
        is_refreshed: Set(false),
        created_at: Set(now),
        updated_at: Set(now),
        expires_at: Set(Some(
            now + Duration::seconds(state.config.oidc.access_token_ttl_seconds),
        )),
        revoked_at: Set(None),
        refreshed_at: Set(None),
        refresh_expires_at: Set(if refresh.is_some() {
            input.refresh_expires_at
        } else {
            None
        }),
        last_used_at: Set(None),
    }
    .insert(db)
    .await?;
    Ok(result)
}

pub async fn exchange(
    state: &AppState,
    request: &HttpRequest,
    input: TokenRequest,
) -> ApiResult<TokenResponse> {
    let client = authenticate_client(
        state,
        request,
        input.client_id.as_deref(),
        input.client_secret.as_deref(),
    )
    .await?;
    match input.grant_type.as_str() {
        "authorization_code" if client.grant_types.contains(&GrantType::AuthorizationCode) => {
            exchange_code(state, &client, input).await
        }
        "refresh_token" if client.grant_types.contains(&GrantType::RefreshToken) => {
            refresh(state, &client, input).await
        }
        "authorization_code" | "refresh_token" => Err(ApiError::protocol(
            "unauthorized_client",
            "Grant type is not allowed for this client",
        )),
        _ => Err(ApiError::protocol(
            "unsupported_grant_type",
            "Unsupported grant type",
        )),
    }
}

async fn exchange_code(
    state: &AppState,
    client: &oauth_client::Model,
    input: TokenRequest,
) -> ApiResult<TokenResponse> {
    let raw_code = input
        .code
        .as_deref()
        .ok_or_else(|| ApiError::bad("code is required"))?;
    let verifier = input
        .code_verifier
        .as_deref()
        .ok_or_else(|| ApiError::bad("code_verifier is required"))?;
    if raw_code.len() != 43
        || !(43..=128).contains(&verifier.len())
        || !verifier
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || b"-._~".contains(&b))
    {
        return Err(invalid_grant());
    }
    let lookup = authorization_code::Entity::find()
        .filter(authorization_code::Column::CodeHash.eq(crypto::hash(raw_code)));
    let snapshot = lookup
        .clone()
        .one(&*state.database)
        .await?
        .ok_or_else(invalid_grant)?;
    let tx = state.database.begin().await?;
    database::lock(&tx, &format!("family:{}", snapshot.id)).await?;
    let code = lookup
        .lock_exclusive()
        .one(&tx)
        .await?
        .ok_or_else(invalid_grant)?;
    if code.oauth_client_id != client.id
        || code.oauth_id != client.oauth_id
        || input.redirect_uri.as_deref() != Some(code.redirect_uri.as_str())
        || !client.redirect_uris.contains(&code.redirect_uri)
        || code.code_challenge_method.as_deref() != Some("S256")
        || !code
            .code_challenge
            .as_deref()
            .is_some_and(|c| crypto::equal(c, &crypto::hash(verifier)))
    {
        return Err(invalid_grant());
    }
    if code.is_consumed {
        revoke_family(&tx, code.id).await?;
        tx.commit().await?;
        return Err(invalid_grant());
    }
    if code.expires_at <= Utc::now().fixed_offset() {
        return Err(invalid_grant());
    }
    let account = authorization(
        &tx,
        code.account_id,
        code.account_oauth_id,
        client,
        &code.scope,
    )
    .await?;
    let result = issue(
        state,
        &tx,
        Issue {
            account: &account,
            client,
            grant_id: code.account_oauth_id,
            scope: code.scope.clone(),
            nonce: code.nonce.clone(),
            auth_time: code.auth_time,
            family_id: code.id,
            code_id: Some(code.id),
            previous_id: None,
            refresh_expires_at: Some(
                Utc::now().fixed_offset()
                    + Duration::seconds(state.config.oidc.refresh_token_ttl_seconds),
            ),
        },
    )
    .await?;
    let mut active: authorization_code::ActiveModel = code.into();
    active.is_consumed = Set(true);
    active.consumed_at = Set(Some(Utc::now().fixed_offset()));
    active.update(&tx).await?;
    tx.commit().await?;
    Ok(result)
}

async fn refresh(
    state: &AppState,
    client: &oauth_client::Model,
    input: TokenRequest,
) -> ApiResult<TokenResponse> {
    let raw = input
        .refresh_token
        .as_deref()
        .ok_or_else(|| ApiError::bad("refresh_token is required"))?;
    if raw.len() != 43 {
        return Err(invalid_grant());
    }
    let lookup =
        token::Entity::find().filter(token::Column::RefreshTokenHash.eq(crypto::hash(raw)));
    let snapshot = lookup
        .clone()
        .one(&*state.database)
        .await?
        .ok_or_else(invalid_grant)?;
    let family = snapshot.token_family_id.ok_or_else(invalid_grant)?;
    let tx = state.database.begin().await?;
    database::lock(&tx, &format!("family:{family}")).await?;
    let token = lookup
        .lock_exclusive()
        .one(&tx)
        .await?
        .ok_or_else(invalid_grant)?;
    if token.oauth_client_id != Some(client.id) || token.oauth_id != Some(client.oauth_id) {
        return Err(invalid_grant());
    }
    if token.is_refreshed {
        revoke_family(&tx, family).await?;
        tx.commit().await?;
        return Err(invalid_grant());
    }
    let now = Utc::now().fixed_offset();
    if token.is_revoked || !token.refresh_expires_at.is_some_and(|expiry| expiry > now) {
        return Err(invalid_grant());
    }
    let scopes = input
        .scope
        .as_deref()
        .map(super::scopes)
        .transpose()?
        .unwrap_or_else(|| token.scope.clone());
    if scopes.iter().any(|s| !token.scope.contains(s)) {
        return Err(ApiError::protocol(
            "invalid_scope",
            "Refresh cannot expand scopes",
        ));
    }
    let account = authorization(
        &tx,
        token.account_id,
        token.account_oauth_id,
        client,
        &scopes,
    )
    .await?;
    let result = issue(
        state,
        &tx,
        Issue {
            account: &account,
            client,
            grant_id: token.account_oauth_id,
            scope: scopes,
            nonce: token.nonce.clone(),
            auth_time: token.auth_time,
            family_id: family,
            code_id: token.authorization_code_id,
            previous_id: Some(token.id),
            refresh_expires_at: token.refresh_expires_at,
        },
    )
    .await?;
    let mut active: token::ActiveModel = token.into();
    active.is_refreshed = Set(true);
    active.refreshed_at = Set(Some(now));
    active.is_revoked = Set(true);
    active.revoked_at = Set(Some(now));
    active.updated_at = Set(now);
    active.update(&tx).await?;
    tx.commit().await?;
    Ok(result)
}

pub async fn userinfo(state: &AppState, request: &HttpRequest) -> ApiResult<UserInfoResponse> {
    let (scheme, raw) = request
        .headers()
        .get(header::AUTHORIZATION)
        .and_then(|h| h.to_str().ok())
        .and_then(|h| h.split_once(' '))
        .ok_or_else(ApiError::unauthorized)?;
    if !scheme.eq_ignore_ascii_case("Bearer") {
        return Err(ApiError::unauthorized());
    }
    if raw.len() != 43 {
        return Err(ApiError::unauthorized());
    }
    let token = token::Entity::find()
        .filter(token::Column::AccessTokenHash.eq(crypto::hash(raw)))
        .filter(token::Column::IsRevoked.eq(false))
        .filter(token::Column::ExpiresAt.gt(Utc::now().fixed_offset()))
        .one(&*state.database)
        .await?
        .ok_or_else(ApiError::unauthorized)?;
    if !token.scope.contains(&Scope::Openid) {
        return Err(ApiError::new(
            StatusCode::FORBIDDEN,
            "insufficient_scope",
            "openid scope is required",
        ));
    }
    let (client, _) = oauth::active_client(
        &*state.database,
        token.oauth_client_id.ok_or_else(ApiError::unauthorized)?,
    )
    .await
    .map_err(|error| {
        if error.status.is_server_error() {
            error
        } else {
            ApiError::unauthorized()
        }
    })?;
    if token.oauth_id != Some(client.oauth_id) {
        return Err(ApiError::unauthorized());
    }
    let account = authorization(
        &*state.database,
        token.account_id,
        token.account_oauth_id,
        &client,
        &token.scope,
    )
    .await
    .map_err(|error| {
        if error.status.is_server_error() {
            error
        } else {
            ApiError::unauthorized()
        }
    })?;
    token::Entity::update_many()
        .col_expr(
            token::Column::LastUsedAt,
            Expr::value(Utc::now().fixed_offset()),
        )
        .filter(token::Column::Id.eq(token.id))
        .exec(&*state.database)
        .await?;
    super::claims(&*state.database, &account, &token.scope).await
}

pub async fn revoke(
    state: &AppState,
    request: &HttpRequest,
    input: RevocationRequest,
) -> ApiResult<()> {
    let client = authenticate_client(
        state,
        request,
        input.client_id.as_deref(),
        input.client_secret.as_deref(),
    )
    .await?;
    let raw = input.token.as_str();
    if raw.len() != 43 {
        return Ok(());
    }
    let hash = crypto::hash(raw);
    let token = token::Entity::find()
        .filter(token::Column::OauthClientId.eq(client.id))
        .filter(
            sea_orm::Condition::any()
                .add(token::Column::AccessTokenHash.eq(&hash))
                .add(token::Column::RefreshTokenHash.eq(&hash)),
        )
        .one(&*state.database)
        .await?;
    if let Some(token) = token {
        let family = token.token_family_id.ok_or_else(ApiError::internal)?;
        let tx = state.database.begin().await?;
        database::lock(&tx, &format!("family:{family}")).await?;
        revoke_family(&tx, family).await?;
        tx.commit().await?;
    }
    Ok(())
}
