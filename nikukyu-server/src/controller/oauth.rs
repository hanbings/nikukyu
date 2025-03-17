use axum::{Json, extract::Path};
use serde::Deserialize;
use serde_json::Value;

#[derive(Deserialize)]
pub struct OAuthCreate {
    pub redirect: Option<Vec<String>>,
    pub access: Option<Vec<String>>,
    pub avatar: Option<String>,
    pub name: Option<String>,
    pub description: Option<String>,
    pub homepage: Option<String>,
    pub background: Option<String>,
    pub theme: Option<String>,
    pub policy: Option<String>,
    pub tos: Option<String>,
}

pub async fn post_oauth(Path(oauth_id): Path<String>, callback: Json<OAuthCreate>) -> Json<Value> {
    todo!()
}

pub async fn get_oauth_list(Path(oauth_id): Path<String>) -> Json<Value> {
    todo!()
}

pub async fn get_oauth(Path(oauth_id): Path<String>) -> Json<Value> {
    todo!()
}

pub async fn update_oauth(Path(oauth_id): Path<String>) -> Json<Value> {
    todo!()
}

pub async fn delete_oauth(Path(oauth_id): Path<String>) -> Json<Value> {
    todo!()
}

#[derive(Deserialize)]
pub struct OAuthClientCreate {
    pub name: Option<String>,
    pub description: Option<String>,
    pub expire: Option<String>,
}

pub async fn post_oauth_client(
    Path(oauth_id): Path<String>,
    callback: Json<OAuthClientCreate>,
) -> Json<Value> {
    todo!()
}

pub async fn get_oauth_client_list(Path(oauth_id): Path<String>) -> Json<Value> {
    todo!()
}

pub async fn get_oauth_client(
    Path(oauth_id): Path<String>,
    Path(client_id): Path<String>,
) -> Json<Value> {
    todo!()
}

pub async fn delete_oauth_client(
    Path(oauth_id): Path<String>,
    Path(client_id): Path<String>,
) -> Json<Value> {
    todo!()
}
