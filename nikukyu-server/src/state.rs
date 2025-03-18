use std::{collections::HashMap, sync::Arc};

use tokio::sync::Mutex;

pub type OAuthAuthorizeCode = String;

#[derive(Clone)]
pub struct OAuthAuthorizeState {
    pub code: OAuthAuthorizeCode,
    pub state: String,
    pub access: Vec<String>,
    pub account_id: i32,
    pub oauth_id: i32,
    pub oauth_client_id: i32,
}

#[derive(Clone)]
pub struct AppState {
    // database
    pub database: sea_orm::DatabaseConnection,

    // state
    pub tokens: HashMap<String, crate::security::token::Token>,
    pub oauths_config: HashMap<String, crate::config::config::OAuthConfig>,
    pub oauth_authorize_states: Arc<Mutex<HashMap<OAuthAuthorizeCode, OAuthAuthorizeState>>>,

    // cache
    pub accounts: Arc<Mutex<HashMap<String, crate::entity::account::Model>>>,
    pub oauths: Arc<Mutex<HashMap<String, crate::entity::oauth::Model>>>,
    pub oauth_clients: Arc<Mutex<HashMap<String, crate::entity::oauth_client::Model>>>,
}
