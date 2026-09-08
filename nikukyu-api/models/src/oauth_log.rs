use sea_orm::entity::prelude::*;

#[sea_orm::model]
#[derive(Clone, Debug, PartialEq, Eq, DeriveEntityModel)]
#[sea_orm(table_name = "oauth_logs")]
pub struct Model {
    #[sea_orm(primary_key)]
    pub id: Uuid,
    pub oauth_id: Uuid,

    pub created_at: DateTimeWithTimeZone,
}

impl ActiveModelBehavior for ActiveModel {}
