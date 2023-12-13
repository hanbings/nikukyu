import {AccessType} from "./token.ts";

export interface OAuth {
    ouid: string
    created: number
    redirect: string
    access: AccessType[]
    avatar: string
    name: string
    description: string
    homepage: string
    background: string
    theme: string
    policy: string
    tos: string
}

export interface OAuthClient {
    ocid: string
    created: number
    ouid: string
    secret: string
    expire: number
}

export enum OAuthLogType {

}

export interface OAuthLog {
    olid: string
    created: number
    ouid: string
    type: OAuthLogType
}