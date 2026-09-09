use actix_web::{HttpRequest, HttpResponse, http::StatusCode, web};
use uuid::Uuid;

use crate::{
    dto::oauth::{NewApplication, NewClient},
    error::ApiResult,
    service::oauth,
    state::AppState,
};

pub async fn list(state: web::Data<AppState>, request: HttpRequest) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        oauth::list(&state, &request).await?,
    ))
}

pub async fn create(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Json<NewApplication>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::CREATED,
        oauth::create_application(&state, &request, input.into_inner()).await?,
    ))
}

pub async fn get(
    state: web::Data<AppState>,
    request: HttpRequest,
    id: web::Path<Uuid>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        oauth::get(&state, &request, *id).await?,
    ))
}

pub async fn destroy(
    state: web::Data<AppState>,
    request: HttpRequest,
    id: web::Path<Uuid>,
) -> ApiResult<HttpResponse> {
    oauth::destroy(&state, &request, *id).await?;
    Ok(HttpResponse::NoContent().finish())
}

pub async fn clients(
    state: web::Data<AppState>,
    request: HttpRequest,
    id: web::Path<Uuid>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        oauth::clients(&state, &request, *id).await?,
    ))
}

pub async fn create_client(
    state: web::Data<AppState>,
    request: HttpRequest,
    id: web::Path<Uuid>,
    input: web::Json<NewClient>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::CREATED,
        oauth::create_client(&state, &request, *id, input.into_inner()).await?,
    ))
}

pub async fn destroy_client(
    state: web::Data<AppState>,
    request: HttpRequest,
    ids: web::Path<(Uuid, Uuid)>,
) -> ApiResult<HttpResponse> {
    let (app_id, client_id) = *ids;
    oauth::destroy_client(&state, &request, app_id, client_id).await?;
    Ok(HttpResponse::NoContent().finish())
}

pub async fn public_client(
    state: web::Data<AppState>,
    id: web::Path<Uuid>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        oauth::public_client(&state, *id).await?,
    ))
}
