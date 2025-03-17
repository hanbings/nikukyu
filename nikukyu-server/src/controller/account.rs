use axum::{Json, extract::Path, http::HeaderMap};
use serde_json::Value;

pub async fn get_account(Path(id): Path<String>, headers: HeaderMap) -> Json<Value> {
    todo!()
}

pub async fn update_account(Path(id): Path<String>) -> Json<Value> {
    todo!()
}

pub async fn delete_account(Path(id): Path<String>) -> Json<Value> {
    todo!()
}
