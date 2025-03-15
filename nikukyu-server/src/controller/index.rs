use axum::Json;
use serde_json::{Value, json};

pub async fn index() -> Json<Value> {
    Json(json!(
        {
            "version": "1.0.0",
            "repository": "https://github.com/hanbings/nikukyu",
            "project": "Nikukyu",
            "author": "hanbings <hanbings@hanbings.io>",
            "license": "Apache-2.0"
        }
    ))
}
