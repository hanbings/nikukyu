use actix_web::HttpRequest;
use chrono::{Duration, Utc};
use models::{account, account_authorization, account_email, account_session, github_login_state};
use sea_orm::{
    ActiveModelTrait, ColumnTrait, EntityTrait, QueryFilter, Set, TransactionTrait,
    sea_query::{Expr, OnConflict},
};
use serde::Deserialize;
use uuid::Uuid;

use super::{
    account as accounts, crypto,
    session::{self, Session},
};
use crate::{
    database,
    dto::session::{
        CompletedLogin, GitHubAuthorizationResponse, GitHubCallback, ReturnToQuery,
        StartedGitHubLogin,
    },
    error::{ApiError, ApiResult},
    state::AppState,
};

async fn start_flow(
    state: &AppState,
    return_to: Option<&str>,
    binding: Option<&Session>,
) -> ApiResult<StartedGitHubLogin> {
    let config = state
        .config
        .github
        .as_ref()
        .ok_or_else(|| ApiError::forbidden("GitHub login is disabled"))?;
    let raw_state = crypto::secret();
    let browser = crypto::secret();
    let verifier = crypto::secret();
    let now = Utc::now().fixed_offset();
    github_login_state::ActiveModel {
        id: Set(Uuid::new_v4()),
        state_hash: Set(crypto::hash(&raw_state)),
        browser_hash: Set(crypto::hash(&browser)),
        code_verifier: Set(verifier.clone()),
        account_id: Set(binding.map(|s| s.account.id)),
        session_id: Set(binding.map(|s| s.record.id)),
        return_to: Set(session::return_to(return_to)?),
        created_at: Set(now),
        expires_at: Set(now + Duration::minutes(10)),
        used_at: Set(None),
    }
    .insert(&*state.database)
    .await?;
    let mut url = url::Url::parse("https://github.com/login/oauth/authorize")
        .map_err(|_| ApiError::internal())?;
    url.query_pairs_mut().extend_pairs([
        ("client_id", config.client_id.as_str()),
        (
            "redirect_uri",
            &state.config.endpoint("/api/v1/sessions/github/callback"),
        ),
        ("scope", "read:user user:email"),
        ("state", &raw_state),
        ("code_challenge", &crypto::hash(&verifier)),
        ("code_challenge_method", "S256"),
    ]);
    Ok(StartedGitHubLogin {
        authorization: GitHubAuthorizationResponse {
            authorization_url: url.into(),
        },
        browser_token: browser,
    })
}

#[derive(Deserialize)]
struct GitHubToken {
    access_token: String,
}
#[derive(Deserialize)]
struct GitHubUser {
    id: u64,
    login: String,
    name: Option<String>,
    avatar_url: Option<String>,
}
#[derive(Deserialize)]
struct GitHubEmail {
    email: String,
    primary: bool,
    verified: bool,
}

fn upstream(_: reqwest::Error) -> ApiError {
    ApiError::new(
        actix_web::http::StatusCode::BAD_GATEWAY,
        "identity_provider_error",
        "GitHub authentication failed; start again",
    )
}

async fn complete_flow(
    state: &AppState,
    request: &HttpRequest,
    code: &str,
    raw_state: &str,
) -> ApiResult<(account::Model, String)> {
    let config = state
        .config
        .github
        .as_ref()
        .ok_or_else(|| ApiError::forbidden("GitHub login is disabled"))?;
    if raw_state.len() != 43 || code.is_empty() || code.len() > 1024 {
        return Err(ApiError::bad("Invalid GitHub callback"));
    }
    let browser = request
        .cookie(session::BROWSER_COOKIE)
        .ok_or_else(|| ApiError::bad("GitHub browser state is missing"))?;
    let flow = github_login_state::Entity::find()
        .filter(github_login_state::Column::StateHash.eq(crypto::hash(raw_state)))
        .filter(github_login_state::Column::BrowserHash.eq(crypto::hash(browser.value())))
        .filter(github_login_state::Column::ExpiresAt.gt(Utc::now().fixed_offset()))
        .filter(github_login_state::Column::UsedAt.is_null())
        .one(&*state.database)
        .await?
        .ok_or_else(|| ApiError::bad("Invalid or expired GitHub state"))?;
    if let Some(account_id) = flow.account_id {
        let current = session::find(state, request).await?;
        if current.account.id != account_id || Some(current.record.id) != flow.session_id {
            return Err(ApiError::forbidden("The binding session has changed"));
        }
    }
    let consumed = github_login_state::Entity::update_many()
        .col_expr(
            github_login_state::Column::UsedAt,
            Expr::value(Utc::now().fixed_offset()),
        )
        .filter(github_login_state::Column::Id.eq(flow.id))
        .filter(github_login_state::Column::UsedAt.is_null())
        .exec(&*state.database)
        .await?;
    if consumed.rows_affected != 1 {
        return Err(ApiError::bad("GitHub state has already been used"));
    }

    let token: GitHubToken = state
        .http
        .post("https://github.com/login/oauth/access_token")
        .header("Accept", "application/json")
        .form(&[
            ("client_id", config.client_id.as_str()),
            ("client_secret", config.client_secret.as_str()),
            ("code", code),
            (
                "redirect_uri",
                &state.config.endpoint("/api/v1/sessions/github/callback"),
            ),
            ("code_verifier", &flow.code_verifier),
        ])
        .send()
        .await
        .map_err(upstream)?
        .error_for_status()
        .map_err(upstream)?
        .json()
        .await
        .map_err(upstream)?;
    let user: GitHubUser = state
        .http
        .get("https://api.github.com/user")
        .bearer_auth(&token.access_token)
        .header("Accept", "application/vnd.github+json")
        .header("X-GitHub-Api-Version", "2022-11-28")
        .send()
        .await
        .map_err(upstream)?
        .error_for_status()
        .map_err(upstream)?
        .json()
        .await
        .map_err(upstream)?;
    let emails: Vec<GitHubEmail> = state
        .http
        .get("https://api.github.com/user/emails")
        .bearer_auth(&token.access_token)
        .header("Accept", "application/vnd.github+json")
        .header("X-GitHub-Api-Version", "2022-11-28")
        .send()
        .await
        .map_err(upstream)?
        .error_for_status()
        .map_err(upstream)?
        .json()
        .await
        .map_err(upstream)?;

    let tx = state.database.begin().await?;
    database::lock(&tx, &format!("github:{}", user.id)).await?;
    if let Some(account_id) = flow.account_id {
        // The original session must still be valid after the provider round trip.
        account_session::Entity::find_by_id(flow.session_id.ok_or_else(ApiError::unauthorized)?)
            .filter(account_session::Column::AccountId.eq(account_id))
            .filter(account_session::Column::RevokedAt.is_null())
            .filter(account_session::Column::ExpiresAt.gt(Utc::now().fixed_offset()))
            .one(&tx)
            .await?
            .ok_or_else(ApiError::unauthorized)?;
    }
    let identity = account_authorization::Entity::find()
        .filter(account_authorization::Column::Provider.eq("github"))
        .filter(account_authorization::Column::Openid.eq(user.id.to_string()))
        .filter(account_authorization::Column::DeletedAt.is_null())
        .one(&tx)
        .await?;
    if let Some(identity) = identity {
        if flow.account_id.is_some_and(|id| id != identity.account_id) {
            return Err(ApiError::forbidden(
                "This GitHub identity is already bound to another account",
            ));
        }
        let account = accounts::active(&tx, identity.account_id).await?;
        tx.commit().await?;
        return Ok((account, flow.return_to));
    }
    let account = match flow.account_id {
        Some(id) => accounts::active(&tx, id).await?,
        None => {
            if !state.config.auth.allow_registration {
                return Err(ApiError::forbidden("Registration is disabled"));
            }
            let username = format!(
                "gh_{}_{}",
                user.id,
                &Uuid::new_v4().simple().to_string()[..8]
            );
            accounts::create(
                &tx,
                username,
                Some(user.name.unwrap_or(user.login).chars().take(128).collect()),
                user.avatar_url,
            )
            .await?
        }
    };
    let now = Utc::now().fixed_offset();
    account_authorization::ActiveModel {
        id: Set(Uuid::new_v4()),
        account_id: Set(account.id),
        provider: Set("github".to_owned()),
        openid: Set(user.id.to_string()),
        created_at: Set(now),
        updated_at: Set(now),
        deleted_at: Set(None),
    }
    .insert(&tx)
    .await?;
    if flow.account_id.is_none()
        && let Some(email) = emails
            .into_iter()
            .find(|e| e.primary && e.verified && e.email.len() <= 320)
    {
        // An email collision never merges identities or changes ownership.
        account_email::Entity::insert(account_email::ActiveModel {
            id: Set(Uuid::new_v4()),
            account_id: Set(account.id),
            email: Set(email.email.to_lowercase()),
            is_primary: Set(true),
            verified_at: Set(Some(now)),
            is_deleted: Set(false),
            created_at: Set(now),
            updated_at: Set(now),
            deleted_at: Set(None),
        })
        .on_conflict(OnConflict::new().do_nothing().to_owned())
        .exec_without_returning(&tx)
        .await?;
    }
    tx.commit().await?;
    Ok((account, flow.return_to))
}

pub async fn start(
    state: &AppState,
    request: &HttpRequest,
    input: ReturnToQuery,
) -> ApiResult<StartedGitHubLogin> {
    state
        .rate_limiter
        .check(request, state.config.auth.login_attempts_per_minute)?;
    start_flow(state, input.return_to.as_deref(), None).await
}

pub async fn bind(state: &AppState, request: &HttpRequest) -> ApiResult<StartedGitHubLogin> {
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    session::recent(&current)?;
    start_flow(state, None, Some(&current)).await
}

pub async fn complete(
    state: &AppState,
    request: &HttpRequest,
    input: GitHubCallback,
) -> ApiResult<CompletedLogin> {
    if input.error.is_some() {
        return Err(ApiError::forbidden("GitHub authorization was denied"));
    }
    let (account, return_to) = complete_flow(
        state,
        request,
        input
            .code
            .as_deref()
            .ok_or_else(|| ApiError::bad("Missing GitHub code"))?,
        input
            .state
            .as_deref()
            .ok_or_else(|| ApiError::bad("Missing GitHub state"))?,
    )
    .await?;
    let return_to = session::return_to(Some(&return_to))?;
    let current = session::create(state, account).await?;
    Ok(CompletedLogin {
        redirect_uri: format!("{}{return_to}", state.config.frontend.origin),
        session_token: current.secret,
    })
}
