use axum::{
    Json,
    extract::{Path, State},
};
use sea_orm::*;
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

pub async fn update_account(
    Path(username): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    todo!()
}

pub async fn delete_account(
    Path(username): Path<String>,
    State(app_state): State<AppState>,
) -> Json<Value> {
    todo!()
}
