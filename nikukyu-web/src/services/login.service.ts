import post from "axios";
import get from "axios";

const getAuthorize = (provider: string) => get(`/api/v0/login/oauth/${provider}/authorize`)
const callback = (provider: string, code: string, state: string) =>
    post(`/api/v0/login/oauth/${provider}/callback?code=${code}&state=${state}`)

export {getAuthorize, callback} 