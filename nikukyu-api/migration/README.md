# Running Migrator CLI

Run these commands from `nikukyu-api/migration`, with `DATABASE_URL` set to the
target PostgreSQL database. From `nikukyu-api`, use `cargo run -p migration -- up`
or `cargo run -p migration -- status` instead.

## Schema

Migrations run in this order and roll back in reverse order:

| Migration | Tables |
| --- | --- |
| `m20220101_000001_create_table` | `accounts`, `account_emails` |
| `m20260908_000002_create_account_support_tables` | `account_authorizations`, `account_logs` |
| `m20260908_000003_create_oauth_tables` | `oauths`, `oauth_clients`, `account_oauths`, `oauth_logs` |
| `m20260908_000004_create_credentials_tables` | `authorization_codes`, `tokens` |

The original migration is unchanged, so databases with accounts and emails already
migrated can apply the remaining three migrations using `up`.

- UUID primary keys default to `gen_random_uuid()`. Creation and update timestamps
  default to the current timestamp on insertion; services must maintain `updated_at`
  on subsequent updates.
- Scope and grant-type lists use PostgreSQL `varchar[]`, defaulting to empty lists.
  Enum checks use the frozen values in `schema_v1.rs`; adding protocol values later
  requires a new migration to update those checks.
- Active third-party bindings are unique by `(provider, openid)`; unrevoked account
  grants are unique by `(account_id, oauth_id)`. Deleted or revoked rows retain their
  history without blocking a new binding or grant.
- Authorization-code, access-token, and non-null refresh-token hashes are unique,
  including consumed or revoked records. Multiple null refresh-token hashes are allowed.
- Soft-delete, consumed, revoked, and refreshed flags must agree with their timestamps.
  PKCE challenge and method must both be present or both be absent.
- Relations follow the original migration's approach: UUID references have lookup
  indexes without database foreign keys. Services must validate referenced records,
  account/application/client ownership, and authorization scope relationships.
- These migrations preserve the current models, including the minimal log tables
  and the required `tokens.account_id` for every grant type.

## Commands

- Generate a new migration file
    ```sh
    cargo run -- generate MIGRATION_NAME
    ```
- Apply all pending migrations
    ```sh
    cargo run
    ```
    ```sh
    cargo run -- up
    ```
- Apply first 10 pending migrations
    ```sh
    cargo run -- up -n 10
    ```
- Rollback last applied migrations
    ```sh
    cargo run -- down
    ```
- Rollback last 10 applied migrations
    ```sh
    cargo run -- down -n 10
    ```
- Drop all tables from the database, then reapply all migrations
    ```sh
    cargo run -- fresh
    ```
- Rollback all applied migrations, then reapply all migrations
    ```sh
    cargo run -- refresh
    ```
- Rollback all applied migrations
    ```sh
    cargo run -- reset
    ```
- Check the status of all migrations
    ```sh
    cargo run -- status
    ```
