export interface Account {
    accountId: string
    created: number
    verified: boolean
    username?: string
    nickname?: string
    avatar?: string
    email: string
    background?: string
    color?: string
}

export interface AccountAuthorization {
    accountAuthorizationId: string
    created: number
    createdBy: string
    provider: string
    openid: string
}

export interface AccountLog {
    accountLogId: string
    created: number
    createdBy: string
    ip: string
    type: string
}

export interface AccountOAuth {
    accountOAuthId: string
    created: number
    createdBy: string
    oauthId: string
    access: Array<string>
}