use axum::{
    Router,
    routing::{delete, get, post, put},
};
use config::config::{Config, OAuthConfig};
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

    let mut oauths: HashMap<String, OAuthConfig> = HashMap::new();
    config.oauths.iter().for_each(|oauth| {
        oauths.insert(oauth.provider.clone(), oauth.clone());
    });

    info!("OAuths: {:#?}", config);
    info!("OAuths: {:#?}", oauths);

    let database = Database::connect(config.db_url.clone()).await?;

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
        .route(
            "/login",
            get(controller::login::get_login_with_oauth_authorize),
        )
        // login
        .route(
            "/login/oauth/{provider}/authorize",
            get(controller::login::get_login_with_oauth_authorize),
        )
        .route(
            "/login/oauth/{provider}/callback",
            post(controller::login::post_login_with_oauth_callback),
        )
        // account
        .route("/account", get(controller::account::get_account))
        .route("/account/{id}", get(controller::account::get_account))
        .route("/account/{id}", put(controller::account::update_account))
        .route("/account/{id}", delete(controller::account::delete_account))
        // state
        .with_state(state::AppState { database, oauths });

    axum::serve(listener, router.into_make_service()).await?;

    Ok(())
}
