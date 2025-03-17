use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct Token {
    pub token: String,
    pub expire_in: u64,
    pub created_at: u64,
    pub created_by: i32,
    pub scopes: Vec<String>,
}
