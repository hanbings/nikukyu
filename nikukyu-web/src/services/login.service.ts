import get from "axios"
import post from "axios"

const getAuthorize = (provider: string) => get(`login/oauth/${provider}/authorize`)
const callback = (provider: string, code: string, state: string) =>
    post(`login/oauth/${provider}/callback?code=${code}&state=${state}`)

export {getAuthorize, callback} 