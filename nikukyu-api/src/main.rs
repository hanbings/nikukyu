mod controller;
mod dto;
mod middleware;
mod service;

mod config;
mod database;
mod error;
mod log;
mod routers;
mod state;

use actix_web::{App, HttpServer, web};
use tracing_actix_web::TracingLogger;

use crate::database::Database;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let config = config::Config::load().map_err(std::io::Error::other)?;
    log::init_tracing(&config.log_level);

    let database = Database::connect(&config.database)
        .await
        .map_err(std::io::Error::other)?;
    tracing::info!("database connection established");

    let bind_address = (config.server.host.clone(), config.server.port);
    let workers = config.server.workers;
    let max_connections = config.server.max_connections;
    let signing_key = service::crypto::SigningKey::load(&config.oidc.signing_key_file)
        .map_err(|error| std::io::Error::other(error.to_string()))?;
    let dummy_password_hash = service::crypto::password_hash(service::crypto::secret())
        .await
        .map_err(std::io::Error::other)?;
    let http = reqwest::Client::builder()
        .user_agent("nikukyu-api")
        .timeout(std::time::Duration::from_secs(15))
        .redirect(reqwest::redirect::Policy::none())
        .build()
        .map_err(std::io::Error::other)?;
    let state = web::Data::new(state::AppState {
        config,
        database,
        http,
        signing_key,
        dummy_password_hash,
        rate_limiter: Default::default(),
    });

    HttpServer::new(move || {
        let mut cors = actix_cors::Cors::default()
            .allowed_origin(&state.config.oidc.issuer)
            .allowed_origin(&state.config.frontend.origin)
            .allowed_origin_fn(|_, head| {
                head.uri.path().starts_with("/api/v1/oauth-clients/")
                    || matches!(
                        head.uri.path(),
                        "/oidc/token"
                            | "/oidc/userinfo"
                            | "/oidc/revoke"
                            | "/oidc/jwks"
                            | "/.well-known/openid-configuration"
                    )
            })
            .allowed_methods(vec!["GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"])
            .allowed_headers(vec!["Content-Type", "Authorization", "X-CSRF-Token"]);
        if state
            .config
            .cors
            .allowed_origins
            .iter()
            .any(|origin| origin == "*")
        {
            cors = cors.allow_any_origin();
        } else {
            for origin in &state.config.cors.allowed_origins {
                cors = cors.allowed_origin(origin);
            }
            cors = cors.supports_credentials();
        }
        App::new()
            .app_data(state.clone())
            .app_data(
                web::JsonConfig::default()
                    .limit(16384)
                    .error_handler(|_, _| error::ApiError::bad("Invalid JSON request body").into()),
            )
            .app_data(
                web::FormConfig::default()
                    .limit(16384)
                    .error_handler(|_, _| error::ApiError::bad("Invalid form request body").into()),
            )
            .app_data(
                web::QueryConfig::default()
                    .error_handler(|_, _| error::ApiError::bad("Invalid query parameters").into()),
            )
            .app_data(
                web::PathConfig::default().error_handler(|_, _| {
                    error::ApiError::bad("Invalid resource identifier").into()
                }),
            )
            .configure(routers::configure)
            .wrap(cors)
            .wrap(
                actix_web::middleware::DefaultHeaders::new()
                    .add(("Referrer-Policy", "no-referrer"))
                    .add(("X-Content-Type-Options", "nosniff"))
                    .add(("X-Frame-Options", "DENY")),
            )
            .wrap(TracingLogger::<log::RequestSpan>::new())
    })
    .workers(workers)
    .max_connections(max_connections)
    .bind(bind_address)?
    .run()
    .await
}
