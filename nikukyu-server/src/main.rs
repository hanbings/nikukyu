#![allow(unused)]

use axum::{
    Router, middleware,
    routing::{get, post},
};
use config::config::Config;
use figment::{
    Figment,
    providers::{Env, Format, Json, Toml},
};
use log::{error, info};
use reqwest::Method;
use sea_orm::Database;
use security::auth_middleware;
use std::{collections::HashMap, net::SocketAddr, sync::Arc};
use tokio::{net::TcpListener, sync::Mutex};
use tower::ServiceBuilder;
use tower_http::cors::{Any, CorsLayer};

mod config;
mod controller;
mod dto;
mod entity;
mod security;
mod service;
mod state;

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    dotenv::dotenv().ok();
    env_logger::init();

    let figment = Figment::new()
        .merge(Toml::file("application.toml"))
        .merge(Json::file("application.json"))
        .merge(Env::prefixed("nikukyu_"));
    let config: Config = figment.extract().unwrap_or_else(|_| {
        error!(
            "
            Unable to extract config from application.json or application.toml.
            Check application.json or set nikukyu_* environment variable.
            "
        );
        std::process::exit(1);
    });

    let database = Database::connect(config.db_url.clone()).await?;
    let tokens: Arc<Mutex<HashMap<String, crate::security::token::Token>>> =
        Arc::new(Mutex::new(HashMap::new()));
    let mut oauths_config: HashMap<String, crate::config::config::OAuthConfig> = HashMap::new();
    let oauth_authorize_states: Arc<
        Mutex<HashMap<crate::state::OAuthAuthorizeCode, crate::state::OAuthAuthorizeState>>,
    > = Arc::new(Mutex::new(HashMap::new()));
    let accounts: Arc<Mutex<HashMap<String, crate::entity::account::Model>>> =
        Arc::new(Mutex::new(HashMap::new()));
    let oauths: Arc<Mutex<HashMap<String, crate::entity::oauth::Model>>> =
        Arc::new(Mutex::new(HashMap::new()));
    let oauth_clients: Arc<Mutex<HashMap<String, crate::entity::oauth_client::Model>>> =
        Arc::new(Mutex::new(HashMap::new()));

    config.oauths.iter().for_each(|oauth| {
        oauths_config.insert(oauth.provider.clone(), oauth.clone());
    });

    info!("Config: {:?}", config);

    let addr = format!(
        "{}:{}",
        config.application_host.clone(),
        config.application_port.clone()
    );
    let addr: SocketAddr = addr.parse().unwrap();
    let listener = TcpListener::bind(addr).await?;
    let app_state = state::AppState {
        database,
        tokens,
        oauths,
        oauth_authorize_states,
        oauths_config,
        accounts,
        oauth_clients,
    };

    info!("Listening on {}", listener.local_addr().unwrap());

    let router = Router::new()
        .route("/", get(controller::index::index))
        .route("/index", get(controller::index::index))
        // account
        .route("/api/v0/account", get(controller::account::get_account))
        .route(
            "/api/v0/account/{username}",
            get(controller::account::get_account)
                .put(controller::account::update_account)
                .delete(controller::account::delete_account),
        )
        // authorize
        .route(
            "/api/v0/oauth/client/{client_id}",
            get(controller::authorize::get_oauth_client),
        )
        .route(
            "/api/v0/oauth/authorize",
            post(controller::authorize::post_oauth_authorize),
        )
        .route(
            "/api/v0/oauth/token",
            post(controller::authorize::post_oauth_token),
        )
        // login
        .route(
            "/api/v0/login",
            get(controller::login::get_login_with_oauth_authorize),
        )
        .route(
            "/api/v0/login/oauth/{provider}/authorize",
            get(controller::login::get_login_with_oauth_authorize),
        )
        .route(
            "/api/v0/login/oauth/{provider}/callback",
            post(controller::login::post_login_with_oauth_callback),
        )
        // oauth
        .route(
            "/api/v0/oauth",
            get(controller::oauth::get_oauth_list).post(controller::oauth::post_oauth),
        )
        .route(
            "/api/v0/oauth/{oauth_id}",
            get(controller::oauth::get_oauth)
                .put(controller::oauth::update_oauth)
                .delete(controller::oauth::delete_oauth),
        )
        .route(
            "/api/v0/oauth/{oauth_id}/client",
            get(controller::oauth::get_oauth_client_list)
                .post(controller::oauth::post_oauth_client),
        )
        .route(
            "/api/v0/oauth/{oauth_id}/client/{client_id}",
            get(controller::oauth::get_oauth_client).delete(controller::oauth::delete_oauth_client),
        )
        // middleware
        .layer(
            ServiceBuilder::new()
                .layer(
                    CorsLayer::new()
                        .allow_methods(vec![
                            Method::GET,
                            Method::POST,
                            Method::PUT,
                            Method::DELETE,
                            Method::OPTIONS,
                        ])
                        .allow_headers(Any)
                        .allow_origin(Any),
                )
                .layer(middleware::from_fn_with_state(
                    app_state.clone(),
                    auth_middleware,
                ))
                .into_inner(),
        )
        // state
        .with_state(app_state);

    axum::serve(listener, router.into_make_service()).await?;

    Ok(())
}
