use axum::{
    Json,
    extract::{Path, State},
};
use log::{error, info};
use rand::{Rng, distr::Alphanumeric};
use sea_orm::{ActiveModelTrait, ActiveValue::Set, ColumnTrait, EntityTrait, QueryFilter, ModelTrait, sqlx::types::chrono};
use serde::Deserialize;
use serde_json::{Value, json};

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

    match crate::entity::oauth::ActiveModel::insert(oauth, &app_state.database).await {
        Ok(oauth) => {
            info!("created oauth: {}", oauth.id);
            return Json(json!({"id": oauth.id}));
        }
        Err(err) => {
            error!("failed to create oauth: {}", err);
            return Json(json!({"error": "failed to create oauth"}));
        }
    }
}

pub async fn get_oauth_list(State(app_state): State<AppState>) -> Json<Value> {
    match crate::entity::oauth::Entity::find()
        .all(&app_state.database)
        .await
    {
        Ok(oauth_list) => {
            let oauth_list = oauth_list
                .into_iter()
                .map(|oauth| json!({
                    "id": oauth.id,
                    "name": oauth.name,
                    "description": oauth.description,
                    "homepage": oauth.homepage,
                    "avatar": oauth.avatar,
                    "background": oauth.background,
                    "theme": oauth.theme,
                    "policy": oauth.policy,
                    "tos": oauth.tos,
                    "redirect": oauth.redirect,
                    "access": oauth.access,
                }))
                .collect::<Vec<_>>();
            Json(json!({ "oauth_list": oauth_list }))
        }
        Err(err) => {
            error!("failed to get oauth list: {}", err);
            Json(json!({ "error": "failed to get oauth list" }))
        }
    }
}

pub async fn get_oauth(
    Path(oauth_id): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    match crate::entity::oauth::Entity::find()
        .filter(crate::entity::oauth::Column::Id.eq(oauth_id))
        .one(&app_state.database)
        .await
    {
        Ok(Some(oauth)) => {
            Json(json!({
                "oauth": {
                    "id": oauth.id,
                    "name": oauth.name,
                    "description": oauth.description,
                    "homepage": oauth.homepage,
                    "avatar": oauth.avatar,
                    "background": oauth.background,
                    "theme": oauth.theme,
                    "policy": oauth.policy,
                    "tos": oauth.tos,
                    "redirect": oauth.redirect,
                    "access": oauth.access,
                }
            }))
        }
        Ok(None) => Json(json!({ "error": "oauth not found" })),
        Err(err) => {
            error!("failed to get oauth: {}", err);
            Json(json!({ "error": "failed to get oauth" }))
        }
    }
}

#[derive(Deserialize)]
pub struct UpdateOAuth {
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

pub async fn update_oauth(
    Path(oauth_id): Path<String>,
    State(app_state): State<AppState>,
    Json(update): Json<UpdateOAuth>,
) -> Json<Value> {
    let Some(mut oauth): Option<crate::entity::oauth::ActiveModel> =
        crate::entity::oauth::Entity::find()
            .filter(crate::entity::oauth::Column::Id.eq(&oauth_id))
            .one(&app_state.database)
            .await
            .ok()
            .flatten()
            .map(|oauth| oauth.into())
    else {
        return Json(json!({ "error": "oauth not found" }));
    };

    update.redirect.map(|redirect| oauth.redirect = Set(redirect));
    update.access.map(|access| oauth.access = Set(access));
    update.name.map(|name| oauth.name = Set(name));
    update.avatar.map(|avatar| oauth.avatar = Set(Some(avatar)));
    update.description.map(|description| oauth.description = Set(Some(description)));
    update.homepage.map(|homepage| oauth.homepage = Set(Some(homepage)));
    update.background.map(|background| oauth.background = Set(Some(background)));
    update.theme.map(|theme| oauth.theme = Set(Some(theme)));
    update.policy.map(|policy| oauth.policy = Set(Some(policy)));
    update.tos.map(|tos| oauth.tos = Set(Some(tos)));

    match oauth.update(&app_state.database).await {
        Ok(oauth) => {
            Json(json!({
                "oauth": {
                    "id": oauth.id,
                    "name": oauth.name,
                    "description": oauth.description,
                    "homepage": oauth.homepage,
                    "avatar": oauth.avatar,
                    "background": oauth.background,
                    "theme": oauth.theme,
                    "policy": oauth.policy,
                    "tos": oauth.tos,
                    "redirect": oauth.redirect,
                    "access": oauth.access,
                }
            }))
        }
        Err(err) => {
            error!("failed to update oauth: {}", err);
            Json(json!({ "error": "failed to update oauth" }))
        }
    }
}

pub async fn delete_oauth(
    Path(oauth_id): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    match crate::entity::oauth::Entity::find()
        .filter(crate::entity::oauth::Column::Id.eq(&oauth_id))
        .one(&app_state.database)
        .await
    {
        Ok(Some(oauth)) => {
            match oauth.delete(&app_state.database).await {
                Ok(_) => Json(json!({ "message": "successful" })),
                Err(err) => {
                    error!("failed to delete oauth: {}", err);
                    Json(json!({ "error": "failed to delete oauth" }))
                }
            }
        }
        Ok(None) => Json(json!({ "error": "oauth not found" })),
        Err(err) => {
            error!("failed to find oauth: {}", err);
            Json(json!({ "error": "failed to find oauth" }))
        }
    }
}

#[derive(Deserialize)]
pub struct OAuthClientCreate {
    pub name: Option<String>,
    pub description: Option<String>,
    pub expire: Option<chrono::NaiveDateTime>,
}

pub async fn post_oauth_client(
    Path(oauth_id): Path<i32>,
    State(app_state): State<AppState>,
    Json(create): Json<OAuthClientCreate>,
) -> Json<Value> {
    let mut client = crate::entity::oauth_client::ActiveModel {
        ..Default::default()
    };

    client.oauth_id = Set(oauth_id);
    client.name = Set(create.name.unwrap_or(
        rand::rng()
            .sample_iter(&Alphanumeric)
            .take(16)
            .map(char::from)
            .collect::<String>(),
    ));
    create.description.map(|description| client.description = Set(Some(description)));
    create.expire.map(|expire| client.expire = Set(expire));

    match client.insert(&app_state.database).await {
        Ok(client) => {
            info!("created oauth client: {}", client.id);
            Json(json!({
                "client": {
                    "id": client.id,
                    "oauth_id": client.oauth_id,
                    "name": client.name,
                    "description": client.description,
                    "expire": client.expire,
                }
            }))
        }
        Err(err) => {
            error!("failed to create oauth client: {}", err);
            Json(json!({ "error": "failed to create oauth client" }))
        }
    }
}

pub async fn get_oauth_client_list(
    Path(oauth_id): Path<i32>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    match crate::entity::oauth_client::Entity::find()
        .filter(crate::entity::oauth_client::Column::OauthId.eq(oauth_id))
        .all(&app_state.database)
        .await
    {
        Ok(clients) => {
            let clients = clients
                .into_iter()
                .map(|client| json!({
                    "id": client.id,
                    "oauth_id": client.oauth_id,
                    "name": client.name,
                    "description": client.description,
                    "expire": client.expire,
                }))
                .collect::<Vec<_>>();
            Json(json!({ "clients": clients }))
        }
        Err(err) => {
            error!("failed to get oauth client list: {}", err);
            Json(json!({ "error": "failed to get oauth client list" }))
        }
    }
}

pub async fn get_oauth_client(
    Path(oauth_id): Path<i32>,
    Path(client_id): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    match crate::entity::oauth_client::Entity::find()
        .filter(crate::entity::oauth_client::Column::OauthId.eq(oauth_id))
        .filter(crate::entity::oauth_client::Column::Id.eq(client_id))
        .one(&app_state.database)
        .await
    {
        Ok(Some(client)) => {
            Json(json!({
                "client": {
                    "id": client.id,
                    "oauth_id": client.oauth_id,
                    "name": client.name,
                    "description": client.description,
                    "expire": client.expire,
                }
            }))
        }
        Ok(None) => Json(json!({ "error": "oauth client not found" })),
        Err(err) => {
            error!("failed to get oauth client: {}", err);
            Json(json!({ "error": "failed to get oauth client" }))
        }
    }
}

pub async fn delete_oauth_client(
    Path(oauth_id): Path<i32>,
    Path(client_id): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    match crate::entity::oauth_client::Entity::find()
        .filter(crate::entity::oauth_client::Column::OauthId.eq(oauth_id))
        .filter(crate::entity::oauth_client::Column::Id.eq(client_id))
        .one(&app_state.database)
        .await
    {
        Ok(Some(client)) => {
            match client.delete(&app_state.database).await {
                Ok(_) => Json(json!({ "message": "successful" })),
                Err(err) => {
                    error!("failed to delete oauth client: {}", err);
                    Json(json!({ "error": "failed to delete oauth client" }))
                }
            }
        }
        Ok(None) => Json(json!({ "error": "oauth client not found" })),
        Err(err) => {
            error!("failed to find oauth client: {}", err);
            Json(json!({ "error": "failed to find oauth client" }))
        }
    }
}
