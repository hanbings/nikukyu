use sea_orm::entity::prelude::*;

use crate::enums::AccountStatus;

#[sea_orm::model]
#[derive(Clone, Debug, PartialEq, Eq, DeriveEntityModel)]
#[sea_orm(table_name = "accounts")]
pub struct Model {
    #[sea_orm(primary_key)]
    pub id: Uuid,

    pub username: String,
    pub nickname: Option<String>,
    pub avatar: Option<String>,
    pub background: Option<String>,
    pub theme_color: Option<String>,

    pub status: AccountStatus,
    pub last_login_at: Option<DateTimeWithTimeZone>,

    pub is_deleted: bool,

    pub created_at: DateTimeWithTimeZone,
    pub updated_at: DateTimeWithTimeZone,
    pub deleted_at: Option<DateTimeWithTimeZone>,
}

impl ActiveModelBehavior for ActiveModel {}
