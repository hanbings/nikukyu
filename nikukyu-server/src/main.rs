use axum::{Router, routing::get};
use std::net::SocketAddr;
use tokio::net::TcpListener;

mod config;
mod controller;
mod entity;
mod service;

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let router = Router::new()
        .route("/", get(controller::index::index))
        .route("/index", get(controller::index::index));

    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
    let listener = TcpListener::bind(addr).await?;

    axum::serve(listener, router.into_make_service()).await?;

    Ok(())
}
