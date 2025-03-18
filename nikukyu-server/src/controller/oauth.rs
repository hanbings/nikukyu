use axum::{
    Json,
    extract::{Path, State},
};
use rand::{Rng, distr::Alphanumeric};
use sea_orm::ActiveValue::Set;
use serde::Deserialize;
use serde_json::Value;

use crate::state::AppState;

#[derive(Deserialize)]
pub struct CreateOAuth {
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

pub async fn post_oauth(
    State(app_state): State<AppState>,
    Json(create): Json<CreateOAuth>,
) -> Json<Value> {
    let mut oauth = crate::entity::oauth::ActiveModel {
        ..Default::default()
    };

    oauth.redirect = Set(create.redirect.unwrap_or(vec![]));
    oauth.access = Set(create.access.unwrap_or(vec![]));
    oauth.name = Set(create.name.unwrap_or(
        rand::rng()
            .sample_iter(&Alphanumeric)
            .take(16)
            .map(char::from)
            .collect::<String>(),
    ));

    create.avatar.map(|avatar| oauth.avatar = Set(Some(avatar)));
    create
        .description
        .map(|description| oauth.description = Set(Some(description)));
    create
        .homepage
        .map(|homepage| oauth.homepage = Set(Some(homepage)));
    create
        .background
        .map(|background| oauth.background = Set(Some(background)));
    create.theme.map(|theme| oauth.theme = Set(Some(theme)));
    create.policy.map(|policy| oauth.policy = Set(Some(policy)));
    create.tos.map(|tos| oauth.tos = Set(Some(tos)));

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
