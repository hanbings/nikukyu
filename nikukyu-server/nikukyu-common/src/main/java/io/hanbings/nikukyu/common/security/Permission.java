package io.hanbings.nikukyu.common.security;

public class Permission {
    // Account
    public static final String ACCOUNT_CREATE = "account:create";
    public static final String ACCOUNT_READ = "account:read";
    public static final String ACCOUNT_UPDATE = "account:update";
    public static final String ACCOUNT_DELETE = "account:delete";
    // Account Authorization
    public static final String ACCOUNT_AUTHORIZATION_CREATE = "account.authorization:create";
    public static final String ACCOUNT_AUTHORIZATION_READ = "account.authorization:read";
    public static final String ACCOUNT_AUTHORIZATION_UPDATE = "account.authorization:update";
    public static final String ACCOUNT_AUTHORIZATION_DELETE = "account.authorization:delete";
    // Account Log
    public static final String ACCOUNT_LOG_CREATE = "account.log:create";
    public static final String ACCOUNT_LOG_READ = "account.log:read";
    public static final String ACCOUNT_LOG_UPDATE = "account.log:update";
    public static final String ACCOUNT_LOG_DELETE = "account.log:delete";
    // Account OAuth
    public static final String ACCOUNT_OAUTH_CREATE = "account.oauth:create";
    public static final String ACCOUNT_OAUTH_READ = "account.oauth:read";
    public static final String ACCOUNT_OAUTH_UPDATE = "account.oauth:update";
    public static final String ACCOUNT_OAUTH_DELETE = "account.oauth:delete";

    // OAuth
    public static final String OAUTH_CREATE = "oauth:create";
    public static final String OAUTH_READ = "oauth:read";
    public static final String OAUTH_UPDATE = "oauth:update";
    public static final String OAUTH_DELETE = "oauth:delete";

    // OAuth Client
    public static final String OAUTH_CLIENT_CREATE = "oauth.client:create";
    public static final String OAUTH_CLIENT_READ = "oauth.client:read";
    public static final String OAUTH_CLIENT_UPDATE = "oauth.client:update";
    public static final String OAUTH_CLIENT_DELETE = "oauth.client:delete";
    // OAuth Log
    public static final String OAUTH_LOG_CREATE = "oauth.log:create";
    public static final String OAUTH_LOG_READ = "oauth.log:read";
    public static final String OAUTH_LOG_UPDATE = "oauth.log:update";
    public static final String OAUTH_LOG_DELETE = "oauth.log:delete";

    // Verify
    public static final String VERIFY_OAUTH_EMAIL = "verify:oauth:email";
    public static final String VERIFY_EMAIL = "verify:email";
}
