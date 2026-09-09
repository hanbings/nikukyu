pub mod account;
pub mod login;
pub mod oauth;
pub mod oidc;

use actix_web::{
    HttpResponse,
    http::{StatusCode, header},
};
use serde::Serialize;

pub fn json(status: StatusCode, value: impl Serialize) -> HttpResponse {
    HttpResponse::build(status)
        .insert_header((header::CACHE_CONTROL, "no-store"))
        .insert_header((header::PRAGMA, "no-cache"))
        .json(value)
}
