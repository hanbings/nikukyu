use crate::dto::oidc::{JsonWebKey, JsonWebKeySet};
use argon2::{Argon2, PasswordHash, PasswordHasher, PasswordVerifier, password_hash::SaltString};
use base64::{Engine, engine::general_purpose::URL_SAFE_NO_PAD};
use jsonwebtoken::{Algorithm, DecodingKey, EncodingKey, Header, Validation};
use rand::{RngCore, rngs::OsRng};
use rsa::{
    RsaPrivateKey, pkcs1::DecodeRsaPrivateKey, pkcs8::DecodePrivateKey, traits::PublicKeyParts,
};
use serde::{Serialize, de::DeserializeOwned};
use sha2::{Digest, Sha256};
use subtle::ConstantTimeEq;

use crate::error::{ApiError, ApiResult};

pub fn secret() -> String {
    let mut bytes = [0_u8; 32];
    OsRng.fill_bytes(&mut bytes);
    URL_SAFE_NO_PAD.encode(bytes)
}
pub fn hash(value: &str) -> String {
    URL_SAFE_NO_PAD.encode(Sha256::digest(value.as_bytes()))
}
pub fn equal(left: &str, right: &str) -> bool {
    bool::from(left.as_bytes().ct_eq(right.as_bytes()))
}
pub fn csrf(secret: &str) -> String {
    hash(&format!("nikukyu-csrf:{secret}"))
}

pub async fn password_hash(password: String) -> ApiResult<String> {
    if !(12..=128).contains(&password.chars().count()) || password.len() > 512 {
        return Err(ApiError::bad("Password must contain 12 to 128 characters"));
    }
    actix_web::web::block(move || {
        Argon2::default()
            .hash_password(password.as_bytes(), &SaltString::generate(&mut OsRng))
            .map(|hash| hash.to_string())
            .map_err(|_| ApiError::internal())
    })
    .await
    .map_err(|_| ApiError::internal())?
}

pub async fn verify_password(password: String, encoded: String) -> ApiResult<bool> {
    if password.len() > 512 {
        return Ok(false);
    }
    actix_web::web::block(move || {
        PasswordHash::new(&encoded).is_ok_and(|hash| {
            Argon2::default()
                .verify_password(password.as_bytes(), &hash)
                .is_ok()
        })
    })
    .await
    .map_err(|_| ApiError::internal())
}

pub struct SigningKey {
    key: EncodingKey,
    verification_key: DecodingKey,
    kid: String,
    pub(super) jwks: JsonWebKeySet,
}

impl SigningKey {
    pub fn load(path: &str) -> Result<Self, Box<dyn std::error::Error>> {
        let pem = std::fs::read_to_string(path)?;
        let rsa =
            RsaPrivateKey::from_pkcs8_pem(&pem).or_else(|_| RsaPrivateKey::from_pkcs1_pem(&pem))?;
        if rsa.n().bits() < 2048 {
            return Err("OIDC signing key must have at least 2048 bits".into());
        }
        let n = URL_SAFE_NO_PAD.encode(rsa.n().to_bytes_be());
        let e = URL_SAFE_NO_PAD.encode(rsa.e().to_bytes_be());
        let kid = hash(&n);
        let verification_key = DecodingKey::from_rsa_components(&n, &e)?;
        let jwks = JsonWebKeySet {
            keys: vec![JsonWebKey {
                kty: "RSA",
                key_use: "sig",
                alg: "RS256",
                kid: kid.clone(),
                n,
                e,
            }],
        };
        Ok(Self {
            key: EncodingKey::from_rsa_pem(pem.as_bytes())?,
            verification_key,
            kid,
            jwks,
        })
    }
    pub(super) fn sign(&self, claims: &impl Serialize) -> ApiResult<String> {
        self.sign_with_type(claims, "JWT")
    }

    fn sign_with_type(&self, claims: &impl Serialize, token_type: &str) -> ApiResult<String> {
        let mut header = Header::new(Algorithm::RS256);
        header.kid = Some(self.kid.clone());
        header.typ = Some(token_type.to_owned());
        jsonwebtoken::encode(&header, claims, &self.key).map_err(|_| ApiError::internal())
    }

    pub(super) fn sign_authorization(&self, claims: &impl Serialize) -> ApiResult<String> {
        self.sign_with_type(claims, "nikukyu-authorization+jwt")
    }

    pub(super) fn verify_authorization<T: DeserializeOwned>(
        &self,
        raw: &str,
        issuer: &str,
    ) -> ApiResult<T> {
        let invalid = || ApiError::bad("Invalid or expired authorization request; start again");
        if raw.len() > 12288 {
            return Err(invalid());
        }
        let mut validation = Validation::new(Algorithm::RS256);
        validation.set_issuer(&[issuer]);
        validation.set_audience(&["nikukyu:authorization"]);
        validation.leeway = 0;
        let token = jsonwebtoken::decode::<T>(raw, &self.verification_key, &validation)
            .map_err(|_| invalid())?;
        if token.header.typ.as_deref() != Some("nikukyu-authorization+jwt") {
            return Err(invalid());
        }
        Ok(token.claims)
    }
}
