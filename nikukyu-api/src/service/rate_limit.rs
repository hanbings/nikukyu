use crate::error::{ApiError, ApiResult};
use actix_web::{HttpRequest, http::StatusCode};
use std::{
    collections::HashMap,
    sync::Mutex,
    time::{Duration, Instant},
};

#[derive(Default)]
pub struct RateLimiter(Mutex<HashMap<String, (Instant, u32)>>);

impl RateLimiter {
    pub fn check(&self, request: &HttpRequest, limit: u32) -> ApiResult<()> {
        // Do not trust caller-controlled X-Forwarded-For headers.
        let key = request
            .peer_addr()
            .map(|peer| peer.ip().to_string())
            .unwrap_or_default();
        let now = Instant::now();
        let mut entries = self.0.lock().map_err(|_| ApiError::internal())?;
        entries.retain(|_, (start, _)| now.duration_since(*start) < Duration::from_secs(60));
        if entries.len() >= 10_000 && !entries.contains_key(&key) {
            return Err(ApiError::new(
                StatusCode::TOO_MANY_REQUESTS,
                "rate_limited",
                "Try again later",
            ));
        }
        let (_, count) = entries.entry(key).or_insert((now, 0));
        if *count >= limit {
            return Err(ApiError::new(
                StatusCode::TOO_MANY_REQUESTS,
                "rate_limited",
                "Try again later",
            ));
        }
        *count += 1;
        Ok(())
    }
}
