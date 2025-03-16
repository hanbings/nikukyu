use std::collections::HashMap;

#[derive(Clone)]
pub struct AppState {
    pub database: sea_orm::DatabaseConnection,
    pub oauths: HashMap<String, crate::config::config::OAuthConfig>,
}
