use actix_web::HttpRequest;
use chrono::Utc;
use models::{
    account, account_authorization, account_oauth, account_password, enums::AccountStatus,
};
use sea_orm::{
    ActiveEnum, ActiveModelTrait, ActiveValue, ColumnTrait, ConnectionTrait, EntityTrait,
    QueryFilter, QueryOrder, QuerySelect, Set, TransactionTrait, sea_query::Expr,
};
use uuid::Uuid;

use super::{crypto, session};
use crate::{
    database,
    dto::{
        ListResponse, Patch,
        account::{
            AccountResponse, GrantResponse, IdentityResponse, RegisterAccountRequest,
            UpdateAccountRequest,
        },
    },
    error::{ApiError, ApiResult},
    state::AppState,
};

pub(super) fn view(a: account::Model) -> AccountResponse {
    AccountResponse {
        id: a.id,
        username: a.username,
        nickname: a.nickname,
        avatar: a.avatar,
        background: a.background,
        theme_color: a.theme_color,
    }
}

fn username(value: &str) -> ApiResult<String> {
    if !(3..=64).contains(&value.len())
        || !value
            .bytes()
            .all(|c| c.is_ascii_alphanumeric() || c == b'_' || c == b'-')
    {
        return Err(ApiError::bad(
            "Username must contain 3 to 64 letters, digits, underscores or hyphens",
        ));
    }
    Ok(value.to_ascii_lowercase())
}

pub(super) async fn active(db: &impl ConnectionTrait, id: Uuid) -> ApiResult<account::Model> {
    account::Entity::find_by_id(id)
        .filter(account::Column::IsDeleted.eq(false))
        .filter(account::Column::Status.eq(AccountStatus::Active))
        .one(db)
        .await?
        .ok_or_else(ApiError::unauthorized)
}

pub(super) async fn create(
    db: &impl ConnectionTrait,
    username: String,
    nickname: Option<String>,
    avatar: Option<String>,
) -> ApiResult<account::Model> {
    let now = Utc::now().fixed_offset();
    Ok(account::ActiveModel {
        id: Set(Uuid::new_v4()),
        username: Set(username),
        nickname: Set(nickname),
        avatar: Set(avatar),
        background: Set(None),
        theme_color: Set(None),
        status: Set(AccountStatus::Active),
        last_login_at: Set(None),
        is_deleted: Set(false),
        created_at: Set(now),
        updated_at: Set(now),
        deleted_at: Set(None),
    }
    .insert(db)
    .await?)
}

pub(super) async fn register_account(
    state: &AppState,
    input: RegisterAccountRequest,
) -> ApiResult<account::Model> {
    if !state.config.auth.allow_registration {
        return Err(ApiError::forbidden("Registration is disabled"));
    }
    let username = username(&input.username)?;
    if input
        .nickname
        .as_ref()
        .is_some_and(|name| name.chars().count() > 128)
    {
        return Err(ApiError::bad("Nickname is too long"));
    }
    let password_hash = crypto::password_hash(input.password).await?;
    let tx = state.database.begin().await?;
    let account = create(&tx, username, input.nickname, None).await?;
    let now = Utc::now().fixed_offset();
    account_password::ActiveModel {
        account_id: Set(account.id),
        password_hash: Set(password_hash),
        created_at: Set(now),
        updated_at: Set(now),
    }
    .insert(&tx)
    .await?;
    tx.commit().await?;
    Ok(account)
}

pub(super) async fn login(
    state: &AppState,
    username: String,
    password: String,
) -> ApiResult<account::Model> {
    let account = account::Entity::find()
        .filter(account::Column::Username.eq(username.to_ascii_lowercase()))
        .filter(account::Column::IsDeleted.eq(false))
        .one(&*state.database)
        .await?;
    let credential = match &account {
        Some(account) => {
            account_password::Entity::find_by_id(account.id)
                .one(&*state.database)
                .await?
        }
        None => None,
    };
    let encoded = credential
        .as_ref()
        .map(|p| p.password_hash.clone())
        .unwrap_or_else(|| state.dummy_password_hash.clone());
    let valid = crypto::verify_password(password, encoded).await?;
    match account {
        Some(a) if valid && credential.is_some() && a.status == AccountStatus::Active => Ok(a),
        _ => Err(ApiError::new(
            actix_web::http::StatusCode::UNAUTHORIZED,
            "invalid_credentials",
            "Invalid username or password",
        )),
    }
}

pub async fn register(
    state: &AppState,
    request: &HttpRequest,
    input: RegisterAccountRequest,
) -> ApiResult<AccountResponse> {
    session::check_origin(&state.config, request)?;
    state
        .rate_limiter
        .check(request, state.config.auth.login_attempts_per_minute)?;
    Ok(view(register_account(state, input).await?))
}

pub async fn current(state: &AppState, request: &HttpRequest) -> ApiResult<AccountResponse> {
    Ok(view(session::find(state, request).await?.account))
}

fn patch_field(
    field: &mut ActiveValue<Option<String>>,
    patch: Patch<String>,
    name: &str,
    limit: usize,
) -> ApiResult<()> {
    match patch {
        Patch::Missing => {}
        Patch::Null => *field = Set(None),
        Patch::Value(value) if value.chars().count() <= limit => *field = Set(Some(value)),
        Patch::Value(_) => return Err(ApiError::bad(format!("Invalid {name}"))),
    }
    Ok(())
}

pub async fn update(
    state: &AppState,
    request: &HttpRequest,
    input: UpdateAccountRequest,
) -> ApiResult<AccountResponse> {
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    let mut account: account::ActiveModel = current.account.into();
    patch_field(&mut account.nickname, input.nickname, "nickname", 128)?;
    patch_field(&mut account.avatar, input.avatar, "avatar", 2048)?;
    patch_field(
        &mut account.background,
        input.background,
        "background",
        2048,
    )?;
    patch_field(
        &mut account.theme_color,
        input.theme_color,
        "theme_color",
        32,
    )?;
    account.updated_at = Set(Utc::now().fixed_offset());
    Ok(view(account.update(&*state.database).await?))
}

pub async fn authorizations(
    state: &AppState,
    request: &HttpRequest,
) -> ApiResult<ListResponse<IdentityResponse>> {
    let current = session::find(state, request).await?;
    let identities = account_authorization::Entity::find()
        .filter(account_authorization::Column::AccountId.eq(current.account.id))
        .filter(account_authorization::Column::DeletedAt.is_null())
        .limit(100)
        .all(&*state.database)
        .await?;
    Ok(ListResponse {
        items: identities
            .into_iter()
            .map(|identity| IdentityResponse {
                id: identity.id,
                provider: identity.provider,
                openid: identity.openid,
                created_at: identity.created_at,
            })
            .collect(),
    })
}

pub async fn grants(
    state: &AppState,
    request: &HttpRequest,
) -> ApiResult<ListResponse<GrantResponse>> {
    let current = session::find(state, request).await?;
    let grants = account_oauth::Entity::find()
        .filter(account_oauth::Column::AccountId.eq(current.account.id))
        .filter(account_oauth::Column::RevokedAt.is_null())
        .order_by_desc(account_oauth::Column::CreatedAt)
        .limit(100)
        .all(&*state.database)
        .await?;
    Ok(ListResponse {
        items: grants
            .into_iter()
            .map(|grant| GrantResponse {
                id: grant.id,
                oauth_id: grant.oauth_id,
                scope: grant.scope.iter().map(ActiveEnum::to_value).collect(),
                created_at: grant.created_at,
            })
            .collect(),
    })
}

pub async fn revoke_grant(state: &AppState, request: &HttpRequest, id: Uuid) -> ApiResult<()> {
    let current = session::find(state, request).await?;
    session::check_csrf(&state.config, request, &current)?;
    let grant = account_oauth::Entity::find_by_id(id)
        .filter(account_oauth::Column::AccountId.eq(current.account.id))
        .one(&*state.database)
        .await?
        .ok_or_else(ApiError::missing)?;
    let tx = state.database.begin().await?;
    database::lock(
        &tx,
        &format!("grant:{}:{}", grant.account_id, grant.oauth_id),
    )
    .await?;
    account_oauth::Entity::update_many()
        .col_expr(
            account_oauth::Column::RevokedAt,
            Expr::value(Utc::now().fixed_offset()),
        )
        .filter(account_oauth::Column::Id.eq(grant.id))
        .exec(&tx)
        .await?;
    tx.commit().await?;
    Ok(())
}
