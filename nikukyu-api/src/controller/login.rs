use actix_web::{
    HttpRequest, HttpResponse,
    http::{StatusCode, header},
    web,
};

use crate::{
    dto::session::{CompletedLogin, Credentials, GitHubCallback, ReturnToQuery},
    error::{ApiError, ApiResult},
    service::{github, session},
    state::AppState,
};

pub async fn create(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Json<Credentials>,
) -> ApiResult<HttpResponse> {
    let issued = session::login(&state, &request, input.into_inner()).await?;
    let mut response = super::json(StatusCode::CREATED, issued.session);
    response
        .add_cookie(&session::cookie(
            &state.config,
            session::SESSION_COOKIE,
            issued.cookie_token,
            state.config.auth.session_ttl_seconds,
        ))
        .map_err(|_| ApiError::internal())?;
    response.headers_mut().insert(
        header::LOCATION,
        header::HeaderValue::from_static("/api/v1/sessions/current"),
    );
    Ok(response)
}

pub async fn current(state: web::Data<AppState>, request: HttpRequest) -> ApiResult<HttpResponse> {
    Ok(super::json(
        StatusCode::OK,
        session::current(&state, &request).await?,
    ))
}

pub async fn destroy(state: web::Data<AppState>, request: HttpRequest) -> ApiResult<HttpResponse> {
    session::destroy(&state, &request).await?;
    Ok(HttpResponse::NoContent()
        .cookie(session::cookie(
            &state.config,
            session::SESSION_COOKIE,
            String::new(),
            0,
        ))
        .finish())
}

pub async fn github_start(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Query<ReturnToQuery>,
) -> ApiResult<HttpResponse> {
    let flow = github::start(&state, &request, input.into_inner()).await?;
    Ok(HttpResponse::SeeOther()
        .cookie(session::cookie(
            &state.config,
            session::BROWSER_COOKIE,
            flow.browser_token,
            600,
        ))
        .insert_header((header::LOCATION, flow.authorization.authorization_url))
        .insert_header((header::CACHE_CONTROL, "no-store"))
        .finish())
}

pub async fn github_bind(
    state: web::Data<AppState>,
    request: HttpRequest,
) -> ApiResult<HttpResponse> {
    let flow = github::bind(&state, &request).await?;
    let mut response = super::json(StatusCode::CREATED, flow.authorization);
    response
        .add_cookie(&session::cookie(
            &state.config,
            session::BROWSER_COOKIE,
            flow.browser_token,
            600,
        ))
        .map_err(|_| ApiError::internal())?;
    Ok(response)
}

pub async fn github_callback(
    state: web::Data<AppState>,
    request: HttpRequest,
    input: web::Query<GitHubCallback>,
) -> ApiResult<HttpResponse> {
    let result = github::complete(&state, &request, input.into_inner()).await?;
    Ok(login_redirect(&state, result))
}

fn login_redirect(state: &AppState, result: CompletedLogin) -> HttpResponse {
    HttpResponse::SeeOther()
        .insert_header((header::LOCATION, result.redirect_uri))
        .insert_header((header::CACHE_CONTROL, "no-store"))
        .cookie(session::cookie(
            &state.config,
            session::SESSION_COOKIE,
            result.session_token,
            state.config.auth.session_ttl_seconds,
        ))
        .cookie(session::cookie(
            &state.config,
            session::BROWSER_COOKIE,
            String::new(),
            0,
        ))
        .finish()
}
