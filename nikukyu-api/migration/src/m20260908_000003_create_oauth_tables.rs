use sea_orm_migration::{prelude::*, schema::*};

use models::{account_oauth, oauth, oauth_client, oauth_log};

use crate::schema_v1::{
    grant_types_check, scope_check, string_array, timestamp_default, uuid_primary_key,
};

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager
            .create_table(
                Table::create()
                    .table(oauth::Entity)
                    .col(uuid_primary_key(oauth::Column::Id))
                    .col(uuid(oauth::Column::AccountId))
                    .col(string_len(oauth::Column::Name, 128))
                    .col(text_null(oauth::Column::Background))
                    .col(string_len_null(oauth::Column::ThemeColor, 32))
                    .col(text_null(oauth::Column::Avatar))
                    .col(text_null(oauth::Column::Description))
                    .col(string_len_null(oauth::Column::Email, 320))
                    .col(text_null(oauth::Column::TermsOfService))
                    .col(text_null(oauth::Column::PrivacyPolicy))
                    .col(boolean(oauth::Column::IsDeleted).default(false))
                    .col(timestamp_default(oauth::Column::CreatedAt))
                    .col(timestamp_default(oauth::Column::UpdatedAt))
                    .col(timestamp_with_time_zone_null(oauth::Column::DeletedAt))
                    .check((
                        "ck_oauths_soft_delete",
                        Expr::col(oauth::Column::IsDeleted)
                            .eq(Expr::col(oauth::Column::DeletedAt).is_not_null()),
                    ))
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("idx_oauths_account_id")
                    .table(oauth::Entity)
                    .col(oauth::Column::AccountId)
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(oauth_client::Entity)
                    .col(uuid_primary_key(oauth_client::Column::Id))
                    .col(uuid(oauth_client::Column::OauthId))
                    .col(text(oauth_client::Column::ClientSecretHash))
                    .col(string_array(oauth_client::Column::GrantTypes))
                    .col(string_array(oauth_client::Column::RedirectUris))
                    .col(string_array(oauth_client::Column::AllowedScopes))
                    .col(boolean(oauth_client::Column::IsDeleted).default(false))
                    .col(timestamp_default(oauth_client::Column::CreatedAt))
                    .col(timestamp_default(oauth_client::Column::UpdatedAt))
                    .col(timestamp_with_time_zone_null(
                        oauth_client::Column::DeletedAt,
                    ))
                    .check((
                        "ck_oauth_clients_soft_delete",
                        Expr::col(oauth_client::Column::IsDeleted)
                            .eq(Expr::col(oauth_client::Column::DeletedAt).is_not_null()),
                    ))
                    .check((
                        "ck_oauth_clients_grant_types",
                        grant_types_check(oauth_client::Column::GrantTypes),
                    ))
                    .check((
                        "ck_oauth_clients_allowed_scopes",
                        scope_check(oauth_client::Column::AllowedScopes),
                    ))
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("idx_oauth_clients_oauth_id")
                    .table(oauth_client::Entity)
                    .col(oauth_client::Column::OauthId)
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(account_oauth::Entity)
                    .col(uuid_primary_key(account_oauth::Column::Id))
                    .col(uuid(account_oauth::Column::AccountId))
                    .col(uuid(account_oauth::Column::OauthId))
                    .col(string_array(account_oauth::Column::Scope))
                    .col(timestamp_default(account_oauth::Column::CreatedAt))
                    .col(timestamp_default(account_oauth::Column::UpdatedAt))
                    .col(timestamp_with_time_zone_null(
                        account_oauth::Column::RevokedAt,
                    ))
                    .check((
                        "ck_account_oauths_scope",
                        scope_check(account_oauth::Column::Scope),
                    ))
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("idx_account_oauths_account_id")
                    .table(account_oauth::Entity)
                    .col(account_oauth::Column::AccountId)
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("idx_account_oauths_oauth_id")
                    .table(account_oauth::Entity)
                    .col(account_oauth::Column::OauthId)
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("uq_account_oauths_account_oauth_active")
                    .table(account_oauth::Entity)
                    .col(account_oauth::Column::AccountId)
                    .col(account_oauth::Column::OauthId)
                    .unique()
                    .and_where(Expr::col(account_oauth::Column::RevokedAt).is_null())
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(oauth_log::Entity)
                    .col(uuid_primary_key(oauth_log::Column::Id))
                    .col(uuid(oauth_log::Column::OauthId))
                    .col(timestamp_default(oauth_log::Column::CreatedAt))
                    .to_owned(),
            )
            .await?;

        manager
            .create_index(
                Index::create()
                    .name("idx_oauth_logs_oauth_created_at")
                    .table(oauth_log::Entity)
                    .col(oauth_log::Column::OauthId)
                    .col(oauth_log::Column::CreatedAt)
                    .to_owned(),
            )
            .await?;

        Ok(())
    }

    async fn down(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager
            .drop_table(Table::drop().table(oauth_log::Entity).to_owned())
            .await?;
        manager
            .drop_table(Table::drop().table(account_oauth::Entity).to_owned())
            .await?;
        manager
            .drop_table(Table::drop().table(oauth_client::Entity).to_owned())
            .await?;
        manager
            .drop_table(Table::drop().table(oauth::Entity).to_owned())
            .await?;

        Ok(())
    }
}
