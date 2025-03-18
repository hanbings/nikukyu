pub mod token;

use axum::http::header::AUTHORIZATION;
use axum::{
    body::Body,
    http::{Request, StatusCode},
    middleware::Next,
    response::Response,
};

pub async fn auth_middleware(req: Request<Body>, next: Next) -> Result<Response, StatusCode> {
    let auth_header = req
        .headers()
        .get(AUTHORIZATION)
        .and_then(|value| value.to_str().ok());

    // 检查是否为 Bearer 令牌
    let _token = match auth_header {
        Some(header_value) if header_value.starts_with("Bearer ") => {
            header_value.trim_start_matches("Bearer ").to_string()
        }
        _ => return Err(StatusCode::UNAUTHORIZED),
    };

    Ok(next.run(req).await)
}
