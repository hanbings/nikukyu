use sea_orm::entity::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(
    Clone,
    Copy,
    Debug,
    PartialEq,
    Eq,
    EnumIter,
    DeriveActiveEnum,
    DeriveDisplay,
    Serialize,
    Deserialize,
)]
#[sea_orm(rs_type = "String", db_type = "String(StringLen::None)")]
#[serde(rename_all = "snake_case")]
pub enum AccountStatus {
    #[sea_orm(string_value = "active")]
    Active,
    #[sea_orm(string_value = "suspended")]
    Suspended,
}

#[derive(
    Clone,
    Copy,
    Debug,
    PartialEq,
    Eq,
    EnumIter,
    DeriveActiveEnum,
    DeriveDisplay,
    Serialize,
    Deserialize,
)]
#[sea_orm(rs_type = "String", db_type = "String(StringLen::None)")]
#[serde(rename_all = "snake_case")]
pub enum Scope {
    #[sea_orm(string_value = "openid")]
    Openid,
    #[sea_orm(string_value = "profile")]
    Profile,
    #[sea_orm(string_value = "email")]
    Email,
    #[sea_orm(string_value = "offline_access")]
    OfflineAccess,

    #[sea_orm(string_value = "oauth_email_verify")]
    OAuthEmailVerify,
    #[sea_orm(string_value = "email_verify")]
    EmailVerify,

    #[sea_orm(string_value = "account_read")]
    AccountRead,
    #[sea_orm(string_value = "account_write")]
    AccountWrite,
    #[sea_orm(string_value = "account_destroy")]
    AccountDestroy,
    #[sea_orm(string_value = "account_authorization_read")]
    AccountAuthorizationRead,
    #[sea_orm(string_value = "account_authorization_write")]
    AccountAuthorizationWrite,
    #[sea_orm(string_value = "account_authorization_destroy")]
    AccountAuthorizationDestroy,
    #[sea_orm(string_value = "account_oauth_read")]
    AccountOAuthRead,
    #[sea_orm(string_value = "account_oauth_write")]
    AccountOAuthWrite,
    #[sea_orm(string_value = "account_log_read")]
    AccountLogRead,
    #[sea_orm(string_value = "account_log_write")]
    AccountLogWrite,

    #[sea_orm(string_value = "oauth_read")]
    OAuthRead,
    #[sea_orm(string_value = "oauth_write")]
    OAuthWrite,
    #[sea_orm(string_value = "oauth_destroy")]
    OAuthDestroy,
    #[sea_orm(string_value = "oauth_client_read")]
    OAuthClientRead,
    #[sea_orm(string_value = "oauth_client_write")]
    OAuthClientWrite,
    #[sea_orm(string_value = "oauth_client_destroy")]
    OAuthClientDestroy,
    #[sea_orm(string_value = "oauth_log_read")]
    OAuthLogRead,
    #[sea_orm(string_value = "oauth_log_write")]
    OAuthLogWrite,

    #[sea_orm(string_value = "oauth_authorize")]
    OAuthAuthorize,
    #[sea_orm(string_value = "oauth_token")]
    OAuthToken,
    #[sea_orm(string_value = "oauth_refresh")]
    OAuthRefresh,
    #[sea_orm(string_value = "oauth_revoke")]
    OAuthRevoke,
}

#[derive(
    Clone,
    Copy,
    Debug,
    PartialEq,
    Eq,
    EnumIter,
    DeriveActiveEnum,
    DeriveDisplay,
    Serialize,
    Deserialize,
)]
#[sea_orm(rs_type = "String", db_type = "String(StringLen::None)")]
#[serde(rename_all = "snake_case")]
pub enum GrantType {
    #[sea_orm(string_value = "authorization_code")]
    AuthorizationCode,
    #[sea_orm(string_value = "refresh_token")]
    RefreshToken,
    #[sea_orm(string_value = "client_credentials")]
    ClientCredentials,
}

#[derive(
    Clone,
    Copy,
    Debug,
    PartialEq,
    Eq,
    EnumIter,
    DeriveActiveEnum,
    DeriveDisplay,
    Serialize,
    Deserialize,
)]
#[sea_orm(rs_type = "String", db_type = "String(StringLen::None)")]
pub enum TokenType {
    #[sea_orm(string_value = "Bearer")]
    Bearer,
}

#[derive(Clone, Copy, Debug, PartialEq, Eq, EnumIter, DeriveActiveEnum, Serialize, Deserialize)]
#[sea_orm(rs_type = "String", db_type = "String(StringLen::None)")]
#[serde(rename_all = "snake_case")]
pub enum ClientAuthMethod {
    #[sea_orm(string_value = "client_secret_basic")]
    ClientSecretBasic,
    #[sea_orm(string_value = "client_secret_post")]
    ClientSecretPost,
    #[sea_orm(string_value = "none")]
    None,
}
