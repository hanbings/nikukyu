use std::{env, path::Path};

use ::config::{Config as ConfigBuilder, ConfigError, Environment, File};
use serde::Deserialize;

const DEFAULT_CONFIG_PATH: &str = "config.toml";
const CONFIG_PATH_ENV: &str = "NIKUKYU_CONFIG";

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct Config {
    pub log_level: String,
    pub database: DatabaseConfig,
    pub server: ServerConfig,
    pub cors: CorsConfig,
    pub frontend: FrontendConfig,
    pub super_token: SuperTokenConfig,
    #[serde(default)]
    pub auth: AuthConfig,
    pub github: Option<GitHubConfig>,
    pub oidc: OidcConfig,
}

#[derive(Deserialize)]
#[serde(default, deny_unknown_fields)]
pub struct AuthConfig {
    pub allow_registration: bool,
    pub session_ttl_seconds: i64,
    pub login_attempts_per_minute: u32,
}

impl Default for AuthConfig {
    fn default() -> Self {
        Self {
            allow_registration: true,
            session_ttl_seconds: 86400,
            login_attempts_per_minute: 20,
        }
    }
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct GitHubConfig {
    pub client_id: String,
    pub client_secret: String,
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct OidcConfig {
    pub issuer: String,
    pub signing_key_file: String,
    #[serde(default = "access_ttl")]
    pub access_token_ttl_seconds: i64,
    #[serde(default = "refresh_ttl")]
    pub refresh_token_ttl_seconds: i64,
}

fn access_ttl() -> i64 {
    900
}
fn refresh_ttl() -> i64 {
    2_592_000
}

impl Config {
    pub fn secure_cookies(&self) -> bool {
        self.oidc.issuer.starts_with("https://")
    }
    pub fn endpoint(&self, path: &str) -> String {
        format!("{}{path}", self.oidc.issuer)
    }
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct DatabaseConfig {
    pub url: String,
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct ServerConfig {
    pub host: String,
    pub port: u16,
    pub workers: usize,
    pub max_connections: usize,
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct CorsConfig {
    pub allowed_origins: Vec<String>,
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct FrontendConfig {
    pub origin: String,
}

#[derive(Deserialize)]
#[serde(deny_unknown_fields)]
pub struct SuperTokenConfig {
    pub token: String,
}

impl Config {
    pub fn load() -> Result<Self, ConfigError> {
        let path = env::var(CONFIG_PATH_ENV).unwrap_or_else(|_| DEFAULT_CONFIG_PATH.to_owned());
        Self::load_from(path)
    }

    pub fn load_from(path: impl AsRef<Path>) -> Result<Self, ConfigError> {
        let config = ConfigBuilder::builder()
            .add_source(File::from(path.as_ref()))
            .add_source(
                Environment::with_prefix("NIKUKYU")
                    .prefix_separator("__")
                    .separator("__")
                    .list_separator(",")
                    .with_list_parse_key("cors.allowed_origins")
                    .ignore_empty(true)
                    .try_parsing(true),
            )
            .build()?
            .try_deserialize::<Self>()?;

        config.validate()
    }

    fn validate(self) -> Result<Self, ConfigError> {
        let required_values = [
            ("log_level", self.log_level.as_str()),
            ("database.url", self.database.url.as_str()),
            ("server.host", self.server.host.as_str()),
            ("super_token.token", self.super_token.token.as_str()),
        ];

        if let Some((key, _)) = required_values
            .into_iter()
            .find(|(_, value)| value.trim().is_empty())
        {
            return Err(ConfigError::Message(format!(
                "configuration value `{key}` must not be empty"
            )));
        }

        if self.server.workers == 0 {
            return Err(ConfigError::Message(
                "configuration value `server.workers` must be greater than zero".to_owned(),
            ));
        }

        if self.server.max_connections == 0 {
            return Err(ConfigError::Message(
                "configuration value `server.max_connections` must be greater than zero".to_owned(),
            ));
        }

        if self.cors.allowed_origins.is_empty() {
            return Err(ConfigError::Message(
                "configuration value `cors.allowed_origins` must not be empty".to_owned(),
            ));
        }

        validate_origin("oidc.issuer", &self.oidc.issuer)?;
        validate_origin("frontend.origin", &self.frontend.origin)?;
        if self.oidc.signing_key_file.trim().is_empty()
            || !(60..=86400).contains(&self.oidc.access_token_ttl_seconds)
            || !(60..=31_536_000).contains(&self.oidc.refresh_token_ttl_seconds)
            || !(60..=31_536_000).contains(&self.auth.session_ttl_seconds)
            || self.auth.login_attempts_per_minute == 0
        {
            return Err(ConfigError::Message(
                "invalid authentication lifetimes or signing key configuration".into(),
            ));
        }
        if self
            .github
            .as_ref()
            .is_some_and(|g| g.client_id.trim().is_empty() || g.client_secret.trim().is_empty())
        {
            return Err(ConfigError::Message(
                "GitHub client_id and client_secret must both be configured".into(),
            ));
        }

        Ok(self)
    }
}

fn validate_origin(key: &str, value: &str) -> Result<(), ConfigError> {
    let origin = url::Url::parse(value)
        .map_err(|_| ConfigError::Message(format!("{key} must be an absolute URL")))?;
    let local = matches!(origin.host_str(), Some("localhost" | "127.0.0.1" | "[::1]"));
    if !(origin.scheme() == "https" || (origin.scheme() == "http" && local))
        || origin.host_str().is_none()
        || origin.query().is_some()
        || origin.fragment().is_some()
        || !origin.username().is_empty()
        || origin.password().is_some()
        || origin.path() != "/"
        || value.ends_with('/')
        || origin.origin().ascii_serialization() != value
    {
        return Err(ConfigError::Message(format!(
            "{key} must be a canonical HTTPS origin without a trailing slash (HTTP allowed for localhost)"
        )));
    }
    Ok(())
}
