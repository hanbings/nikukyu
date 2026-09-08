use sea_orm::entity::prelude::*;

#[sea_orm::model]
#[derive(Clone, Debug, PartialEq, Eq, DeriveEntityModel)]
#[sea_orm(table_name = "oauths")]
pub struct Model {
    #[sea_orm(primary_key)]
    pub id: Uuid,
    pub account_id: Uuid,

    pub name: String,

    pub background: Option<String>,
    pub theme_color: Option<String>,
    pub avatar: Option<String>,
    pub description: Option<String>,
    pub email: Option<String>,
    pub terms_of_service: Option<String>,
    pub privacy_policy: Option<String>,

    pub is_deleted: bool,

    pub created_at: DateTimeWithTimeZone,
    pub updated_at: DateTimeWithTimeZone,
    pub deleted_at: Option<DateTimeWithTimeZone>,
}

impl ActiveModelBehavior for ActiveModel {}
