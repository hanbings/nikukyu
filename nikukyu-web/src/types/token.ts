export interface Token {
    token: string
    belong: string
    created: number
    expire: number
    access: Array<string>
}

export const access = [
    {
        access: "oauth.email.verify",
        description: "在 OAuth 验证中未创建 OAuth 记录和绑定账号发起邮件验证的权限",
        isDanger: true
    },
    { access: "email.verify", description: "发起邮件验证的权限", isDanger: true },
    { access: "account.read", description: "查看账号信息的权限", isDanger: false },
    { access: "account.write", description: "修改账号信息的权限", isDanger: false },
    { access: "account.settings.read", description: "查看账号设置的权限", isDanger: false },
    { access: "account.settings.write", description: "修改账号设置的权限", isDanger: false },
    { access: "account.authorization.read", description: "查看账号内 OAuth 提供商的权限", isDanger: false },
    { access: "account.authorization.write", description: "修改账号内 OAuth 提供商的权限", isDanger: false },
    { access: "account.authorization.destroy", description: "删除账号内 OAuth 提供商的权限", isDanger: false },
    { access: "account.oauth.read", description: "查看 OAuth 记录的权限", isDanger: false },
    { access: "account.oauth.write", description: "修改 OAuth 记录的权限", isDanger: false },
    { access: "account.log.read", description: "查看日志的权限", isDanger: false },
    { access: "account.log.write", description: "修改日志的权限", isDanger: false },
    { access: "oauth.read", description: "查看 OAuth 记录的权限", isDanger: false },
    { access: "oauth.write", description: "修改 OAuth 记录的权限", isDanger: false },
    { access: "oauth.destroy", description: "删除 OAuth 记录的权限", isDanger: false },
    { access: "oauth.client.read", description: "查看 OAuth 客户端的权限", isDanger: false },
    { access: "oauth.client.write", description: "修改 OAuth 客户端的权限", isDanger: false },
    { access: "oauth.client.destroy", description: "删除 OAuth 客户端的权限", isDanger: false },
    { access: "oauth.log.read", description: "查看 OAuth 日志的权限", isDanger: false },
    { access: "oauth.log.write", description: "修改 OAuth 日志的权限", isDanger: false },
    { access: "oauth.authorize", description: "OAuth 授权权限", isDanger: false },
    { access: "oauth.token", description: "OAuth 获取 Token 权限", isDanger: true },
    { access: "oauth.refresh", description: "刷新 OAuth 授权权限", isDanger: false },
    { access: "oauth.revoke", description: "撤销 OAuth 授权权限", isDanger: false },
]