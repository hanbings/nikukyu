pub struct AppState {
    pub config: crate::config::Config,
    pub database: crate::database::Database,
    pub http: reqwest::Client,
    pub signing_key: crate::service::crypto::SigningKey,
    pub dummy_password_hash: String,
    pub rate_limiter: crate::service::rate_limit::RateLimiter,
}
