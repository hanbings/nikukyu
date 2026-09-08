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
