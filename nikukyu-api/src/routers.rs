use crate::controller::{account, login, oauth, oidc};
use actix_web::web;

pub fn configure(config: &mut web::ServiceConfig) {
    config
        .route(
            "/.well-known/openid-configuration",
            web::get().to(oidc::discovery),
        )
        .service(
            web::scope("/oidc")
                .route("/jwks", web::get().to(oidc::jwks))
                .service(
                    web::resource("/authorize")
                        .route(web::get().to(oidc::authorize_get))
                        .route(web::post().to(oidc::authorize_post)),
                )
                .route("/token", web::post().to(oidc::token))
                .route("/revoke", web::post().to(oidc::revoke))
                .service(
                    web::resource("/userinfo")
                        .route(web::get().to(oidc::userinfo))
                        .route(web::post().to(oidc::userinfo)),
                ),
        )
        .service(
            web::scope("/api/v1")
                .route(
                    "/oauth-clients/{client_id}",
                    web::get().to(oauth::public_client),
                )
                .route(
                    "/oidc/authorization-contexts",
                    web::post().to(oidc::authorization_context),
                )
                .route(
                    "/oidc/authorizations",
                    web::post().to(oidc::authorization_decision),
                )
                .route("/accounts", web::post().to(account::create))
                .service(
                    web::resource("/accounts/me")
                        .route(web::get().to(account::current))
                        .route(web::patch().to(account::update)),
                )
                .route(
                    "/accounts/me/authorizations",
                    web::get().to(account::authorizations),
                )
                .route(
                    "/accounts/me/authorizations/github",
                    web::post().to(login::github_bind),
                )
                .route("/accounts/me/grants", web::get().to(account::grants))
                .route(
                    "/accounts/me/grants/{id}",
                    web::delete().to(account::revoke_grant),
                )
                .route("/sessions", web::post().to(login::create))
                .service(
                    web::resource("/sessions/current")
                        .route(web::get().to(login::current))
                        .route(web::delete().to(login::destroy)),
                )
                .route("/sessions/github", web::get().to(login::github_start))
                .route(
                    "/sessions/github/callback",
                    web::get().to(login::github_callback),
                )
                .service(
                    web::resource("/oauths")
                        .route(web::get().to(oauth::list))
                        .route(web::post().to(oauth::create)),
                )
                .service(
                    web::resource("/oauths/{id}")
                        .route(web::get().to(oauth::get))
                        .route(web::delete().to(oauth::destroy)),
                )
                .service(
                    web::resource("/oauths/{id}/clients")
                        .route(web::get().to(oauth::clients))
                        .route(web::post().to(oauth::create_client)),
                )
                .route(
                    "/oauths/{id}/clients/{client_id}",
                    web::delete().to(oauth::destroy_client),
                ),
        );
}
