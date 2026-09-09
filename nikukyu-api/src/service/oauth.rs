use actix_web::HttpRequest;
use chrono::Utc;
use models::{
    enums::{ClientAuthMethod, GrantType, Scope},
    oauth, oauth_client,
};
use sea_orm::{
    ActiveEnum, ActiveModelTrait, ColumnTrait, ConnectionTrait, EntityTrait, QueryFilter,
    QueryOrder, QuerySelect, Set, sea_query::Expr,
};
use uuid::Uuid;

use super::{account, crypto, session};
use crate::{
    dto::{
        ListResponse,
        oauth::{
            ApplicationResponse, ClientAuthMethod as ApiClientAuthMethod, ClientResponse,
            CreatedClientResponse, NewApplication, NewClient, PublicClientResponse,
        },
    },
    error::{ApiError, ApiResult},
    state::AppState,
};

pub(super) const OIDC_SCOPES: [Scope; 4] = [
    Scope::Openid,
    Scope::Profile,
    Scope::Email,
    Scope::OfflineAccess,
];

fn application_view(app: oauth::Model) -> ApplicationResponse {
    ApplicationResponse {
        id: app.id,
        name: app.name,
        description: app.description,
        avatar: app.avatar,
        created_at: app.created_at,
    }
}

fn client_view(client: oauth_client::Model) -> ClientResponse {
    ClientResponse {
        client_id: client.id,
        oauth_id: client.oauth_id,
        redirect_uris: client.redirect_uris,
        allowed_scopes: client
            .allowed_scopes
            .iter()
            .map(ActiveEnum::to_value)
            .collect(),
        grant_types: client
            .grant_types
            .iter()
            .map(ActiveEnum::to_value)
            .collect(),
        token_endpoint_auth_method: match client.token_endpoint_auth_method {
            ClientAuthMethod::ClientSecretBasic => ApiClientAuthMethod::ClientSecretBasic,
            ClientAuthMethod::ClientSecretPost => ApiClientAuthMethod::ClientSecretPost,
            ClientAuthMethod::None => ApiClientAuthMethod::None,
        },
        created_at: client.created_at,
    }
}

async fn owned(db: &impl ConnectionTrait, id: Uuid, owner: Uuid) -> ApiResult<oauth::Model> {
    oauth::Entity::find_by_id(id)
        .filter(oauth::Column::AccountId.eq(owner))
        .filter(oauth::Column::IsDeleted.eq(false))
        .one(db)
        .await?
        .ok_or_else(ApiError::missing)
}

pub(super) async fn active_client(
    db: &impl ConnectionTrait,
    id: Uuid,
) -> ApiResult<(oauth_client::Model, oauth::Model)> {
    let invalid = || ApiError::protocol("invalid_client", "Unknown or disabled client");
    let client = oauth_client::Entity::find_by_id(id)
        .filter(oauth_client::Column::IsDeleted.eq(false))
        .one(db)
        .await?
        .ok_or_else(invalid)?;
    let app = oauth::Entity::find_by_id(client.oauth_id)
        .filter(oauth::Column::IsDeleted.eq(false))
        .one(db)
        .await?
        .ok_or_else(invalid)?;
    account::active(db, app.account_id).await.map_err(|error| {
        if error.status.is_server_error() {
            error
        } else {
            invalid()
        }
    })?;
    Ok((client, app))
}

pub async fn create_application(
    state: &AppState,
    request: &HttpRequest,
    input: NewApplication,
) -> ApiResult<ApplicationResponse> {
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    if input.name.trim().is_empty()
        || input.name.chars().count() > 128
        || input.description.as_ref().is_some_and(|s| s.len() > 4096)
    {
        return Err(ApiError::bad("Invalid application name or description"));
    }
    let now = Utc::now().fixed_offset();
    let app = oauth::ActiveModel {
        id: Set(Uuid::new_v4()),
        account_id: Set(current.account.id),
        name: Set(input.name),
        description: Set(input.description),
        background: Set(None),
        theme_color: Set(None),
        avatar: Set(None),
        email: Set(None),
        terms_of_service: Set(None),
        privacy_policy: Set(None),
        is_deleted: Set(false),
        created_at: Set(now),
        updated_at: Set(now),
        deleted_at: Set(None),
    }
    .insert(&*state.database)
    .await?;
    Ok(application_view(app))
}

pub(super) fn validate_redirect(value: &str) -> ApiResult<()> {
    let url = url::Url::parse(value).map_err(|_| ApiError::bad("Invalid redirect URI"))?;
    let local = matches!(url.host_str(), Some("localhost" | "127.0.0.1" | "[::1]"));
    if value.len() > 2048
        || value.contains('*')
        || url.fragment().is_some()
        || url.host_str().is_none()
        || !url.username().is_empty()
        || url.password().is_some()
        || !(url.scheme() == "https" || (url.scheme() == "http" && local))
    {
        return Err(ApiError::bad(
            "Redirect URIs must use HTTPS (HTTP allowed for localhost), without fragments or wildcards",
        ));
    }
    Ok(())
}

pub async fn create_client(
    state: &AppState,
    request: &HttpRequest,
    app_id: Uuid,
    input: NewClient,
) -> ApiResult<CreatedClientResponse> {
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    owned(&*state.database, app_id, current.account.id).await?;
    let allowed_scopes = input
        .allowed_scopes
        .iter()
        .map(|raw| Scope::try_from_value(raw).map_err(|_| ApiError::bad("Unknown scope")))
        .collect::<ApiResult<Vec<_>>>()?;
    let grant_types = input
        .grant_types
        .iter()
        .map(|raw| GrantType::try_from_value(raw).map_err(|_| ApiError::bad("Unknown grant type")))
        .collect::<ApiResult<Vec<_>>>()?;
    let auth_method = match input.token_endpoint_auth_method {
        ApiClientAuthMethod::ClientSecretBasic => ClientAuthMethod::ClientSecretBasic,
        ApiClientAuthMethod::ClientSecretPost => ClientAuthMethod::ClientSecretPost,
        ApiClientAuthMethod::None => ClientAuthMethod::None,
    };
    if input.redirect_uris.is_empty() || input.redirect_uris.len() > 20 {
        return Err(ApiError::bad("Provide between 1 and 20 redirect URIs"));
    }
    for uri in &input.redirect_uris {
        validate_redirect(uri)?;
    }
    if !allowed_scopes.contains(&Scope::Openid)
        || allowed_scopes.len() > 4
        || allowed_scopes.iter().any(|s| !OIDC_SCOPES.contains(s))
    {
        return Err(ApiError::bad(
            "Client scopes must include openid and may include profile, email and offline_access",
        ));
    }
    if !grant_types.contains(&GrantType::AuthorizationCode)
        || grant_types.len() > 2
        || grant_types.contains(&GrantType::ClientCredentials)
        || (allowed_scopes.contains(&Scope::OfflineAccess)
            && !grant_types.contains(&GrantType::RefreshToken))
    {
        return Err(ApiError::bad(
            "Supported grants are authorization_code and refresh_token; offline_access requires refresh_token",
        ));
    }
    let secret = (auth_method != ClientAuthMethod::None).then(crypto::secret);
    let now = Utc::now().fixed_offset();
    let client = oauth_client::ActiveModel {
        id: Set(Uuid::new_v4()),
        oauth_id: Set(app_id),
        client_secret_hash: Set(secret.as_deref().map(crypto::hash)),
        token_endpoint_auth_method: Set(auth_method),
        grant_types: Set(grant_types),
        redirect_uris: Set(input.redirect_uris),
        allowed_scopes: Set(allowed_scopes),
        is_deleted: Set(false),
        created_at: Set(now),
        updated_at: Set(now),
        deleted_at: Set(None),
    }
    .insert(&*state.database)
    .await?;
    Ok(CreatedClientResponse {
        client: client_view(client),
        client_secret: secret,
    })
}

pub async fn list(
    state: &AppState,
    request: &HttpRequest,
) -> ApiResult<ListResponse<ApplicationResponse>> {
    let current = session::find(state, request).await?;
    let apps = oauth::Entity::find()
        .filter(oauth::Column::AccountId.eq(current.account.id))
        .filter(oauth::Column::IsDeleted.eq(false))
        .order_by_desc(oauth::Column::CreatedAt)
        .limit(100)
        .all(&*state.database)
        .await?;
    Ok(ListResponse {
        items: apps.into_iter().map(application_view).collect(),
    })
}

pub async fn get(
    state: &AppState,
    request: &HttpRequest,
    id: Uuid,
) -> ApiResult<ApplicationResponse> {
    let current = session::find(state, request).await?;
    Ok(application_view(
        owned(&*state.database, id, current.account.id).await?,
    ))
}

pub async fn destroy(state: &AppState, request: &HttpRequest, id: Uuid) -> ApiResult<()> {
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    owned(&*state.database, id, current.account.id).await?;
    let now = Utc::now().fixed_offset();
    oauth::Entity::update_many()
        .col_expr(oauth::Column::IsDeleted, Expr::value(true))
        .col_expr(oauth::Column::DeletedAt, Expr::value(now))
        .col_expr(oauth::Column::UpdatedAt, Expr::value(now))
        .filter(oauth::Column::Id.eq(id))
        .exec(&*state.database)
        .await?;
    Ok(())
}

pub async fn clients(
    state: &AppState,
    request: &HttpRequest,
    id: Uuid,
) -> ApiResult<ListResponse<ClientResponse>> {
    let current = session::find(state, request).await?;
    owned(&*state.database, id, current.account.id).await?;
    let clients = oauth_client::Entity::find()
        .filter(oauth_client::Column::OauthId.eq(id))
        .filter(oauth_client::Column::IsDeleted.eq(false))
        .limit(100)
        .all(&*state.database)
        .await?;
    Ok(ListResponse {
        items: clients.into_iter().map(client_view).collect(),
    })
}

pub async fn destroy_client(
    state: &AppState,
    request: &HttpRequest,
    app_id: Uuid,
    client_id: Uuid,
) -> ApiResult<()> {
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    owned(&*state.database, app_id, current.account.id).await?;
    let now = Utc::now().fixed_offset();
    let result = oauth_client::Entity::update_many()
        .col_expr(oauth_client::Column::IsDeleted, Expr::value(true))
        .col_expr(oauth_client::Column::DeletedAt, Expr::value(now))
        .col_expr(oauth_client::Column::UpdatedAt, Expr::value(now))
        .filter(oauth_client::Column::Id.eq(client_id))
        .filter(oauth_client::Column::OauthId.eq(app_id))
        .exec(&*state.database)
        .await?;
    if result.rows_affected == 0 {
        return Err(ApiError::missing());
    }
    Ok(())
}

pub async fn public_client(state: &AppState, id: Uuid) -> ApiResult<PublicClientResponse> {
    let (client, app) = active_client(&*state.database, id).await.map_err(|error| {
        if error.status.is_server_error() {
            error
        } else {
            ApiError::missing()
        }
    })?;
    Ok(PublicClientResponse {
        client_id: client.id,
        oauth_id: app.id,
        name: app.name,
        description: app.description,
        avatar: app.avatar,
        terms_of_service: app.terms_of_service,
        privacy_policy: app.privacy_policy,
    })
}
