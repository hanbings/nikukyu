package io.hanbings.server.nikukyu.data;

public class Permission {
    // 在 OAuth 验证中未创建 OAuth 记录和绑定账号发起邮件验证的权限
    public static final String OAUTH_EMAIL_VERIFY = "oauth.email.verify";

    // 发起邮件验证的权限
    public static final String EMAIL_VERIFY = "email.verify";

    // 账号相关
    public static final String ACCOUNT_READ = "account.read";
    public static final String ACCOUNT_WRITE = "account.write";
    public static final String ACCOUNT_SETTINGS_READ = "account.settings.read";
    public static final String ACCOUNT_SETTINGS_WRITE = "account.settings.write";
    public static final String ACCOUNT_DESTROY = "account.destroy";
    public static final String ACCOUNT_AUTHORIZATION_READ = "account.authorization.read";
    public static final String ACCOUNT_AUTHORIZATION_WRITE = "account.authorization.write";
    public static final String ACCOUNT_AUTHORIZATION_DESTROY = "account.authorization.destroy";

    // 查看账号内 OAuth 提供商的权限
    public static final String ACCOUNT_OAUTH_READ = "account.oauth.read";
    public static final String ACCOUNT_OAUTH_WRITE = "account.oauth.write";

    // 查看账号日志的权限
    public static final String ACCOUNT_LOG_READ = "account.log.read";
    public static final String ACCOUNT_LOG_WRITE = "account.log.write";

    // OAuth 相关
    public static final String OAUTH_READ = "oauth.read";
    public static final String OAUTH_WRITE = "oauth.write";
    public static final String OAUTH_DESTROY = "oauth.destroy";
    public static final String OAUTH_CLIENT_READ = "oauth.client.read";
    public static final String OAUTH_CLIENT_WRITE = "oauth.client.write";
    public static final String OAUTH_CLIENT_DESTROY = "oauth.client.destroy";
    public static final String OAUTH_LOG_READ = "oauth.log.read";
    public static final String OAUTH_LOG_WRITE = "oauth.log.write";

    // OAuth 授权相关
    public static final String OAUTH_AUTHORIZE = "oauth.authorize";
    public static final String OAUTH_TOKEN = "oauth.token";
    public static final String OAUTH_REFRESH = "oauth.refresh";
    public static final String OAUTH_REVOKE = "oauth.revoke";
}
