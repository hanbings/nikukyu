use actix_web::{
    HttpRequest, HttpResponse,
    http::{StatusCode, header},
    web,
};

use crate::{
    dto::oidc::{
        AuthorizationContextRequest, AuthorizationDecisionRequest, AuthorizationRedirect,
        AuthorizationRequest, RevocationRequest, TokenRequest,
    },
    error::ApiResult,
    service::{
        oidc::{self, authorize, token},
        session,
    },
    state::AppState,
};

pub async fn discovery(state: web::Data<AppState>) -> HttpResponse {
    HttpResponse::Ok()
        .insert_header((header::CACHE_CONTROL, "public, max-age=300"))
        .json(oidc::discovery(&state))
}

pub async fn jwks(state: web::Data<AppState>) -> HttpResponse {
    HttpResponse::Ok()
        .insert_header((header::CACHE_CONTROL, "public, max-age=300"))
        .json(oidc::jwks(&state))
}

fn authorization_response(state: &AppState, outcome: AuthorizationRedirect) -> HttpResponse {
    let mut response = HttpResponse::SeeOther();
    response.insert_header((header::LOCATION, outcome.redirect_uri));
    response.insert_header((header::CACHE_CONTROL, "no-store"));
    if let Some(token) = outcome.browser_token {
        response.cookie(session::cookie(
            &state.config,
            session::AUTHORIZATION_COOKIE,
            token,
            600,
        ));
    }
    response.finish()
}

pub async fn authorize_get(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Query<AuthorizationRequest>,
) -> ApiResult<HttpResponse> {
    Ok(authorization_response(
        &state,
        authorize::begin(&state, &request, input.into_inner()).await?,
    ))
}

pub async fn authorize_post(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Form<AuthorizationRequest>,
) -> ApiResult<HttpResponse> {
    Ok(authorization_response(
        &state,
        authorize::begin(&state, &request, input.into_inner()).await?,
    ))
}

pub async fn token(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Form<TokenRequest>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        token::exchange(&state, &request, input.into_inner()).await?,
    ))
}

pub async fn userinfo(state: web::Data<AppState>, request: HttpRequest) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        token::userinfo(&state, &request).await?,
    ))
}

pub async fn revoke(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Form<RevocationRequest>,
) -> ApiResult<HttpResponse> {
    token::revoke(&state, &request, input.into_inner()).await?;
    Ok(HttpResponse::Ok()
        .insert_header((header::CACHE_CONTROL, "no-store"))
        .finish())
}

pub async fn authorization_context(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Json<AuthorizationContextRequest>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        authorize::context(&state, &request, input.into_inner()).await?,
    ))
}

pub async fn authorization_decision(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Json<AuthorizationDecisionRequest>,
) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::CREATED,
        authorize::decide(&state, &request, input.into_inner()).await?,
    ))
}
