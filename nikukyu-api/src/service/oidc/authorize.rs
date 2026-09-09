use actix_web::{HttpRequest, http::StatusCode};
use chrono::{Duration, Utc};
use models::{
    account_oauth, authorization_code,
    enums::{GrantType, Scope},
    oauth_client,
};
use sea_orm::{
    ActiveEnum, ActiveModelTrait, ColumnTrait, EntityTrait, QueryFilter, Set, TransactionTrait,
};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use crate::service::{
    crypto, oauth,
    session::{self, Session},
};
use crate::{
    database,
    dto::oidc::{
        AuthorizationContextRequest, AuthorizationContextResponse, AuthorizationDecision,
        AuthorizationDecisionRequest, AuthorizationDecisionResponse, AuthorizationRedirect,
        AuthorizationRequest,
    },
    error::{ApiError, ApiResult},
    state::AppState,
};

impl AuthorizationRequest {
    fn prompt(&self, value: &str) -> bool {
        self.prompt
            .as_deref()
            .unwrap_or("")
            .split(' ')
            .any(|p| p == value)
    }
}

async fn trusted_client(
    state: &AppState,
    input: &AuthorizationRequest,
) -> ApiResult<(oauth_client::Model, models::oauth::Model)> {
    let id = Uuid::parse_str(&input.client_id).map_err(|_| ApiError::bad("Invalid client_id"))?;
    let (client, app) = oauth::active_client(&*state.database, id).await?;
    if !client.redirect_uris.contains(&input.redirect_uri) {
        return Err(ApiError::bad(
            "redirect_uri does not exactly match a registered URI",
        ));
    }
    // Also enforce validation for clients created outside the API.
    oauth::validate_redirect(&input.redirect_uri)?;
    Ok((client, app))
}

fn validate(client: &oauth_client::Model, input: &AuthorizationRequest) -> ApiResult<Vec<Scope>> {
    if input.response_type != "code" {
        return Err(ApiError::protocol(
            "unsupported_response_type",
            "Only authorization code flow is supported",
        ));
    }
    if !client.grant_types.contains(&GrantType::AuthorizationCode) {
        return Err(ApiError::protocol(
            "unauthorized_client",
            "Client cannot use authorization code flow",
        ));
    }
    if input.request.is_some() || input.request_uri.is_some() || input.claims.is_some() {
        return Err(ApiError::bad(
            "Request objects and the claims parameter are not supported",
        ));
    }
    if input
        .response_mode
        .as_deref()
        .is_some_and(|mode| mode != "query")
    {
        return Err(ApiError::bad("Only query response mode is supported"));
    }
    if input.state.as_ref().is_some_and(|s| s.len() > 1024)
        || input.nonce.as_ref().is_some_and(|s| s.len() > 1024)
    {
        return Err(ApiError::bad("state or nonce is too long"));
    }
    if let Some(prompt) = &input.prompt {
        let prompts = prompt.split(' ').collect::<Vec<_>>();
        if prompts
            .iter()
            .any(|p| !["none", "login", "consent", "select_account"].contains(p))
            || (prompts.contains(&"none") && prompts.len() != 1)
        {
            return Err(ApiError::bad("Invalid prompt"));
        }
    }
    let challenge = input.code_challenge.as_deref().unwrap_or("");
    if input.code_challenge_method.as_deref() != Some("S256")
        || challenge.len() != 43
        || !challenge
            .bytes()
            .all(|c| c.is_ascii_alphanumeric() || c == b'-' || c == b'_')
    {
        return Err(ApiError::bad(
            "PKCE with an S256 code_challenge is required",
        ));
    }
    let scopes = super::scopes(&input.scope)?;
    if !scopes.contains(&Scope::Openid) || scopes.iter().any(|s| !client.allowed_scopes.contains(s))
    {
        return Err(ApiError::protocol(
            "invalid_scope",
            "openid is required and all scopes must be allowed by the client",
        ));
    }
    if scopes.contains(&Scope::OfflineAccess)
        && !client.grant_types.contains(&GrantType::RefreshToken)
    {
        return Err(ApiError::protocol(
            "invalid_scope",
            "Client cannot request offline access",
        ));
    }
    Ok(scopes)
}

async fn consent_required(
    state: &AppState,
    session: &Session,
    client: &oauth_client::Model,
    input: &AuthorizationRequest,
    scopes: &[Scope],
) -> ApiResult<bool> {
    if input.prompt("consent") || scopes.contains(&Scope::OfflineAccess) {
        return Ok(true);
    }
    let grant = account_oauth::Entity::find()
        .filter(account_oauth::Column::AccountId.eq(session.account.id))
        .filter(account_oauth::Column::OauthId.eq(client.oauth_id))
        .filter(account_oauth::Column::RevokedAt.is_null())
        .one(&*state.database)
        .await?;
    Ok(!grant.is_some_and(|g| scopes.iter().all(|s| g.scope.contains(s))))
}

async fn issue_code(
    state: &AppState,
    session: &Session,
    client: &oauth_client::Model,
    input: &AuthorizationRequest,
    scopes: Vec<Scope>,
    explicit_consent: bool,
    code_id: Uuid,
) -> ApiResult<String> {
    let tx = state.database.begin().await?;
    database::lock(
        &tx,
        &format!("grant:{}:{}", session.account.id, client.oauth_id),
    )
    .await?;
    let previous = account_oauth::Entity::find()
        .filter(account_oauth::Column::AccountId.eq(session.account.id))
        .filter(account_oauth::Column::OauthId.eq(client.oauth_id))
        .filter(account_oauth::Column::RevokedAt.is_null())
        .one(&tx)
        .await?;
    let now = Utc::now().fixed_offset();
    if !explicit_consent
        && !previous
            .as_ref()
            .is_some_and(|g| scopes.iter().all(|s| g.scope.contains(s)))
    {
        return Err(ApiError::protocol(
            "consent_required",
            "Authorization changed; request consent again",
        ));
    }
    let grant = match previous {
        Some(mut previous) => {
            for scope in &scopes {
                if !previous.scope.contains(scope) {
                    previous.scope.push(*scope);
                }
            }
            let mut active: account_oauth::ActiveModel = previous.into();
            active.scope = Set(active.scope.take().unwrap_or_default());
            active.updated_at = Set(now);
            active.update(&tx).await?
        }
        None => {
            account_oauth::ActiveModel {
                id: Set(Uuid::new_v4()),
                account_id: Set(session.account.id),
                oauth_id: Set(client.oauth_id),
                scope: Set(scopes.clone()),
                created_at: Set(now),
                updated_at: Set(now),
                revoked_at: Set(None),
            }
            .insert(&tx)
            .await?
        }
    };
    let code = crypto::secret();
    authorization_code::ActiveModel {
        // A signed interaction can issue at most one authorization code, even across sessions.
        id: Set(code_id),
        account_id: Set(session.account.id),
        oauth_id: Set(client.oauth_id),
        oauth_client_id: Set(client.id),
        account_oauth_id: Set(Some(grant.id)),
        code_hash: Set(crypto::hash(&code)),
        redirect_uri: Set(input.redirect_uri.clone()),
        scope: Set(scopes),
        code_challenge: Set(input.code_challenge.clone()),
        code_challenge_method: Set(input.code_challenge_method.clone()),
        nonce: Set(input.nonce.clone()),
        auth_time: Set(session.record.created_at),
        is_consumed: Set(false),
        created_at: Set(now),
        expires_at: Set(now + Duration::minutes(5)),
        consumed_at: Set(None),
    }
    .insert(&tx)
    .await?;
    tx.commit().await?;
    redirect(state, input, "code", &code)
}

fn redirect(
    state: &AppState,
    input: &AuthorizationRequest,
    key: &str,
    value: &str,
) -> ApiResult<String> {
    let mut url =
        url::Url::parse(&input.redirect_uri).map_err(|_| ApiError::bad("Invalid redirect URI"))?;
    url.query_pairs_mut()
        .append_pair(key, value)
        .append_pair("iss", &state.config.oidc.issuer);
    if let Some(state) = &input.state {
        url.query_pairs_mut().append_pair("state", state);
    }
    Ok(url.into())
}

fn oauth_error(
    state: &AppState,
    input: &AuthorizationRequest,
    code: &str,
) -> ApiResult<AuthorizationRedirect> {
    Ok(AuthorizationRedirect {
        redirect_uri: redirect(state, input, "error", code)?,
        browser_token: None,
    })
}

fn authentication_too_old(input: &AuthorizationRequest, current: &Session) -> bool {
    input.max_age.is_some_and(|age| {
        age == 0
            || Utc::now()
                .timestamp()
                .saturating_sub(current.record.created_at.timestamp()) as u64
                > age
    })
}

#[derive(Deserialize, Serialize)]
struct AuthorizationTicket {
    iss: String,
    aud: String,
    iat: i64,
    started_at_micros: i64,
    exp: i64,
    jti: Uuid,
    browser_hash: String,
    previous_session_id: Option<Uuid>,
    require_login: bool,
    request: AuthorizationRequest,
}

fn interaction(
    state: &AppState,
    request: &HttpRequest,
    input: AuthorizationRequest,
    current: Option<&Session>,
) -> ApiResult<AuthorizationRedirect> {
    let browser = request
        .cookie(session::AUTHORIZATION_COOKIE)
        .filter(|cookie| cookie.value().len() == 43)
        .map(|cookie| cookie.value().to_owned())
        .unwrap_or_else(crypto::secret);
    let now = Utc::now();
    let ticket = AuthorizationTicket {
        iss: state.config.oidc.issuer.clone(),
        aud: "nikukyu:authorization".to_owned(),
        iat: now.timestamp(),
        started_at_micros: now.timestamp_micros(),
        exp: now.timestamp() + 600,
        jti: Uuid::new_v4(),
        browser_hash: crypto::hash(&browser),
        previous_session_id: current.map(|current| current.record.id),
        require_login: current
            .is_none_or(|current| input.prompt("login") || authentication_too_old(&input, current)),
        request: input,
    };
    let token = state.signing_key.sign_authorization(&ticket)?;
    // Fragments are not sent in HTTP requests or Referer headers. The frontend stores
    // this per-tab continuation, clears the fragment, then reads its verified context.
    let fragment = serde_urlencoded::to_string([("request_token", token)])
        .map_err(|_| ApiError::internal())?;
    Ok(AuthorizationRedirect {
        redirect_uri: format!("{}/authorize#{fragment}", state.config.frontend.origin),
        browser_token: Some(browser),
    })
}

fn read_ticket(
    state: &AppState,
    request: &HttpRequest,
    raw: &str,
) -> ApiResult<AuthorizationTicket> {
    session::check_origin(&state.config, request)?;
    let ticket: AuthorizationTicket = state
        .signing_key
        .verify_authorization(raw, &state.config.oidc.issuer)?;
    let browser = request
        .cookie(session::AUTHORIZATION_COOKIE)
        .ok_or_else(|| ApiError::bad("Authorization browser state is missing; start again"))?;
    if !crypto::equal(&ticket.browser_hash, &crypto::hash(browser.value())) {
        return Err(ApiError::forbidden(
            "Authorization request belongs to another browser",
        ));
    }
    Ok(ticket)
}

async fn current_session(state: &AppState, request: &HttpRequest) -> ApiResult<Option<Session>> {
    match session::find(state, request).await {
        Ok(current) => Ok(Some(current)),
        Err(error) if error.status == StatusCode::UNAUTHORIZED => Ok(None),
        Err(error) => Err(error),
    }
}

fn continuation_needs_login(ticket: &AuthorizationTicket, current: &Session) -> bool {
    if ticket.require_login
        && (ticket.previous_session_id == Some(current.record.id)
            || current.record.created_at.timestamp_micros() < ticket.started_at_micros)
    {
        return true;
    }
    // max_age=0 has been fulfilled by a fresh session. Non-zero age must still
    // be respected if the user leaves the consent page open before continuing.
    ticket
        .request
        .max_age
        .filter(|age| *age > 0)
        .is_some_and(|age| {
            Utc::now()
                .timestamp()
                .saturating_sub(current.record.created_at.timestamp()) as u64
                > age
        })
}

pub async fn begin(
    state: &AppState,
    request: &HttpRequest,
    input: AuthorizationRequest,
) -> ApiResult<AuthorizationRedirect> {
    // Do not redirect errors until the client and exact callback URI are trusted.
    let (client, _) = trusted_client(state, &input).await?;
    let scopes = match validate(&client, &input) {
        Ok(scopes) => scopes,
        Err(error) => return oauth_error(state, &input, error.code),
    };
    let current = current_session(state, request).await?;
    if current
        .as_ref()
        .is_none_or(|current| input.prompt("login") || authentication_too_old(&input, current))
    {
        if input.prompt("none") {
            return oauth_error(state, &input, "login_required");
        }
        return interaction(state, request, input, current.as_ref());
    }
    let current = current.ok_or_else(ApiError::unauthorized)?;
    if input.prompt("select_account")
        || consent_required(state, &current, &client, &input, &scopes).await?
    {
        if input.prompt("none") {
            return oauth_error(state, &input, "consent_required");
        }
        return interaction(state, request, input, Some(&current));
    }
    match issue_code(
        state,
        &current,
        &client,
        &input,
        scopes,
        false,
        Uuid::new_v4(),
    )
    .await
    {
        Ok(url) => Ok(AuthorizationRedirect {
            redirect_uri: url,
            browser_token: None,
        }),
        Err(error) if error.code == "consent_required" => oauth_error(state, &input, error.code),
        Err(error) => Err(error),
    }
}

pub async fn context(
    state: &AppState,
    request: &HttpRequest,
    input: AuthorizationContextRequest,
) -> ApiResult<AuthorizationContextResponse> {
    let ticket = read_ticket(state, request, &input.request_token)?;
    let (client, _) = trusted_client(state, &ticket.request).await?;
    let scopes = validate(&client, &ticket.request)?;
    let current = current_session(state, request).await?;
    Ok(AuthorizationContextResponse {
        client_id: client.id,
        scope: scopes.iter().map(ActiveEnum::to_value).collect(),
        login_required: current
            .as_ref()
            .is_none_or(|current| continuation_needs_login(&ticket, current)),
        select_account: ticket.request.prompt("select_account"),
        expires_at: ticket.exp,
    })
}

pub async fn decide(
    state: &AppState,
    request: &HttpRequest,
    input: AuthorizationDecisionRequest,
) -> ApiResult<AuthorizationDecisionResponse> {
    let ticket = read_ticket(state, request, &input.request_token)?;
    let (client, _) = trusted_client(state, &ticket.request).await?;
    let scopes = match validate(&client, &ticket.request) {
        Ok(scopes) => scopes,
        Err(error) => {
            return Ok(AuthorizationDecisionResponse {
                redirect_uri: redirect(state, &ticket.request, "error", error.code)?,
            });
        }
    };
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    if continuation_needs_login(&ticket, &current) {
        return Err(ApiError::protocol(
            "login_required",
            "Sign in again before continuing this authorization request",
        ));
    }
    let redirect_uri = match input.decision {
        AuthorizationDecision::Deny => redirect(state, &ticket.request, "error", "access_denied")?,
        AuthorizationDecision::Allow => {
            issue_code(
                state,
                &current,
                &client,
                &ticket.request,
                scopes,
                true,
                ticket.jti,
            )
            .await?
        }
    };
    Ok(AuthorizationDecisionResponse { redirect_uri })
}
