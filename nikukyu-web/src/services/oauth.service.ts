import {create, get, list, remove, update} from "./service.ts"
import {OAuth, OAuthClient, OAuthLog} from "../data/oauth.ts"

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

const listOAuth = (page: number, size: number): Promise<Array<OAuth>> => list(`oauth?page=${page}&size=${size}`)
const createOAuth = (params: OAuthParams): Promise<OAuth> => create(`oauth`, params)
const getOAuth = (id: string): Promise<OAuth> => get(`oauth/${id}`)
const updateOAuth = (id: string, params: OAuthParams): Promise<OAuth> => update(`oauth/${id}`, params)
const deleteOAuth = (id: string): Promise<void> => get(`oauth/${id}`)

const listOAuthClient = (oauthId: string, page: number, size: number): Promise<Array<OAuthClient>> =>
    list(`oauth/${oauthId}/client?page=${page}&size=${size}`)
const createOAuthClient = (oauthId: string, params: OAuthParams): Promise<OAuthClient> =>
    create(`oauth/${oauthId}/client`, params)
const getOAuthClient = (oauthId: string, clientId: string): Promise<OAuthClient> =>
    get(`oauth/${oauthId}/client/${clientId}`)
const deleteOAuthClient = (oauthId: string, clientId: string): Promise<void> =>
    remove(`oauth/${oauthId}/client/${clientId}`)

const listOAuthLog = (oauthId: string, page: number, size: number): Promise<Array<OAuthLog>> =>
    list(`oauth/${oauthId}/log?page=${page}&size=${size}`)
const getOAuthLog = (oauthId: string, logId: string): Promise<OAuthLog> =>
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