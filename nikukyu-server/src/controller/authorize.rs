use axum::{Json, extract::Path, http::HeaderMap};
use serde::Deserialize;
use serde_json::Value;

pub async fn get_oauth_client(Path(id): Path<String>, headers: HeaderMap) -> Json<Value> {
    todo!()
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
