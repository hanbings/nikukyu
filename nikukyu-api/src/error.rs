use crate::dto::ErrorResponse;
use actix_web::{
    HttpResponse, ResponseError,
    http::{StatusCode, header},
};
use sea_orm::{DbErr, SqlErr};

#[derive(Debug)]
pub struct ApiError {
    pub status: StatusCode,
    pub code: &'static str,
    pub message: String,
}

impl ApiError {
    pub fn new(status: StatusCode, code: &'static str, message: impl Into<String>) -> Self {
        Self {
            status,
            code,
            message: message.into(),
        }
    }
    pub fn bad(message: impl Into<String>) -> Self {
        Self::new(StatusCode::BAD_REQUEST, "invalid_request", message)
    }
    pub fn protocol(code: &'static str, message: impl Into<String>) -> Self {
        Self::new(StatusCode::BAD_REQUEST, code, message)
    }
    pub fn unauthorized() -> Self {
        Self::new(
            StatusCode::UNAUTHORIZED,
            "invalid_token",
            "Authentication required",
        )
    }
    pub fn forbidden(message: impl Into<String>) -> Self {
        Self::new(StatusCode::FORBIDDEN, "access_denied", message)
    }
    pub fn missing() -> Self {
        Self::new(StatusCode::NOT_FOUND, "not_found", "Resource not found")
    }
    pub fn internal() -> Self {
        Self::new(
            StatusCode::INTERNAL_SERVER_ERROR,
            "server_error",
            "An internal error occurred",
        )
    }
}

impl std::fmt::Display for ApiError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.write_str(&self.message)
    }
}
impl std::error::Error for ApiError {}
impl ResponseError for ApiError {
    fn status_code(&self) -> StatusCode {
        self.status
    }
    fn error_response(&self) -> HttpResponse {
        let mut response = HttpResponse::build(self.status);
        response.insert_header((header::CACHE_CONTROL, "no-store"));
        response.insert_header((header::PRAGMA, "no-cache"));
        if self.status == StatusCode::UNAUTHORIZED {
            response.insert_header((
                header::WWW_AUTHENTICATE,
                if self.code == "invalid_client" {
                    "Basic realm=\"oidc\""
                } else {
                    "Bearer"
                },
            ));
        }
        response.json(ErrorResponse {
            error: self.code,
            error_description: &self.message,
        })
    }
}
impl From<DbErr> for ApiError {
    fn from(error: DbErr) -> Self {
        if matches!(error.sql_err(), Some(SqlErr::UniqueConstraintViolation(_))) {
            Self::new(StatusCode::CONFLICT, "conflict", "Resource already exists")
        } else {
            tracing::error!(error = %error, "database operation failed");
            Self::internal()
        }
    }
}

pub type ApiResult<T> = Result<T, ApiError>;
