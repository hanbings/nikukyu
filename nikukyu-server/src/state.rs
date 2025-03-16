use std::collections::HashMap;

#[derive(Clone)]
pub struct AppState {
    pub config: crate::config::config::Config,
    pub oauths: HashMap<String, crate::config::config::OAuthConfig>,
}
