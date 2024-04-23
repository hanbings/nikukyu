import axios, {AxiosRequestConfig} from "axios"
import {config} from "../config.ts"

export const headers: AxiosRequestConfig['headers'] = {'Content-Type': 'application/json'}
export const requester = axios.create({
    headers
})

const list = async <R>(api: string): Promise<R> => (await requester.get<R>(`${config.api}/api/v0/${api}`)).data
const get = async <R>(api: string): Promise<R> => (await requester.get<R>(`${config.api}/api/v0/${api}`)).data
const create = async <T, R>(api: string, data: T): Promise<R> =>
    (await requester.post<R>(`${config.api}/api/v0/${api}`, data)).data
const update = async <T, R>(api: string, data: T): Promise<R> =>
    (await requester.post<R>(`${config.api}/api/v0/${api}`, data)).data
const remove = async <R>(api: string): Promise<R> =>
    (await requester.delete<R>(`${config.api}/api/v0/${api}`)).data

export {list, get, create, update, remove}