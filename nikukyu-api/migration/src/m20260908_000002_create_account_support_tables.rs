use sea_orm_migration::{prelude::*, schema::*};

use models::{account_authorization, account_log};

use crate::schema_v1::{timestamp_default, uuid_primary_key};

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager
            .create_table(
                Table::create()
                    .table(account_authorization::Entity)
                    .col(uuid_primary_key(account_authorization::Column::Id))
                    .col(uuid(account_authorization::Column::AccountId))
                    .col(string_len(account_authorization::Column::Provider, 64))
                    .col(text(account_authorization::Column::Openid))
                    .col(timestamp_default(account_authorization::Column::CreatedAt))
                    .col(timestamp_default(account_authorization::Column::UpdatedAt))
                    .col(timestamp_with_time_zone_null(
                        account_authorization::Column::DeletedAt,
                    ))
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("idx_account_authorizations_account_id")
                    .table(account_authorization::Entity)
                    .col(account_authorization::Column::AccountId)
                    .to_owned(),
            )
            .await?;

        // A third-party identity can have only one current binding, but keeps its history.
        manager
            .create_index(
                Index::create()
                    .name("uq_account_authorizations_provider_openid_active")
                    .table(account_authorization::Entity)
                    .col(account_authorization::Column::Provider)
                    .col(account_authorization::Column::Openid)
                    .unique()
                    .and_where(Expr::col(account_authorization::Column::DeletedAt).is_null())
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(account_log::Entity)
                    .col(uuid_primary_key(account_log::Column::Id))
                    .col(uuid(account_log::Column::AccountId))
                    .col(timestamp_default(account_log::Column::CreatedAt))
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("idx_account_logs_account_created_at")
                    .table(account_log::Entity)
                    .col(account_log::Column::AccountId)
                    .col(account_log::Column::CreatedAt)
                    .to_owned(),
            )
            .await?;

        Ok(())
    }

    async fn down(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager
            .drop_table(Table::drop().table(account_log::Entity).to_owned())
            .await?;
        manager
            .drop_table(
                Table::drop()
                    .table(account_authorization::Entity)
                    .to_owned(),
            )
            .await?;

        Ok(())
    }
}
