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

const listOAuth = (page: number, size: number): Promise<Message<Array<OAuth>>> => list(`oauth?page=${page}&size=${size}`)
const createOAuth = (params: OAuthParams): Promise<Message<OAuth>> => create(`oauth`, params)
const getOAuth = (id: string): Promise<Message<OAuth>> => get(`oauth/${id}`)
const updateOAuth = (id: string, params: OAuthParams): Promise<Message<OAuth>> => update(`oauth/${id}`, params)
const deleteOAuth = (id: string): Promise<Message<void>> => get(`oauth/${id}`)

const listOAuthClient = (oauthId: string, page: number, size: number): Promise<Message<Array<OAuthClient>>> =>
    list(`oauth/${oauthId}/client?page=${page}&size=${size}`)
const createOAuthClient = (oauthId: string, params: OAuthParams): Promise<Message<OAuthClient>> =>
    create(`oauth/${oauthId}/client`, params)
const getOAuthClient = (oauthId: string, clientId: string): Promise<Message<OAuthClient>> =>
    get(`oauth/${oauthId}/client/${clientId}`)
const deleteOAuthClient = (oauthId: string, clientId: string): Promise<Message<void>> =>
    remove(`oauth/${oauthId}/client/${clientId}`)

const listOAuthLog = (oauthId: string, page: number, size: number): Promise<Message<Array<OAuthLog>>> =>
    list(`oauth/${oauthId}/log?page=${page}&size=${size}`)
const getOAuthLog = (oauthId: string, logId: string): Promise<Message<OAuthLog>> =>
    get(`oauth/${oauthId}/log/${logId}`)

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