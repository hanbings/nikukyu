use std::ops::Deref;

use sea_orm::{ConnectOptions, Database as SeaOrmDatabase, DatabaseConnection, DbErr};

use crate::config::DatabaseConfig;

pub struct Database(DatabaseConnection);

impl Database {
    pub async fn connect(config: &DatabaseConfig) -> Result<Self, DbErr> {
        let mut options = ConnectOptions::new(config.url.clone());
        options.sqlx_logging(false);

        let connection = SeaOrmDatabase::connect(options).await?;
        connection.ping().await?;

        Ok(Self(connection))
    }
}

impl Deref for Database {
    type Target = DatabaseConnection;

    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

/// Serialize operations that share an identity or token family within a transaction.
pub async fn lock(connection: &impl sea_orm::ConnectionTrait, key: &str) -> Result<(), DbErr> {
    connection
        .execute_raw(sea_orm::Statement::from_sql_and_values(
            sea_orm::DbBackend::Postgres,
            "SELECT pg_advisory_xact_lock(hashtextextended($1, 0))",
            [key.into()],
        ))
        .await?;
    Ok(())
}
