use sea_orm::entity::prelude::*;

use crate::enums::{GrantType, Scope};

#[sea_orm::model]
#[derive(Clone, Debug, PartialEq, Eq, DeriveEntityModel)]
#[sea_orm(table_name = "oauth_clients")]
pub struct Model {
    #[sea_orm(primary_key)]
    pub id: Uuid,
    pub oauth_id: Uuid,

    pub client_secret_hash: String,
    pub grant_types: Vec<GrantType>,
    pub redirect_uris: Vec<String>,
    pub allowed_scopes: Vec<Scope>,

    pub is_deleted: bool,

    pub created_at: DateTimeWithTimeZone,
    pub updated_at: DateTimeWithTimeZone,
    pub deleted_at: Option<DateTimeWithTimeZone>,
}

impl ActiveModelBehavior for ActiveModel {}
