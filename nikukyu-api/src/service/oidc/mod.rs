pub mod authorize;
pub mod token;

use models::{account, account_email, enums::Scope};
use sea_orm::{ActiveEnum, ColumnTrait, ConnectionTrait, EntityTrait, QueryFilter};

use super::oauth::OIDC_SCOPES;
use crate::{
    dto::oidc::{DiscoveryResponse, JsonWebKeySet, UserInfoResponse},
    error::{ApiError, ApiResult},
    state::AppState,
};

pub(super) fn scopes(value: &str) -> ApiResult<Vec<Scope>> {
    if value.len() > 512 || value.split(' ').any(str::is_empty) {
        return Err(ApiError::protocol("invalid_scope", "Invalid scope list"));
    }
    let mut scopes = Vec::new();
    for raw in value.split(' ') {
        let scope = Scope::try_from_value(&raw.to_owned())
            .map_err(|_| ApiError::protocol("invalid_scope", "Unknown scope"))?;
        if !OIDC_SCOPES.contains(&scope) {
            return Err(ApiError::protocol(
                "invalid_scope",
                "Unsupported OIDC scope",
            ));
        }
        if !scopes.contains(&scope) {
            scopes.push(scope);
        }
    }
    Ok(scopes)
}

pub(super) fn scope_string(scopes: &[Scope]) -> String {
    scopes
        .iter()
        .map(ActiveEnum::to_value)
        .collect::<Vec<_>>()
        .join(" ")
}

async fn claims(
    db: &impl ConnectionTrait,
    account: &account::Model,
    scopes: &[Scope],
) -> ApiResult<UserInfoResponse> {
    let mut claims = UserInfoResponse {
        sub: account.id.to_string(),
        ..Default::default()
    };
    if scopes.contains(&Scope::Profile) {
        claims.preferred_username = Some(account.username.clone());
        if let Some(name) = &account.nickname {
            claims.name = Some(name.clone());
        }
        if let Some(avatar) = &account.avatar {
            claims.picture = Some(avatar.clone());
        }
        claims.updated_at = Some(account.updated_at.timestamp());
    }
    if scopes.contains(&Scope::Email)
        && let Some(email) = account_email::Entity::find()
            .filter(account_email::Column::AccountId.eq(account.id))
            .filter(account_email::Column::IsDeleted.eq(false))
            .filter(account_email::Column::IsPrimary.eq(true))
            .one(db)
            .await?
    {
        claims.email = Some(email.email);
        claims.email_verified = Some(email.verified_at.is_some());
    }
    Ok(claims)
}

pub fn discovery(state: &AppState) -> DiscoveryResponse {
    DiscoveryResponse {
        issuer: state.config.oidc.issuer.clone(),
        authorization_endpoint: state.config.endpoint("/oidc/authorize"),
        token_endpoint: state.config.endpoint("/oidc/token"),
        userinfo_endpoint: state.config.endpoint("/oidc/userinfo"),
        revocation_endpoint: state.config.endpoint("/oidc/revoke"),
        jwks_uri: state.config.endpoint("/oidc/jwks"),
        response_types_supported: vec!["code"],
        response_modes_supported: vec!["query"],
        grant_types_supported: vec!["authorization_code", "refresh_token"],
        subject_types_supported: vec!["public"],
        id_token_signing_alg_values_supported: vec!["RS256"],
        token_endpoint_auth_methods_supported: vec![
            "client_secret_basic",
            "client_secret_post",
            "none",
        ],
        revocation_endpoint_auth_methods_supported: vec![
            "client_secret_basic",
            "client_secret_post",
            "none",
        ],
        code_challenge_methods_supported: vec!["S256"],
        scopes_supported: vec!["openid", "profile", "email", "offline_access"],
        claims_supported: vec![
            "iss",
            "sub",
            "aud",
            "exp",
            "iat",
            "auth_time",
            "nonce",
            "preferred_username",
            "name",
            "picture",
            "updated_at",
            "email",
            "email_verified",
        ],
        claims_parameter_supported: false,
        request_parameter_supported: false,
        request_uri_parameter_supported: false,
        authorization_response_iss_parameter_supported: true,
    }
}

pub fn jwks(state: &AppState) -> &JsonWebKeySet {
    &state.signing_key.jwks
}
