use actix_web::{HttpRequest, HttpResponse, http::StatusCode, web};
use uuid::Uuid;

use crate::{
    dto::account::{RegisterAccountRequest, UpdateAccountRequest},
    error::ApiResult,
    service::account,
    state::AppState,
};

pub async fn create(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Json<RegisterAccountRequest>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::CREATED,
        account::register(&state, &request, input.into_inner()).await?,
    ))
}

pub async fn current(state: web::Data<AppState>, request: HttpRequest) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        account::current(&state, &request).await?,
    ))
}

pub async fn update(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Json<UpdateAccountRequest>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        account::update(&state, &request, input.into_inner()).await?,
    ))
}

pub async fn authorizations(
    state: web::Data<AppState>,
    request: HttpRequest,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        account::authorizations(&state, &request).await?,
    ))
}

pub async fn grants(state: web::Data<AppState>, request: HttpRequest) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        account::grants(&state, &request).await?,
    ))
}

pub async fn revoke_grant(
    state: web::Data<AppState>,
    request: HttpRequest,
    id: web::Path<Uuid>,
) -> ApiResult<HttpResponse> {
    account::revoke_grant(&state, &request, *id).await?;
    Ok(HttpResponse::NoContent().finish())
}
