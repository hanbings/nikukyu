use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct Config {
    pub db_url: String,
    pub db_pool_size: u32,
    pub application_host: String,
    pub application_port: u16,
    pub application_url: Vec<String>,
    pub application_name: String,
    pub oauths: Vec<OAuthConfig>,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct OAuthConfig {
    pub provider: String,
    pub client_id: String,
    pub client_secret: String,
    pub redirect_uri: String,
}
