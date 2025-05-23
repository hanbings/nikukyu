use serde::{Deserialize, Serialize};

use crate::entity::account::Model;

use super::token::Token;

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct ActiveToken {
    pub token: Token,
    pub account: Model,
}
