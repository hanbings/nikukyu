import get from "axios"
import post from "axios"
import {config} from "../config.ts"

const getAuthorize = (provider: string) => get(`${config.api}/api/v0/login/oauth/${provider}/authorize`)
const callback = (provider: string, code: string, state: string) =>
    post(`${config.api}/api/v0/login/oauth/${provider}/callback?code=${code}&state=${state}`)

export {getAuthorize, callback} 