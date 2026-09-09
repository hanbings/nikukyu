pub use sea_orm_migration::prelude::*;

mod m20220101_000001_create_table;
mod m20260908_000002_create_account_support_tables;
mod m20260908_000003_create_oauth_tables;
mod m20260908_000004_create_credentials_tables;
mod m20260909_000005_add_authentication_and_oidc;
mod schema_v1;

pub struct Migrator;

#[async_trait::async_trait]
impl MigratorTrait for Migrator {
    fn migrations() -> Vec<Box<dyn MigrationTrait>> {
        vec![
            Box::new(m20220101_000001_create_table::Migration),
            Box::new(m20260908_000002_create_account_support_tables::Migration),
            Box::new(m20260908_000003_create_oauth_tables::Migration),
            Box::new(m20260908_000004_create_credentials_tables::Migration),
            Box::new(m20260909_000005_add_authentication_and_oidc::Migration),
        ]
    }
}
