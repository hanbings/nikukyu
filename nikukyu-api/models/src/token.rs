use sea_orm::entity::prelude::*;

use crate::enums::{GrantType, Scope, TokenType};

#[sea_orm::model]
#[derive(Clone, Debug, PartialEq, Eq, DeriveEntityModel)]
#[sea_orm(table_name = "tokens")]
pub struct Model {
    #[sea_orm(primary_key)]
    pub id: Uuid,
    pub account_id: Uuid,
    pub oauth_id: Option<Uuid>,
    pub oauth_client_id: Option<Uuid>,

    pub scope: Vec<Scope>,
    pub token_type: TokenType,
    pub grant_type: GrantType,

    pub account_oauth_id: Option<Uuid>,
    pub token_family_id: Option<Uuid>,
    pub authorization_code_id: Option<Uuid>,
    pub nonce: Option<String>,
    pub auth_time: DateTimeWithTimeZone,

    #[sea_orm(unique)]
    pub access_token_hash: String,
    pub previous_token_id: Option<Uuid>,
    #[sea_orm(unique)]
    pub refresh_token_hash: Option<String>,
    pub is_revoked: bool,
    pub is_refreshed: bool,

    pub created_at: DateTimeWithTimeZone,
    pub updated_at: DateTimeWithTimeZone,
    pub expires_at: Option<DateTimeWithTimeZone>,

    pub revoked_at: Option<DateTimeWithTimeZone>,
    pub refreshed_at: Option<DateTimeWithTimeZone>,
    pub refresh_expires_at: Option<DateTimeWithTimeZone>,
    pub last_used_at: Option<DateTimeWithTimeZone>,
}

impl ActiveModelBehavior for ActiveModel {}
