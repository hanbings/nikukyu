use sea_orm_migration::prelude::*;

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager.create_table(Account::table()).await?;
        manager.create_table(OAuth::table()).await?;
        manager.create_table(OAuthClient::table()).await?;
        manager.create_table(OAuthLog::table()).await?;
        manager.create_table(AccountOAuth::table()).await?;
        manager.create_table(AccountLog::table()).await?;
        manager.create_table(AccountAuthorization::table()).await?;

        Ok(())
    }

    async fn down(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        manager.drop_table(OAuthLog::drop_table()).await?;
        manager.drop_table(OAuthClient::drop_table()).await?;
        manager.drop_table(OAuth::drop_table()).await?;
        manager.drop_table(AccountOAuth::drop_table()).await?;
        manager.drop_table(AccountLog::drop_table()).await?;
        manager
            .drop_table(AccountAuthorization::drop_table())
            .await?;
        manager.drop_table(Account::drop_table()).await?;

        Ok(())
    }
}

#[derive(DeriveIden)]
enum Account {
    #[sea_orm(iden = "account")]
    Table,
    #[sea_orm(iden = "id")]
    Id,
    #[sea_orm(iden = "openid")]
    OpenId,
    #[sea_orm(iden = "created_at")]
    CreatedAt,
    #[sea_orm(iden = "updated_at")]
    UpdatedAt,
    #[sea_orm(iden = "username")]
    Username,
    #[sea_orm(iden = "nickname")]
    Nickname,
    #[sea_orm(iden = "avatar")]
    Avatar,
    #[sea_orm(iden = "email")]
    Email,
    #[sea_orm(iden = "background")]
    Background,
    #[sea_orm(iden = "color")]
    Color,
}

impl Account {
    pub fn table() -> TableCreateStatement {
        Table::create()
            .table(Account::Table)
            .if_not_exists()
            .col(
                ColumnDef::new(Account::Id)
                    .integer()
                    .not_null()
                    .auto_increment()
                    .primary_key(),
            )
            .col(
                ColumnDef::new(Account::OpenId)
                    .string()
                    .unique_key()
                    .not_null(),
            )
            .col(ColumnDef::new(Account::CreatedAt).timestamp().not_null())
            .col(ColumnDef::new(Account::UpdatedAt).timestamp().null())
            .col(
                ColumnDef::new(Account::Username)
                    .string()
                    .unique_key()
                    .not_null(),
            )
            .col(ColumnDef::new(Account::Nickname).string().null())
            .col(ColumnDef::new(Account::Avatar).string().null())
            .col(
                ColumnDef::new(Account::Email)
                    .string()
                    .unique_key()
                    .not_null(),
            )
            .col(ColumnDef::new(Account::Background).string().null())
            .col(ColumnDef::new(Account::Color).string().null())
            .to_owned()
    }

    pub fn drop_table() -> TableDropStatement {
        Table::drop().table(Account::Table).to_owned()
    }
}

#[derive(DeriveIden)]
enum AccountAuthorization {
    #[sea_orm(iden = "account_authorization")]
    Table,
    #[sea_orm(iden = "id")]
    Id,
    #[sea_orm(iden = "created_at")]
    CreatedAt,
    #[sea_orm(iden = "updated_at")]
    UpdatedAt,
    #[sea_orm(iden = "created_by")]
    CreatedBy,
    #[sea_orm(iden = "provider")]
    Provider,
    #[sea_orm(iden = "openid")]
    Openid,
}

impl AccountAuthorization {
    pub fn table() -> TableCreateStatement {
        Table::create()
            .table(AccountAuthorization::Table)
            .if_not_exists()
            .col(
                ColumnDef::new(AccountAuthorization::Id)
                    .integer()
                    .auto_increment()
                    .primary_key()
                    .not_null(),
            )
            .col(
                ColumnDef::new(AccountAuthorization::CreatedAt)
                    .timestamp()
                    .not_null(),
            )
            .col(
                ColumnDef::new(AccountAuthorization::UpdatedAt)
                    .timestamp()
                    .null(),
            )
            .col(
                ColumnDef::new(AccountAuthorization::CreatedBy)
                    .integer()
                    .not_null(),
            )
            .col(
                ColumnDef::new(AccountAuthorization::Provider)
                    .string()
                    .not_null(),
            )
            .col(
                ColumnDef::new(AccountAuthorization::Openid)
                    .string()
                    .not_null(),
            )
            .foreign_key(
                ForeignKey::create()
                    .name("account_id")
                    .from(AccountAuthorization::Table, AccountAuthorization::CreatedBy)
                    .to(Account::Table, Account::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .to_owned()
    }

    pub fn drop_table() -> TableDropStatement {
        Table::drop().table(AccountAuthorization::Table).to_owned()
    }
}

#[derive(DeriveIden)]
enum AccountLog {
    #[sea_orm(iden = "account_log")]
    Table,
    #[sea_orm(iden = "id")]
    Id,
    #[sea_orm(iden = "created_at")]
    CreatedAt,
    #[sea_orm(iden = "created_by")]
    CreatedBy,
    #[sea_orm(iden = "ip")]
    Ip,
    #[sea_orm(iden = "type")]
    Type,
}

impl AccountLog {
    pub fn table() -> TableCreateStatement {
        Table::create()
            .table(AccountLog::Table)
            .if_not_exists()
            .col(
                ColumnDef::new(AccountLog::Id)
                    .integer()
                    .not_null()
                    .auto_increment()
                    .primary_key(),
            )
            .col(ColumnDef::new(AccountLog::CreatedAt).timestamp().not_null())
            .col(ColumnDef::new(AccountLog::CreatedBy).integer().not_null())
            .col(ColumnDef::new(AccountLog::Ip).string().not_null())
            .col(ColumnDef::new(AccountLog::Type).string().not_null())
            .foreign_key(
                ForeignKey::create()
                    .name("account_id")
                    .from(AccountLog::Table, AccountLog::CreatedBy)
                    .to(Account::Table, Account::Id),
            )
            .to_owned()
    }

    pub fn drop_table() -> TableDropStatement {
        Table::drop().table(AccountLog::Table).to_owned()
    }
}

#[derive(DeriveIden)]
enum AccountOAuth {
    #[sea_orm(iden = "account_oauth")]
    Table,
    #[sea_orm(iden = "id")]
    Id,
    #[sea_orm(iden = "created_at")]
    CreatedAt,
    #[sea_orm(iden = "updated_at")]
    UpdatedAt,
    #[sea_orm(iden = "created_by")]
    CreatedBy,
    #[sea_orm(iden = "oauth_id")]
    OAuthId,
    #[sea_orm(iden = "access")]
    Access,
}

impl AccountOAuth {
    pub fn table() -> TableCreateStatement {
        Table::create()
            .table(AccountOAuth::Table)
            .if_not_exists()
            .col(
                ColumnDef::new(AccountOAuth::Id)
                    .integer()
                    .not_null()
                    .auto_increment()
                    .primary_key(),
            )
            .col(
                ColumnDef::new(AccountOAuth::CreatedAt)
                    .timestamp()
                    .not_null(),
            )
            .col(ColumnDef::new(AccountOAuth::UpdatedAt).timestamp().null())
            .col(ColumnDef::new(AccountOAuth::CreatedBy).integer().not_null())
            .col(ColumnDef::new(AccountOAuth::OAuthId).integer().not_null())
            .col(
                ColumnDef::new(AccountOAuth::Access)
                    .array(ColumnType::Text)
                    .not_null(),
            )
            .foreign_key(
                ForeignKey::create()
                    .name("account_id")
                    .from(AccountOAuth::Table, AccountOAuth::CreatedBy)
                    .to(Account::Table, Account::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .foreign_key(
                ForeignKey::create()
                    .name("oauth_id")
                    .from(AccountOAuth::Table, AccountOAuth::OAuthId)
                    .to(OAuth::Table, OAuth::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .to_owned()
    }

    pub fn drop_table() -> TableDropStatement {
        Table::drop().table(AccountOAuth::Table).to_owned()
    }
}

#[derive(DeriveIden)]
enum OAuth {
    #[sea_orm(iden = "oauth")]
    Table,
    #[sea_orm(iden = "id")]
    Id,
    #[sea_orm(iden = "created_at")]
    CreatedAt,
    #[sea_orm(iden = "updated_at")]
    UpdatedAt,
    #[sea_orm(iden = "created_by")]
    CreatedBy,
    #[sea_orm(iden = "redirect")]
    Redirect,
    #[sea_orm(iden = "access")]
    Access,
    #[sea_orm(iden = "avatar")]
    Avatar,
    #[sea_orm(iden = "name")]
    Name,
    #[sea_orm(iden = "description")]
    Description,
    #[sea_orm(iden = "homepage")]
    Homepage,
    #[sea_orm(iden = "background")]
    Background,
    #[sea_orm(iden = "theme")]
    Theme,
    #[sea_orm(iden = "policy")]
    Policy,
    #[sea_orm(iden = "tos")]
    Tos,
}

impl OAuth {
    pub fn table() -> TableCreateStatement {
        Table::create()
            .table(OAuth::Table)
            .if_not_exists()
            .col(
                ColumnDef::new(OAuth::Id)
                    .integer()
                    .not_null()
                    .auto_increment()
                    .primary_key(),
            )
            .col(ColumnDef::new(OAuth::CreatedAt).timestamp().not_null())
            .col(ColumnDef::new(OAuth::UpdatedAt).timestamp().null())
            .col(ColumnDef::new(OAuth::CreatedBy).integer().not_null())
            .col(
                ColumnDef::new(OAuth::Redirect)
                    .array(ColumnType::Text)
                    .not_null(),
            )
            .col(
                ColumnDef::new(OAuth::Access)
                    .array(ColumnType::Text)
                    .not_null(),
            )
            .col(ColumnDef::new(OAuth::Avatar).string().null())
            .col(ColumnDef::new(OAuth::Name).string().not_null())
            .col(ColumnDef::new(OAuth::Description).string().null())
            .col(ColumnDef::new(OAuth::Homepage).string().null())
            .col(ColumnDef::new(OAuth::Background).string().null())
            .col(ColumnDef::new(OAuth::Theme).string().null())
            .col(ColumnDef::new(OAuth::Policy).string().null())
            .col(ColumnDef::new(OAuth::Tos).string().null())
            .foreign_key(
                ForeignKey::create()
                    .name("account_id")
                    .from(OAuth::Table, OAuth::CreatedBy)
                    .to(Account::Table, Account::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .to_owned()
    }

    pub fn drop_table() -> TableDropStatement {
        Table::drop().table(OAuth::Table).to_owned()
    }
}

#[derive(DeriveIden)]
enum OAuthClient {
    #[sea_orm(iden = "oauth_client")]
    Table,
    #[sea_orm(iden = "id")]
    Id,
    #[sea_orm(iden = "oauth_id")]
    OAuthId,
    #[sea_orm(iden = "created_at")]
    CreatedAt,
    #[sea_orm(iden = "updated_at")]
    UpdatedAt,
    #[sea_orm(iden = "created_by")]
    CreatedBy,
    #[sea_orm(iden = "name")]
    Name,
    #[sea_orm(iden = "description")]
    Description,
    #[sea_orm(iden = "client_id")]
    ClientId,
    #[sea_orm(iden = "secret")]
    Secret,
    #[sea_orm(iden = "expire")]
    Expire,
}

impl OAuthClient {
    pub fn table() -> TableCreateStatement {
        Table::create()
            .table(OAuthClient::Table)
            .if_not_exists()
            .col(
                ColumnDef::new(OAuthClient::Id)
                    .integer()
                    .not_null()
                    .auto_increment()
                    .primary_key(),
            )
            .col(ColumnDef::new(OAuthClient::OAuthId).integer().not_null())
            .col(
                ColumnDef::new(OAuthClient::CreatedAt)
                    .timestamp()
                    .not_null(),
            )
            .col(ColumnDef::new(OAuthClient::UpdatedAt).timestamp().null())
            .col(ColumnDef::new(OAuthClient::CreatedBy).integer().not_null())
            .col(ColumnDef::new(OAuthClient::Name).string().not_null())
            .col(ColumnDef::new(OAuthClient::Description).string().null())
            .col(ColumnDef::new(OAuthClient::ClientId).string().not_null())
            .col(ColumnDef::new(OAuthClient::Secret).string().not_null())
            .col(ColumnDef::new(OAuthClient::Expire).timestamp().not_null())
            .foreign_key(
                ForeignKey::create()
                    .name("oauth_id")
                    .from(OAuthClient::Table, OAuthClient::OAuthId)
                    .to(OAuth::Table, OAuth::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .foreign_key(
                ForeignKey::create()
                    .name("account_id")
                    .from(OAuthClient::Table, OAuthClient::CreatedBy)
                    .to(Account::Table, Account::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .to_owned()
    }

    pub fn drop_table() -> TableDropStatement {
        Table::drop().table(OAuthClient::Table).to_owned()
    }
}

#[derive(DeriveIden)]
enum OAuthLog {
    #[sea_orm(iden = "oauth_log")]
    Table,
    #[sea_orm(iden = "id")]
    Id,
    #[sea_orm(iden = "oauth_id")]
    OAuthId,
    #[sea_orm(iden = "created_at")]
    CreatedAt,
    #[sea_orm(iden = "created_by")]
    CreatedBy,
    #[sea_orm(iden = "ip")]
    Ip,
    #[sea_orm(iden = "type")]
    Type,
}

impl OAuthLog {
    pub fn table() -> TableCreateStatement {
        Table::create()
            .table(OAuthLog::Table)
            .if_not_exists()
            .col(
                ColumnDef::new(OAuthLog::Id)
                    .integer()
                    .not_null()
                    .auto_increment()
                    .primary_key(),
            )
            .col(ColumnDef::new(OAuthLog::OAuthId).integer().not_null())
            .col(ColumnDef::new(OAuthLog::CreatedAt).timestamp().not_null())
            .col(ColumnDef::new(OAuthLog::CreatedBy).integer().not_null())
            .col(ColumnDef::new(OAuthLog::Ip).string().not_null())
            .col(ColumnDef::new(OAuthLog::Type).string().not_null())
            .foreign_key(
                ForeignKey::create()
                    .name("oauth_id")
                    .from(OAuthLog::Table, OAuthLog::OAuthId)
                    .to(OAuth::Table, OAuth::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .foreign_key(
                ForeignKey::create()
                    .name("account_id")
                    .from(OAuthLog::Table, OAuthLog::CreatedBy)
                    .to(Account::Table, Account::Id)
                    .on_delete(ForeignKeyAction::Cascade),
            )
            .to_owned()
    }

    pub fn drop_table() -> TableDropStatement {
        Table::drop().table(OAuthLog::Table).to_owned()
    }
}
