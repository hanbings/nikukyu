import {Account, AccountAuthorization, AccountLog, AccountOAuth} from "../data/account.ts";
import {get, list, remove, update} from "./service.ts";
import {Message} from "../data/message.ts";

type AccountParams = {
    nickname: string
    avatar: string
    background: string
    color: string
}

const getAccount = (accountId: string): Promise<Message<Account>> => get(`account/${accountId}`)
const updateAccount = (accountId: string, data: AccountParams): Promise<Message<Account>> =>
    update(`account/${accountId}`, data)

const listAccountAuthorization =
    (accountId: string, page: number, size: number): Promise<Message<Array<AccountAuthorization>>> =>
        list(`account/${accountId}/authorization?page=${page}&size=${size}`)
const getAccountAuthorization = (accountId: string, accountAuthorizationId: string): Promise<Message<AccountAuthorization>> =>
    get(`account/${accountId}/authorization/${accountAuthorizationId}`)
const deleteAccountAuthorization = (accountId: string, accountAuthorizationId: string): Promise<Message<null>> =>
    remove(`account/${accountId}/authorization/${accountAuthorizationId}`)

const listAccountLog = (accountId: string, page: number, size: number): Promise<Message<Array<AccountLog>>> =>
    list(`account/${accountId}/log?page=${page}&size=${size}`)
const getAccountLog = (accountId: string, logId: string): Promise<Message<AccountLog>> =>
    get(`account/${accountId}/log/${logId}`)

const listAccountOAuth = (accountId: string, page: number, size: number): Promise<Message<Array<AccountOAuth>>> =>
    list(`account/${accountId}/oauth?page=${page}&size=${size}`)
const getAccountOAuth = (accountId: string, oauthId: string): Promise<Message<AccountOAuth>> =>
    get(`account/${accountId}/oauth/${oauthId}`)
const deleteOAuth = (accountId: string, oauthId: string): Promise<Message<null>> =>
    remove(`account/${accountId}/oauth/${oauthId}`)

export {
    getAccount,
    updateAccount,
    listAccountAuthorization,
    getAccountAuthorization,
    deleteAccountAuthorization,
    listAccountLog,
    getAccountLog,
    listAccountOAuth,
    getAccountOAuth,
    deleteOAuth
}