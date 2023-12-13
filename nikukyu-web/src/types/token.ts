export interface Token {
    token: string
    belong: string
    created: number
    expire: number
    access: AccessType[]
}


export enum AccessType {
    // 在 OAuth 验证中未创建 OAuth 记录和绑定账号发起邮件验证的权限
    OAUTH_EMAIL_VERIFY = "oauth.email.verify",
    // 发起邮件验证的权限
    EMAIL_VERIFY = "email.verify",


    // 账号相关
    ACCOUNT_READ = "account.read",
    ACCOUNT_WRITE = "account.write",
    ACCOUNT_SETTINGS_READ = "account.settings.read",
    ACCOUNT_SETTINGS_WRITE = "account.settings.write",
    ACCOUNT_DESTROY = "account.destroy",
    ACCOUNT_AUTHORIZATION_READ = "account.authorization.read",
    ACCOUNT_AUTHORIZATION_WRITE = "account.authorization.write",
    ACCOUNT_AUTHORIZATION_DESTROY = "account.authorization.destroy",
    // 查看账号内 OAuth 提供商的权限
    ACCOUNT_OAUTH_READ = "account.oauth.read",
    ACCOUNT_OAUTH_WRITE = "account.oauth.write",
    // 查看日志的权限
    ACCOUNT_LOG_READ = "account.log.read",
    ACCOUNT_LOG_WRITE = "account.log.write",


    // OAuth 相关
    OAUTH_READ = "oauth.read",
    OAUTH_WRITE = "oauth.write",
    OAUTH_DESTROY = "oauth.destroy",
    OAUTH_CLIENT_READ = "oauth.client.read",
    OAUTH_CLIENT_WRITE = "oauth.client.write",
    OAUTH_CLIENT_DESTROY = "oauth.client.destroy",
    OAUTH_LOG_READ = "oauth.log.read",
    OAUTH_LOG_WRITE = "oauth.log.write",

    // OAuth 授权相关
    OAUTH_AUTHORIZE = "oauth.authorize",
    OAUTH_TOKEN = "oauth.token",
    OAUTH_REFRESH = "oauth.refresh",
    OAUTH_REVOKE = "oauth.revoke",
}