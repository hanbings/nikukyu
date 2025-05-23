use axum::{
    Extension, Json,
    extract::{Path, State},
    http::HeaderMap,
};
use log::info;
use serde::Deserialize;
use serde_json::{Value, json};

use crate::{security::extension::ActiveToken, state::AppState};

pub async fn get_oauth_client(
    Path(id): Path<String>,
    Extension(active_token): Extension<ActiveToken>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    info!("token: {:?}", active_token);

    Json(json!({}))
}

#[derive(Deserialize)]
pub struct OAuthAuthorize {
    client_id: String,
    redirect_uri: String,
    response_type: String,
    scope: String,
    state: String,
}

pub async fn post_oauth_authorize(callback: Json<OAuthAuthorize>) -> Json<Value> {
    todo!()
}

#[derive(Deserialize)]
pub struct OAuthToken {
    code: String,
    state: String,
    client_id: String,
    client_secret: String,
}

pub async fn post_oauth_token(callback: Json<OAuthToken>) -> Json<Value> {
    todo!()
}
