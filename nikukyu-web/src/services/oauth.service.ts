import {create, get, list, remove, update} from "./service.ts"
import {OAuth, OAuthClient, OAuthLog} from "../data/oauth.ts"
import {Message} from "../data/message.ts"

type OAuthParams = {
    name: string
    redirect: Set<string>
    access: Set<string>
    avatar: string
    description: string
    homepage: string
    background: string
    theme: string
    policy: string
    tos: string
}

const listOAuth = (page: number, size: number): Promise<Message<Array<OAuth>>> => list(`/api/v0/oauth?page=${page}&size=${size}`)
const createOAuth = (params: OAuthParams): Promise<Message<OAuth>> => create(`/api/v0/oauth`, params)
const getOAuth = (id: string): Promise<Message<OAuth>> => get(`/api/v0/oauth/${id}`)
const updateOAuth = (id: string, params: OAuthParams): Promise<Message<OAuth>> => update(`/api/v0/oauth/${id}`, params)
const deleteOAuth = (id: string): Promise<Message<void>> => get(`/api/v0/oauth/${id}`)

const listOAuthClient = (oauthId: string, page: number, size: number): Promise<Message<Array<OAuthClient>>> =>
    list(`/api/v0/oauth/${oauthId}/client?page=${page}&size=${size}`)
const createOAuthClient = (oauthId: string, params: OAuthParams): Promise<Message<OAuthClient>> =>
    create(`/api/v0/oauth/${oauthId}/client`, params)
const getOAuthClient = (oauthId: string, clientId: string): Promise<Message<OAuthClient>> =>
    get(`/api/v0/oauth/${oauthId}/client/${clientId}`)
const deleteOAuthClient = (oauthId: string, clientId: string): Promise<Message<void>> =>
    remove(`/api/v0/oauth/${oauthId}/client/${clientId}`)

const listOAuthLog = (oauthId: string, page: number, size: number): Promise<Message<Array<OAuthLog>>> =>
    list(`/api/v0/oauth/${oauthId}/log?page=${page}&size=${size}`)
const getOAuthLog = (oauthId: string, logId: string): Promise<Message<OAuthLog>> =>
    get(`/api/v0/oauth/${oauthId}/log/${logId}`)

export {
    listOAuth,
    createOAuth,
    getOAuth,
    updateOAuth,
    deleteOAuth,
    listOAuthClient,
    createOAuthClient,
    getOAuthClient,
    deleteOAuthClient,
    listOAuthLog,
    getOAuthLog
}