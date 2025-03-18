use sea_orm::prelude::DateTime;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct GetAccount {
    pub id: i32,
    pub openid: String,
    pub created_at: DateTime,
    pub updated_at: Option<DateTime>,
    pub username: String,
    pub nickname: Option<String>,
    pub avatar: Option<String>,
    pub email: String,
    pub background: Option<String>,
    pub color: Option<String>,
}

impl From<&crate::entity::account::Model> for GetAccount {
    fn from(account: &crate::entity::account::Model) -> Self {
        Self {
            id: account.id,
            openid: account.openid.clone(),
            created_at: account.created_at,
            updated_at: account.updated_at,
            username: account.username.clone(),
            nickname: account.nickname.clone(),
            avatar: account.avatar.clone(),
            email: account.email.clone(),
            background: account.background.clone(),
            color: account.color.clone(),
        }
    }
}

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct UpdateAccount {
    pub nickname: Option<String>,
    pub avatar: Option<String>,
    pub background: Option<String>,
    pub color: Option<String>,
}
