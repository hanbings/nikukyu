use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct Token {
    pub token: String,
    pub expire_in: i64,
    pub created_at: i64,
    pub created_by: i32,
    pub scopes: Vec<String>,
}
