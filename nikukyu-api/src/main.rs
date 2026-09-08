mod controller;
mod middleware;
mod service;

mod config;
mod database;
mod log;
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
    let state = web::Data::new(state::AppState { config, database });

    HttpServer::new(move || {
        App::new()
            .app_data(state.clone())
            .wrap(TracingLogger::default())
    })
    .workers(workers)
    .max_connections(max_connections)
    .bind(bind_address)?
    .run()
    .await
}
