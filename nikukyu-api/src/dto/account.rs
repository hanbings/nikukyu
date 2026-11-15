use chrono::{DateTime, FixedOffset};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use super::Patch;

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct RegisterAccountRequest {
    pub username: String,
    pub password: String,
    pub nickname: Option<String>,
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct UpdateAccountRequest {
    #[serde(default)]
    pub nickname: Patch<String>,
    #[serde(default)]
    pub avatar: Patch<String>,
    #[serde(default)]
    pub background: Patch<String>,
    #[serde(default)]
    pub theme_color: Patch<String>,
}

#[derive(Serialize)]
pub struct AccountResponse {
    pub id: Uuid,
    pub username: String,
    pub nickname: Option<String>,
    pub avatar: Option<String>,
    pub background: Option<String>,
    pub theme_color: Option<String>,
}

#[derive(Serialize)]
pub struct IdentityResponse {
    pub id: Uuid,
    pub provider: String,
    pub openid: String,
    pub created_at: DateTime<FixedOffset>,
}

#[derive(Serialize)]
pub struct GrantResponse {
    pub id: Uuid,
    pub oauth_id: Uuid,
    pub application: Option<GrantApplicationResponse>,
    pub scope: Vec<String>,
    pub created_at: DateTime<FixedOffset>,
}

/// Display metadata only; credentials and application ownership remain private.
#[derive(Serialize)]
pub struct GrantApplicationResponse {
    pub name: String,
    pub description: Option<String>,
    pub avatar: Option<String>,
    pub background: Option<String>,
    pub theme_color: Option<String>,
    pub terms_of_service: Option<String>,
    pub privacy_policy: Option<String>,
    pub is_deleted: bool,
}

#[derive(Serialize)]
pub struct PublicConfigResponse {
    pub registration_enabled: bool,
    pub github_enabled: bool,
}
