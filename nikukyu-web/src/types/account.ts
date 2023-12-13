import {AccessType} from "./token.ts";

export interface Account {
    auid: string
    created: number
    verified: boolean
    id: string
    nick: string
    avatar: string
    background: string
    color: string
    email: string
}

export interface AccountAuthorization {
    aaid: string
    created: number
    auid: string
    provider: string
    openid: string
}

export enum AccountLogType {

}

export interface AccountLog {
    alid: string
    created: number
    auid: string
    ip: string
    type: AccountLogType
}

export interface AccountOAuth {
    aoid: string
    created: number
    auid: string
    ooid: string
    access: AccessType[]
}