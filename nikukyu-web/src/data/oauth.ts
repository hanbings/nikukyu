export interface OAuth {
    oauthId: string
    created: number
    createdBy: string
    redirect: Array<string>
    access: Array<string>
    avatar?: string
    name: string
    description?: string
    homepage?: string
    background?: string
    theme?: string
    policy?: string
    tos?: string
}

export interface OAuthClient {
    oauthClientId: string
    created: number
    createdBy: string
    secret: string
    expire: number
}

export interface OAuthLog {
    oauthLogId: string
    created: number
    createdBy: string
    ip: string
    type: string
}