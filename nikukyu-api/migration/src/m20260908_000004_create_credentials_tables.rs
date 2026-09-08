use sea_orm_migration::{prelude::*, schema::*};

use models::{authorization_code, token};

use crate::schema_v1::{scope_check, string_array, timestamp_default, uuid_primary_key};

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager
            .create_table(
                Table::create()
                    .table(authorization_code::Entity)
                    .col(uuid_primary_key(authorization_code::Column::Id))
                    .col(uuid(authorization_code::Column::AccountId))
                    .col(uuid(authorization_code::Column::OauthId))
                    .col(uuid(authorization_code::Column::OauthClientId))
                    .col(text_uniq(authorization_code::Column::CodeHash))
                    .col(text(authorization_code::Column::RedirectUri))
                    .col(string_array(authorization_code::Column::Scope))
                    .col(text_null(authorization_code::Column::CodeChallenge))
                    .col(string_len_null(
                        authorization_code::Column::CodeChallengeMethod,
                        16,
                    ))
                    .col(boolean(authorization_code::Column::IsConsumed).default(false))
                    .col(timestamp_default(authorization_code::Column::CreatedAt))
                    .col(timestamp_with_time_zone(
                        authorization_code::Column::ExpiresAt,
                    ))
                    .col(timestamp_with_time_zone_null(
                        authorization_code::Column::ConsumedAt,
                    ))
                    .check((
                        "ck_authorization_codes_scope",
                        scope_check(authorization_code::Column::Scope),
                    ))
                    .check((
                        "ck_authorization_codes_consumed",
                        Expr::col(authorization_code::Column::IsConsumed)
                            .eq(Expr::col(authorization_code::Column::ConsumedAt).is_not_null()),
                    ))
                    .check((
                        "ck_authorization_codes_pkce_pair",
                        Expr::col(authorization_code::Column::CodeChallenge)
                            .is_null()
                            .eq(Expr::col(authorization_code::Column::CodeChallengeMethod)
                                .is_null()),
                    ))
                    .check((
                        "ck_authorization_codes_expiry",
                        Expr::col(authorization_code::Column::ExpiresAt)
                            .gt(Expr::col(authorization_code::Column::CreatedAt)),
                    ))
                    .to_owned(),
            )
            .await?;

        for (name, column) in [
            (
                "idx_authorization_codes_account_id",
                authorization_code::Column::AccountId,
            ),
            (
                "idx_authorization_codes_oauth_id",
                authorization_code::Column::OauthId,
            ),
            (
                "idx_authorization_codes_client_id",
                authorization_code::Column::OauthClientId,
            ),
            (
                "idx_authorization_codes_expires_at",
                authorization_code::Column::ExpiresAt,
            ),
        ] {
            manager
                .create_index(
                    Index::create()
                        .name(name)
                        .table(authorization_code::Entity)
                        .col(column)
                        .to_owned(),
                )
                .await?;
        }

        manager
            .create_table(
                Table::create()
                    .table(token::Entity)
                    .col(uuid_primary_key(token::Column::Id))
                    .col(uuid(token::Column::AccountId))
                    .col(uuid_null(token::Column::OauthId))
                    .col(uuid_null(token::Column::OauthClientId))
                    .col(string_array(token::Column::Scope))
                    .col(string_len(token::Column::TokenType, 32).default("Bearer"))
                    .col(string_len(token::Column::GrantType, 32))
                    .col(uuid_null(token::Column::AccountOauthId))
                    .col(uuid_null(token::Column::TokenFamilyId))
                    .col(text_uniq(token::Column::AccessTokenHash))
                    .col(uuid_null(token::Column::PreviousTokenId))
                    .col(text_null(token::Column::RefreshTokenHash).unique_key())
                    .col(boolean(token::Column::IsRevoked).default(false))
                    .col(boolean(token::Column::IsRefreshed).default(false))
                    .col(timestamp_default(token::Column::CreatedAt))
                    .col(timestamp_default(token::Column::UpdatedAt))
                    .col(timestamp_with_time_zone_null(token::Column::ExpiresAt))
                    .col(timestamp_with_time_zone_null(token::Column::RevokedAt))
                    .col(timestamp_with_time_zone_null(token::Column::RefreshedAt))
                    .col(timestamp_with_time_zone_null(
                        token::Column::RefreshExpiresAt,
                    ))
                    .col(timestamp_with_time_zone_null(token::Column::LastUsedAt))
                    .check(("ck_tokens_scope", scope_check(token::Column::Scope)))
                    .check((
                        "ck_tokens_token_type",
                        Expr::col(token::Column::TokenType).eq("Bearer"),
                    ))
                    .check((
                        "ck_tokens_grant_type",
                        Expr::col(token::Column::GrantType).is_in([
                            "authorization_code",
                            "refresh_token",
                            "client_credentials",
                        ]),
                    ))
                    .check((
                        "ck_tokens_revoked",
                        Expr::col(token::Column::IsRevoked)
                            .eq(Expr::col(token::Column::RevokedAt).is_not_null()),
                    ))
                    .check((
                        "ck_tokens_refreshed",
                        Expr::col(token::Column::IsRefreshed)
                            .eq(Expr::col(token::Column::RefreshedAt).is_not_null()),
                    ))
                    .to_owned(),
            )
            .await?;

        for (name, column) in [
            ("idx_tokens_account_id", token::Column::AccountId),
            ("idx_tokens_oauth_id", token::Column::OauthId),
            ("idx_tokens_client_id", token::Column::OauthClientId),
            ("idx_tokens_account_oauth_id", token::Column::AccountOauthId),
            ("idx_tokens_family_id", token::Column::TokenFamilyId),
            (
                "idx_tokens_previous_token_id",
                token::Column::PreviousTokenId,
            ),
            ("idx_tokens_expires_at", token::Column::ExpiresAt),
            (
                "idx_tokens_refresh_expires_at",
                token::Column::RefreshExpiresAt,
            ),
        ] {
            manager
                .create_index(
                    Index::create()
                        .name(name)
                        .table(token::Entity)
                        .col(column)
                        .to_owned(),
                )
                .await?;
        }

        Ok(())
    }

    async fn down(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager
            .drop_table(Table::drop().table(token::Entity).to_owned())
            .await?;
        manager
            .drop_table(Table::drop().table(authorization_code::Entity).to_owned())
            .await?;

        Ok(())
    }
}
