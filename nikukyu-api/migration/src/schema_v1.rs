//! Frozen helpers and protocol values for the September 2026 migrations.
//! Add later schema changes in new migrations instead of changing these values.

use sea_orm_migration::{prelude::*, schema::*};

pub fn uuid_primary_key(column: impl IntoIden) -> ColumnDef {
    pk_uuid(column)
        .default(Expr::cust("gen_random_uuid()"))
        .take()
}

pub fn timestamp_default(column: impl IntoIden) -> ColumnDef {
    timestamp_with_time_zone(column)
        .default(Expr::current_timestamp())
        .take()
}

pub fn string_array(column: impl IntoIden) -> ColumnDef {
    ColumnDef::new(column)
        .array(ColumnType::String(StringLen::None))
        .not_null()
        .default(Expr::cust("ARRAY[]::varchar[]"))
        .take()
}

pub fn scope_check(column: impl IntoIden) -> Expr {
    Expr::cust_with_exprs(
        "$1 <@ ARRAY[\
            'oauth_email_verify', 'email_verify', \
            'account_read', 'account_write', 'account_destroy', \
            'account_authorization_read', 'account_authorization_write', \
            'account_authorization_destroy', 'account_oauth_read', 'account_oauth_write', \
            'account_log_read', 'account_log_write', \
            'oauth_read', 'oauth_write', 'oauth_destroy', \
            'oauth_client_read', 'oauth_client_write', 'oauth_client_destroy', \
            'oauth_log_read', 'oauth_log_write', \
            'oauth_authorize', 'oauth_token', 'oauth_refresh', 'oauth_revoke'\
        ]::varchar[] AND array_position($1, NULL) IS NULL",
        [Expr::col(column)],
    )
}

pub fn grant_types_check(column: impl IntoIden) -> Expr {
    Expr::cust_with_exprs(
        "$1 <@ ARRAY['authorization_code', 'refresh_token', 'client_credentials']::varchar[] \
         AND array_position($1, NULL) IS NULL",
        [Expr::col(column)],
    )
}
