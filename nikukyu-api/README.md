# Nikukyu API

## User-facing configuration and grants

`GET /api/v1/config` is public and returns `registration_enabled` and
`github_enabled` booleans. It never exposes provider credentials.

`GET /api/v1/accounts/me/grants` requires a session and returns the user's
active grants in `items`. Each item retains `id`, `oauth_id`, `scope` and
`created_at`, and includes an `application` object with `name`, `description`,
`avatar`, `background`, `theme_color`, `terms_of_service`, `privacy_policy`
and `is_deleted`. The application object is `null` if the application record
is missing. Soft-deleted applications remain visible so users can identify and
revoke their grants. `scope` contains the permissions actually granted, not
the application's full list of allowed scopes.

`DELETE /api/v1/accounts/me/grants/{id}` revokes a grant and requires the
session's `X-CSRF-Token`.

The API listens with HTTP/1.1. HTTP/2 should be terminated at the reverse
proxy: Actix's HTTP/2 feature is disabled while its `h2` 0.3 dependency is
subject to RUSTSEC-2026-0258.
