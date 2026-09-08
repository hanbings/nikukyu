use sea_orm::entity::prelude::*;

use crate::enums::Scope;

#[sea_orm::model]
#[derive(Clone, Debug, PartialEq, Eq, DeriveEntityModel)]
#[sea_orm(table_name = "authorization_codes")]
pub struct Model {
    #[sea_orm(primary_key)]
    pub id: Uuid,
    pub account_id: Uuid,
    pub oauth_id: Uuid,
    pub oauth_client_id: Uuid,

    #[sea_orm(unique)]
    pub code_hash: String,
    pub redirect_uri: String,
    pub scope: Vec<Scope>,
    pub code_challenge: Option<String>,
    pub code_challenge_method: Option<String>,
    pub is_consumed: bool,

    pub created_at: DateTimeWithTimeZone,
    pub expires_at: DateTimeWithTimeZone,
    pub consumed_at: Option<DateTimeWithTimeZone>,
}

impl ActiveModelBehavior for ActiveModel {}
