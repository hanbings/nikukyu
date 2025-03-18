use axum::{
    Json,
    extract::{Path, State},
};
use sea_orm::{sqlx::types::chrono, *};
use serde_json::{Value, json};

use crate::{dto::account::GetAccount, state::AppState};

pub async fn get_account(
    Path(username): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    let username = username.as_str();

    {
        let accounts = app_state.accounts.try_lock().unwrap();
        if let Some(account) = accounts.get(username) {
            return Json(json!({ "account": GetAccount::from(account) }));
        }
    }

    match crate::entity::account::Entity::find()
        .filter(crate::entity::account::Column::Username.eq(username))
        .one(&app_state.database)
        .await
        .unwrap()
    {
        Some(account) => {
            app_state
                .accounts
                .try_lock()
                .unwrap()
                .insert(username.to_string(), account.clone());

            Json(json!({ "account": GetAccount::from(&account) }))
        }
        None => Json(json!({ "error": "account not found" })),
    }
}

#[derive(serde::Deserialize)]
pub struct UpdateAccount {
    pub nickname: Option<String>,
    pub avatar: Option<String>,
    pub background: Option<String>,
    pub color: Option<String>,
}

pub async fn update_account(
    Path(username): axum::extract::Path<String>,
    State(app_state): State<AppState>,
    Json(update): axum::extract::Json<UpdateAccount>,
) -> Json<Value> {
    let username = username.as_str();
    let Some(mut account): Option<crate::entity::account::ActiveModel> =
        crate::entity::account::Entity::find()
            .filter(crate::entity::account::Column::Username.eq(username))
            .one(&app_state.database)
            .await
            .ok()
            .flatten()
            .map(|acc| acc.into())
    else {
        return Json(json!({ "error": "account not found" }));
    };

    update
        .nickname
        .map(|nickname| account.nickname = Set(Some(nickname)));
    update
        .avatar
        .map(|avatar| account.avatar = Set(Some(avatar)));
    update
        .background
        .map(|background| account.background = Set(Some(background)));
    update.color.map(|color| account.color = Set(Some(color)));
    account.updated_at = Set(Some(chrono::Local::now().naive_local()));

    match crate::entity::account::Entity::find()
        .filter(crate::entity::account::Column::Username.eq(username))
        .one(&app_state.database)
        .await
    {
        Ok(account) => {
            let account = account.unwrap();
            app_state
                .accounts
                .try_lock()
                .unwrap()
                .insert(username.to_string(), account.clone());

            Json(json!({ "account": GetAccount::from(&account)}))
        }
        Err(_) => return Json(json!({ "error": "update failed" })),
    }
}

pub async fn delete_account(
    Path(username): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    let username = username.as_str();

    {
        let mut accounts = app_state.accounts.try_lock().unwrap();
        if let Some(account) = accounts.remove(username) {
            return Json(json!({ "account": GetAccount::from(&account) }));
        }
    }

    match crate::entity::account::Entity::find()
        .filter(crate::entity::account::Column::Username.eq(username))
        .one(&app_state.database)
        .await
        .unwrap()
    {
        Some(account) => {
            account.delete(&app_state.database).await.unwrap();

            Json(json!({ "message": "sucessful" }))
        }
        None => Json(json!({ "error": "account not found" })),
    }
}
