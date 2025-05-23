use axum::{
    Json,
    extract::{Path, State},
};
use log::{error, info};
use rand::{Rng, distr::Alphanumeric, rng};
use reqwest::header::{AUTHORIZATION, USER_AGENT};
use sea_orm::{
    ActiveModelTrait,
    ActiveValue::Set,
    ColumnTrait, EntityTrait, QueryFilter, TryIntoModel,
    sqlx::types::{chrono::Utc, time},
};
use serde::Deserialize;
use serde_json::{Value, json};
use uuid::Uuid;

use crate::{controller::account, entity::account_authorization, state::AppState};

pub async fn get_login_with_oauth_authorize(
    State(app_state): State<AppState>,
    Path(provider): Path<String>,
) -> Json<Value> {
    if !app_state.oauths_config.contains_key(&provider) {
        return Json(json!({
            "error": "invalid provider"
        }));
    }

    let oauth = app_state.oauths_config.get(&provider).unwrap();
    let redirect_uri = match oauth.provider.as_str() {
        "github" => format!(
            "https://github.com/login/oauth/authorize?client_id={}&redirect_uri={}&state={}&response_type=code&scope=user:email",
            oauth.client_id,
            oauth.redirect_uri,
            rand::rng()
                .sample_iter(&Alphanumeric)
                .take(16)
                .map(char::from)
                .collect::<String>()
        ),
        _ => {
            return Json(json!({
                "error": "invalid provider"
            }));
        }
    };

    Json(json!({
        "provider": provider,
        "authorize": redirect_uri
    }))
}

#[derive(Deserialize)]
pub struct OAuthCallback {
    code: String,
    state: String,
    error: Option<String>,
}

pub async fn post_login_with_oauth_callback(
    State(app_state): State<AppState>,
    Path(provider): Path<String>,
    callback: Json<OAuthCallback>,
) -> Json<Value> {
    if callback.error.is_some() {
        return Json(json!({
            "error": callback.error
        }));
    }

    let oauth = app_state.oauths_config.get(&provider).unwrap();
    let (token, account) = match oauth.provider.as_str() {
        "github" => {
            // 1. request access token
            let client = reqwest::Client::new();
            let res = client.post(
                format!(
                    "https://github.com/login/oauth/access_token?client_id={}&client_secret={}&redirect_uri={}&code={}&state={}", 
                    oauth.client_id,
                    oauth.client_secret,
                    oauth.redirect_uri,
                    callback.code,
                    callback.state
                ))
                .header("Accept", "application/json")
                .send()
                .await
                .unwrap();

            if res.status().as_u16() != 200 {
                return Json(json!({
                    "error": "invalid token"
                }));
            }

            let json: serde_json::Value = match res.json().await {
                Ok(json) => json,
                Err(e) => {
                    return Json(json!({
                        "error": e.to_string()
                    }));
                }
            };

            if json["error"].is_string() {
                return Json(json!({
                    "error": json["error"].as_str().unwrap()
                }));
            }

            // 2. request user info
            let client = reqwest::Client::new();
            let res = client
                .get(format!("https://api.github.com/user",))
                .header(USER_AGENT, "Nikukyu OAuth/1.0")
                .header(
                    AUTHORIZATION,
                    format!("Bearer {}", json["access_token"].as_str().unwrap()),
                )
                .send()
                .await
                .unwrap();

            info!("res: {:#?}", res.status().as_u16());

            if res.status().as_u16() != 200 {
                return Json(json!({ "error": "invalid token" }));
            }

            let json: serde_json::Value = match res.json().await {
                Ok(json) => json,
                Err(e) => {
                    return Json(json!({ "error": e.to_string() }));
                }
            };

            if json["error"].is_string() {
                return Json(json!({ "error": json["error"].as_str().unwrap() }));
            }

            let (openid, email, username) = match (
                json["id"].clone(),
                json["email"].clone(),
                json["login"].clone(),
            ) {
                (Value::Number(id), Value::String(email), Value::String(username)) => {
                    (id.as_u64().unwrap().to_string(), email, username)
                }
                (Value::String(id), Value::Null, Value::String(username)) => {
                    let res = client
                        .get(format!("https://api.github.com/user/emails",))
                        .header(USER_AGENT, "Nikukyu OAuth/1.0")
                        .header(
                            AUTHORIZATION,
                            format!("Bearer {}", json["access_token"].as_str().unwrap()),
                        )
                        .send()
                        .await
                        .unwrap();

                    if res.status().as_u16() != 200 {
                        return Json(json!({
                            "error": "invalid token"
                        }));
                    }

                    let json: serde_json::Value = match res.json().await {
                        Ok(json) => json,
                        Err(e) => {
                            return Json(json!({
                                "error": e.to_string()
                            }));
                        }
                    };

                    if json["error"].is_string() {
                        return Json(json!({
                            "error": json["error"].as_str().unwrap()
                        }));
                    }

                    let email = match json["email"].as_str() {
                        Some(email) => email,
                        None => {
                            return Json(json!({
                                "error": "invalid token"
                            }));
                        }
                    };

                    (id, email.to_string(), username)
                }
                _ => {
                    return Json(json!({
                        "error": "invalid token"
                    }));
                }
            };

            // 3. check if user exists
            let account = match crate::entity::account_authorization::Entity::find()
                .filter(crate::entity::account_authorization::Column::Openid.eq(openid.to_owned()))
                .filter(
                    crate::entity::account_authorization::Column::Provider.eq(provider.to_owned()),
                )
                .one(&app_state.database)
                .await
            {
                Ok(Some(account_authorization)) => {
                    let account = match crate::entity::account::Entity::find_by_id(
                        account_authorization.created_by,
                    )
                    .one(&app_state.database)
                    .await
                    {
                        Ok(Some(account)) => account,
                        Ok(None) => {
                            return Json(json!({
                                "error": "invalid account authorization relation, please contact administrator."
                            }));
                        }
                        Err(e) => {
                            error!("query error: {:#?}", e);
                            return Json(json!({ "error": e.to_string() }));
                        }
                    };

                    account
                }
                Ok(None) => {
                    // 4. create new account if not exists
                    let new_account = crate::entity::account::ActiveModel {
                        created_at: Set(Utc::now().naive_local()),
                        openid: Set(Uuid::new_v4().to_string()),
                        email: Set(email.to_owned()),
                        username: Set(username.to_owned()),
                        ..Default::default()
                    };

                    let account = match crate::entity::account::ActiveModel::insert(
                        new_account,
                        &app_state.database,
                    )
                    .await
                    {
                        Ok(account) => account,
                        Err(e) => {
                            error!("insert error: {:#?}", e);
                            return Json(json!({ "error": e.to_string() }));
                        }
                    };

                    let new_account_authorization =
                        crate::entity::account_authorization::ActiveModel {
                            created_at: Set(Utc::now().naive_local()),
                            created_by: Set(account.id),
                            provider: Set(provider.to_owned()),
                            openid: Set(openid.to_owned()),
                            ..Default::default()
                        };

                    match crate::entity::account_authorization::ActiveModel::insert(
                        new_account_authorization,
                        &app_state.database,
                    )
                    .await
                    {
                        Ok(_) => {}
                        Err(e) => {
                            error!("insert error: {:#?}", e);
                            return Json(json!({ "error": e.to_string() }));
                        }
                    };

                    account
                }
                Err(e) => {
                    error!("query error: {:#?}", e);
                    return Json(json!({ "error": e.to_string() }));
                }
            };

            // 5. return token and user info
            let timestamp = Utc::now().naive_local().and_utc().timestamp();
            let token = crate::security::token::Token {
                token: rand::rng()
                    .sample_iter(&Alphanumeric)
                    .take(32)
                    .map(char::from)
                    .collect::<String>(),
                expire_in: timestamp + 3600,
                created_at: timestamp,
                created_by: account.id,
                scopes: vec!["user".to_string()],
            };

            app_state
                .tokens
                .lock()
                .await
                .insert(token.token.clone(), token.clone());

            (token, account)
        }
        _ => {
            return Json(json!({
                "error": "invalid provider"
            }));
        }
    };

    Json(json!({
        "token": token,
        // Here we need to mark entity::account with a
        // serde Serialize macro to indicate that it can be serialized.
        "account": account,
    }))
}
