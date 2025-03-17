use axum::{
    Router,
    routing::{get, post},
};
use config::config::Config;
use figment::{
    Figment,
    providers::{Env, Format, Json, Toml},
};
use log::{error, info};
use sea_orm::Database;
use std::{collections::HashMap, net::SocketAddr};
use tokio::net::TcpListener;

mod config;
mod controller;
mod entity;
mod service;
mod state;
mod token;

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
    let tokens: HashMap<String, crate::token::Token> = HashMap::new();
    let mut oauths_config: HashMap<String, crate::config::config::OAuthConfig> = HashMap::new();
    let oauth_authorize_states: HashMap<
        crate::state::OAuthAuthorizeCode,
        crate::state::OAuthAuthorizeState,
    > = HashMap::new();
    let accounts: HashMap<String, crate::entity::account::Model> = HashMap::new();
    let oauths: HashMap<String, crate::entity::oauth::Model> = HashMap::new();
    let oauth_clients: HashMap<String, crate::entity::oauth_client::Model> = HashMap::new();

    config.oauths.iter().for_each(|oauth| {
        oauths_config.insert(oauth.provider.clone(), oauth.clone());
    });

    let addr = format!(
        "{}:{}",
        config.application_host.clone(),
        config.application_port.clone()
    );
    let addr: SocketAddr = addr.parse().unwrap();
    let listener = TcpListener::bind(addr).await?;

    info!("Listening on {}", listener.local_addr().unwrap());

    let router = Router::new()
        .route("/", get(controller::index::index))
        .route("/index", get(controller::index::index))
        // account
        .route("/api/v0/account", get(controller::account::get_account))
        .route(
            "/api/v0/account/{id}",
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
        // state
        .with_state(state::AppState {
            database,
            tokens,
            oauths,
            oauth_authorize_states,
            oauths_config,
            accounts,
            oauth_clients,
        });

    axum::serve(listener, router.into_make_service()).await?;

    Ok(())
}
