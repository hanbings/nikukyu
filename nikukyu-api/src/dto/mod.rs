//! HTTP contracts. This layer does not depend on persistence models.
pub mod account;
pub mod oauth;
pub mod oidc;
pub mod session;

use serde::{Deserialize, Deserializer, Serialize};

#[derive(Serialize)]
pub struct ListResponse<T> {
    pub items: Vec<T>,
}

#[derive(Serialize)]
pub struct ErrorResponse<'a> {
    pub error: &'a str,
    pub error_description: &'a str,
}

/// Missing leaves a field unchanged; explicit null clears it.
#[derive(Default)]
pub enum Patch<T> {
    #[default]
    Missing,
    Null,
    Value(T),
}

impl<'de, T: Deserialize<'de>> Deserialize<'de> for Patch<T> {
    fn deserialize<D: Deserializer<'de>>(deserializer: D) -> Result<Self, D::Error> {
        Ok(match Option::<T>::deserialize(deserializer)? {
            Some(value) => Self::Value(value),
            None => Self::Null,
        })
    }
}
