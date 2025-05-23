use serde::{Deserialize, Serialize};

use crate::{entity::account::Model, security::token::Token};

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct Config {
    pub db_url: String,
    pub db_pool_size: u32,
    pub application_host: String,
    pub application_port: u16,
    pub application_url: Vec<String>,
    pub application_name: String,
    pub oauths: Vec<OAuthConfig>,
    pub debugs: Vec<DebugConfig>,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct OAuthConfig {
    pub provider: String,
    pub client_id: String,
    pub client_secret: String,
    pub redirect_uri: String,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct DebugConfig {
    pub debug: bool,
    pub token: Token,
    pub account: Model,
}