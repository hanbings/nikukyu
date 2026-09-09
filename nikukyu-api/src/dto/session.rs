use chrono::{DateTime, FixedOffset};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use super::account::AccountResponse;

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct Credentials {
    pub username: String,
    pub password: String,
}

#[derive(Serialize)]
pub struct SessionResponse {
    pub id: Uuid,
    pub account: AccountResponse,
    pub expires_at: DateTime<FixedOffset>,
    pub csrf_token: String,
}

/// Transport-only result: the cookie token must never be serialized into JSON.
pub struct IssuedSession {
    pub session: SessionResponse,
    pub cookie_token: String,
}

#[derive(Deserialize)]
pub struct ReturnToQuery {
    pub return_to: Option<String>,
}

#[derive(Deserialize)]
pub struct GitHubCallback {
    pub code: Option<String>,
    pub state: Option<String>,
    pub error: Option<String>,
}

#[derive(Serialize)]
pub struct GitHubAuthorizationResponse {
    pub authorization_url: String,
}

pub struct StartedGitHubLogin {
    pub authorization: GitHubAuthorizationResponse,
    pub browser_token: String,
}

pub struct CompletedLogin {
    pub redirect_uri: String,
    pub session_token: String,
}
