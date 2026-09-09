use actix_web::{
    HttpRequest,
    cookie::{Cookie, SameSite, time::Duration as CookieDuration},
    http::header,
};
use chrono::{Duration, Utc};
use models::{account, account_session};
use sea_orm::{ActiveModelTrait, ColumnTrait, ConnectionTrait, EntityTrait, QueryFilter, Set};
use uuid::Uuid;

use super::{account as accounts, crypto};
use crate::{
    config::Config,
    dto::session::{Credentials, IssuedSession, SessionResponse},
    error::{ApiError, ApiResult},
    state::AppState,
};

pub const SESSION_COOKIE: &str = "nikukyu_session";
pub const BROWSER_COOKIE: &str = "nikukyu_browser";
pub const AUTHORIZATION_COOKIE: &str = "nikukyu_authorization";

pub(super) struct Session {
    pub account: account::Model,
    pub record: account_session::Model,
    pub secret: String,
}

pub fn cookie(config: &Config, name: &'static str, value: String, seconds: i64) -> Cookie<'static> {
    Cookie::build(name, value)
        .path("/")
        .http_only(true)
        .secure(config.secure_cookies())
        .same_site(SameSite::Lax)
        .max_age(CookieDuration::seconds(seconds))
        .finish()
}

pub(super) async fn create(state: &AppState, account: account::Model) -> ApiResult<Session> {
    let secret = crypto::secret();
    let now = Utc::now().fixed_offset();
    let record = account_session::ActiveModel {
        id: Set(Uuid::new_v4()),
        account_id: Set(account.id),
        secret_hash: Set(crypto::hash(&secret)),
        created_at: Set(now),
        expires_at: Set(now + Duration::seconds(state.config.auth.session_ttl_seconds)),
        revoked_at: Set(None),
    }
    .insert(&*state.database)
    .await?;
    account::Entity::update_many()
        .col_expr(
            account::Column::LastLoginAt,
            sea_orm::sea_query::Expr::value(now),
        )
        .filter(account::Column::Id.eq(account.id))
        .exec(&*state.database)
        .await?;
    Ok(Session {
        account,
        record,
        secret,
    })
}

pub(super) async fn find(state: &AppState, request: &HttpRequest) -> ApiResult<Session> {
    let cookie = request
        .cookie(SESSION_COOKIE)
        .ok_or_else(ApiError::unauthorized)?;
    if cookie.value().len() != 43 {
        return Err(ApiError::unauthorized());
    }
    let record = account_session::Entity::find()
        .filter(account_session::Column::SecretHash.eq(crypto::hash(cookie.value())))
        .filter(account_session::Column::RevokedAt.is_null())
        .filter(account_session::Column::ExpiresAt.gt(Utc::now().fixed_offset()))
        .one(&*state.database)
        .await?
        .ok_or_else(ApiError::unauthorized)?;
    let account = accounts::active(&*state.database, record.account_id).await?;
    Ok(Session {
        account,
        record,
        secret: cookie.value().to_owned(),
    })
}

pub(super) fn check_origin(config: &Config, request: &HttpRequest) -> ApiResult<()> {
    if let Some(origin) = request.headers().get(header::ORIGIN) {
        let origin = origin
            .to_str()
            .map_err(|_| ApiError::forbidden("Invalid origin"))?;
        if origin != config.oidc.issuer
            && origin != config.frontend.origin
            && !config
                .cors
                .allowed_origins
                .iter()
                .any(|o| o == origin && o != "*")
        {
            return Err(ApiError::forbidden("Origin is not allowed"));
        }
    }
    Ok(())
}

pub(super) fn check_csrf(
    config: &Config,
    request: &HttpRequest,
    session: &Session,
) -> ApiResult<()> {
    check_origin(config, request)?;
    let token = request
        .headers()
        .get("X-CSRF-Token")
        .and_then(|v| v.to_str().ok());
    if !token.is_some_and(|token| crypto::equal(token, &crypto::csrf(&session.secret))) {
        return Err(ApiError::forbidden("Invalid CSRF token"));
    }
    Ok(())
}

pub(super) fn recent(session: &Session) -> ApiResult<()> {
    if session.record.created_at < Utc::now().fixed_offset() - Duration::minutes(5) {
        return Err(ApiError::forbidden(
            "Sign in again before changing authentication methods",
        ));
    }
    Ok(())
}

pub(super) fn view(session: &Session) -> SessionResponse {
    SessionResponse {
        id: session.record.id,
        account: accounts::view(session.account.clone()),
        expires_at: session.record.expires_at,
        csrf_token: crypto::csrf(&session.secret),
    }
}

pub(super) async fn revoke(db: &impl ConnectionTrait, id: Uuid) -> ApiResult<()> {
    account_session::Entity::update_many()
        .col_expr(
            account_session::Column::RevokedAt,
            sea_orm::sea_query::Expr::value(Utc::now().fixed_offset()),
        )
        .filter(account_session::Column::Id.eq(id))
        .exec(db)
        .await?;
    Ok(())
}

pub(super) fn return_to(value: Option<&str>) -> ApiResult<String> {
    match value {
        None | Some("/") => Ok("/".to_owned()),
        Some(value)
            if (value == "/authorize" || value.starts_with("/authorize?"))
                && value.len() <= 8192
                && !value.chars().any(char::is_control)
                && !value.contains('\\')
                && !value.contains('#') =>
        {
            Ok(value.to_owned())
        }
        _ => Err(ApiError::bad("Invalid return path")),
    }
}

pub async fn login(
    state: &AppState,
    request: &HttpRequest,
    input: Credentials,
) -> ApiResult<IssuedSession> {
    check_origin(&state.config, request)?;
    state
        .rate_limiter
        .check(request, state.config.auth.login_attempts_per_minute)?;
    let account = accounts::login(state, input.username, input.password).await?;
    let current = create(state, account).await?;
    Ok(IssuedSession {
        session: view(&current),
        cookie_token: current.secret,
    })
}

pub async fn current(state: &AppState, request: &HttpRequest) -> ApiResult<SessionResponse> {
    Ok(view(&find(state, request).await?))
}

pub async fn destroy(state: &AppState, request: &HttpRequest) -> ApiResult<()> {
    let current = find(state, request).await?;
    check_csrf(&state.config, request, &current)?;
    revoke(&*state.database, current.record.id).await
}
