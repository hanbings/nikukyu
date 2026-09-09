use chrono::{DateTime, FixedOffset};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct NewApplication {
    pub name: String,
    pub description: Option<String>,
}

#[derive(Clone, Copy, Default, Deserialize, Serialize)]
#[serde(rename_all = "snake_case")]
pub enum ClientAuthMethod {
    #[default]
    ClientSecretBasic,
    ClientSecretPost,
    None,
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct NewClient {
    pub redirect_uris: Vec<String>,
    pub allowed_scopes: Vec<String>,
    pub grant_types: Vec<String>,
    #[serde(default)]
    pub token_endpoint_auth_method: ClientAuthMethod,
}

#[derive(Serialize)]
pub struct ApplicationResponse {
    pub id: Uuid,
    pub name: String,
    pub description: Option<String>,
    pub avatar: Option<String>,
    pub created_at: DateTime<FixedOffset>,
}

/// Public display metadata only, without ownership, credentials or management fields.
#[derive(Serialize)]
pub struct PublicClientResponse {
    pub client_id: Uuid,
    pub oauth_id: Uuid,
    pub name: String,
    pub description: Option<String>,
    pub avatar: Option<String>,
    pub terms_of_service: Option<String>,
    pub privacy_policy: Option<String>,
}

#[derive(Serialize)]
pub struct ClientResponse {
    pub client_id: Uuid,
    pub oauth_id: Uuid,
    pub redirect_uris: Vec<String>,
    pub allowed_scopes: Vec<String>,
    pub grant_types: Vec<String>,
    pub token_endpoint_auth_method: ClientAuthMethod,
    pub created_at: DateTime<FixedOffset>,
}

#[derive(Serialize)]
pub struct CreatedClientResponse {
    #[serde(flatten)]
    pub client: ClientResponse,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub client_secret: Option<String>,
}
