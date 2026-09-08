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
    pub super_token: SuperTokenConfig,
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

        Ok(self)
    }
}
