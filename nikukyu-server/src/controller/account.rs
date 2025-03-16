use axum::{
    Json,
    extract::{Path, State},
    http::HeaderMap,
};
use serde::Deserialize;
use serde_json::{Value, json};

use crate::state::AppState;

pub async fn get_account(Path(id): Path<String>, headers: HeaderMap) -> Json<Value> {
    todo!()
}

pub async fn update_account(Path(id): Path<String>) -> Json<Value> {
    todo!()
}

pub async fn delete_account(Path(id): Path<String>) -> Json<Value> {
    todo!()
}
