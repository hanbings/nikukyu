use axum::{
    Json,
    extract::{Path, State},
};
use log::{error, info};
use rand::{Rng, distr::Alphanumeric};
use reqwest::header::{AUTHORIZATION, USER_AGENT};
use sea_orm::{ActiveModelTrait, ActiveValue::Set, sqlx::types::chrono::Utc};
use serde::Deserialize;
use serde_json::{Value, json};

use crate::state::AppState;

pub async fn get_login_with_oauth_authorize(
    State(app_state): State<AppState>,
    Path(provider): Path<String>,
) -> Json<Value> {
    if !app_state.oauths.contains_key(&provider) {
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
    let (openid, email, username) = match oauth.provider.as_str() {
        "github" => {
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

            let account = crate::entity::account::ActiveModel {
                created_at: Set(Utc::now().naive_local()),
                openid: Set(openid.to_owned()),
                email: Set(email.to_owned()),
                username: Set(username.to_owned()),
                ..Default::default()
            };

            match crate::entity::account::ActiveModel::insert(account, &app_state.database).await {
                Ok(account) => {
                    info!("account: {:#?}", account);
                }
                Err(e) => {
                    error!("error: {:#?}", e);
                }
            }

            (openid, email, username)
        }
        _ => {
            return Json(json!({
                "error": "invalid provider"
            }));
        }
    };

    Json(json!({
        "openid": openid,
        "email": email,
        "username": username
    }))
}
