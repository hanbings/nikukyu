use sea_orm_migration::{prelude::*, sea_orm::ConnectionTrait};

#[derive(DeriveMigrationName)]
pub struct Migration;

// Protocol values are snapshots: changing a Rust enum must not rewrite migration history.
const SCOPES: &str = "'oauth_email_verify','email_verify',\
    'account_read','account_write','account_destroy',\
    'account_authorization_read','account_authorization_write','account_authorization_destroy',\
    'account_oauth_read','account_oauth_write','account_log_read','account_log_write',\
    'oauth_read','oauth_write','oauth_destroy','oauth_client_read','oauth_client_write',\
    'oauth_client_destroy','oauth_log_read','oauth_log_write',\
    'oauth_authorize','oauth_token','oauth_refresh','oauth_revoke'";
const SCOPE_COLUMNS: &[(&str, &str, &str)] = &[
    (
        "oauth_clients",
        "allowed_scopes",
        "ck_oauth_clients_allowed_scopes",
    ),
    ("account_oauths", "scope", "ck_account_oauths_scope"),
    (
        "authorization_codes",
        "scope",
        "ck_authorization_codes_scope",
    ),
    ("tokens", "scope", "ck_tokens_scope"),
];

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        let db = manager.get_connection();
        for sql in [
            "CREATE TABLE account_passwords (
                account_id uuid PRIMARY KEY,
                password_hash text NOT NULL,
                created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
            )",
            "CREATE TABLE account_sessions (
                id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                account_id uuid NOT NULL,
                secret_hash text NOT NULL UNIQUE,
                created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
                expires_at timestamptz NOT NULL,
                revoked_at timestamptz
            )",
            "CREATE INDEX idx_account_sessions_account_id ON account_sessions (account_id)",
            "CREATE INDEX idx_account_sessions_expires_at ON account_sessions (expires_at)",
            "CREATE TABLE github_login_states (
                id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                state_hash text NOT NULL UNIQUE,
                browser_hash text NOT NULL,
                code_verifier text NOT NULL,
                account_id uuid,
                session_id uuid,
                return_to text NOT NULL,
                created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
                expires_at timestamptz NOT NULL,
                used_at timestamptz
            )",
            "CREATE INDEX idx_github_login_states_expires_at ON github_login_states (expires_at)",
            "ALTER TABLE oauth_clients
                ALTER COLUMN client_secret_hash DROP NOT NULL,
                ADD COLUMN token_endpoint_auth_method varchar(32) NOT NULL DEFAULT 'client_secret_basic',
                ADD CONSTRAINT ck_oauth_clients_auth_method CHECK (
                    (token_endpoint_auth_method = 'none' AND client_secret_hash IS NULL) OR
                    (token_endpoint_auth_method IN ('client_secret_basic', 'client_secret_post')
                        AND client_secret_hash IS NOT NULL)
                )",
            "ALTER TABLE authorization_codes
                ADD COLUMN account_oauth_id uuid,
                ADD COLUMN nonce text,
                ADD COLUMN auth_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE tokens
                ADD COLUMN authorization_code_id uuid,
                ADD COLUMN nonce text,
                ADD COLUMN auth_time timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "CREATE INDEX idx_tokens_authorization_code_id ON tokens (authorization_code_id)",
        ] {
            db.execute_unprepared(sql).await?;
        }
        for (table, column, constraint) in SCOPE_COLUMNS {
            db.execute_unprepared(&format!(
                "ALTER TABLE {table} DROP CONSTRAINT {constraint},
                 ADD CONSTRAINT {constraint} CHECK (
                    {column} <@ ARRAY[{SCOPES},'openid','profile','email','offline_access']::varchar[]
                    AND array_position({column}, NULL) IS NULL
                 )"
            )).await?;
        }
        Ok(())
    }

    async fn down(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        let db = manager.get_connection();
        // Refuse incompatible downgrades rather than silently deleting public clients or scopes.
        for (table, column, constraint) in SCOPE_COLUMNS {
            db.execute_unprepared(&format!(
                "ALTER TABLE {table} DROP CONSTRAINT {constraint},
                 ADD CONSTRAINT {constraint} CHECK (
                    {column} <@ ARRAY[{SCOPES}]::varchar[] AND array_position({column}, NULL) IS NULL
                 )"
            )).await?;
        }
        for sql in [
            "ALTER TABLE oauth_clients ALTER COLUMN client_secret_hash SET NOT NULL,
                DROP CONSTRAINT ck_oauth_clients_auth_method,
                DROP COLUMN token_endpoint_auth_method",
            "ALTER TABLE tokens DROP COLUMN authorization_code_id, DROP COLUMN nonce, DROP COLUMN auth_time",
            "ALTER TABLE authorization_codes DROP COLUMN account_oauth_id, DROP COLUMN nonce, DROP COLUMN auth_time",
            "DROP TABLE github_login_states",
            "DROP TABLE account_sessions",
            "DROP TABLE account_passwords",
        ] {
            db.execute_unprepared(sql).await?;
        }
        Ok(())
    }
}
