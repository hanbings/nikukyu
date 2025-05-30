pub mod extension;
pub mod token;

use std::iter::Map;

use axum::RequestExt;
use axum::extract::{FromRef, State};
use axum::http::header::AUTHORIZATION;
use axum::http::{self, HeaderValue};
use axum::{
    body::Body,
    http::{Request, StatusCode},
    middleware::Next,
    response::Response,
};
use extension::ActiveToken;
use log::{debug, info};
use sea_orm::EntityTrait;

use crate::state::AppState;

pub async fn auth_middleware(
    State(app_state): State<AppState>,
    mut req: Request<Body>,
    next: Next,
) -> Result<Response, StatusCode> {
    let (token, account) = {
        let auth_header = req
            .headers()
            .get(AUTHORIZATION)
            .and_then(|value| value.to_str().ok());

        debug!("auth_header: {:?}", auth_header);
        debug!("req.uri().path(): {:?}", req.uri().path());
        debug!("req.method(): {:?}", req.method());
        debug!("app_state tokens: {:?}", app_state.tokens);

        if req.uri().path().starts_with("/api/v0/oauth/client/")
            && req.method() == &http::Method::GET
        {
            return Ok(next.run(req).await);
        }

        let (path, method) = (req.uri().path(), req.method());
        match (path, method) {
            ("/api/v0/login/oauth/github/authorize", &http::Method::GET)
            | ("/api/v0/login/oauth/github/callback", &http::Method::POST)
            | ("/", &http::Method::GET)
            | ("/index", &http::Method::GET) => {
                return Ok(next.run(req).await);
            }
            _ => {}
        }

        // 检查是否为 Bearer 令牌
        let token = match auth_header {
            Some(header_value) if header_value.starts_with("Bearer ") => {
                let bearer_token = header_value.trim_start_matches("Bearer ").to_string();
                match app_state.tokens.lock().await.get(&bearer_token) {
                    Some(token) => token.clone(),
                    None => return Err(StatusCode::UNAUTHORIZED),
                }
            }
            _ => return Err(StatusCode::UNAUTHORIZED),
        };

        let account = match crate::entity::account::Entity::find_by_id(token.created_by)
            .one(&app_state.database)
            .await
        {
            Ok(Some(account)) => account,
            Ok(None) => return Err(StatusCode::UNAUTHORIZED),
            Err(_) => return Err(StatusCode::INTERNAL_SERVER_ERROR),
        };

        (token, account)
    };

    req.extensions_mut().insert(ActiveToken { token, account });

    Ok(next.run(req).await)
}
