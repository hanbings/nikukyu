import {Fragment, useState} from "react"
import {useQuery} from "@tanstack/react-query"
import {Message} from "../data/message.ts"
import {OAuth} from "../data/oauth.ts"
import {listOAuth} from "../services/oauth.service.ts"

export function OAuthPage() {
    const [isOwner] = useState(false)

    return (
        <div className="w-screen h-screen flex flex-col gap-3 bg-green-100 p-2 sm:p-12">
            {Applications()}
            {isOwner ? OAuthClients() : <Fragment/>}
            {isOwner ? OAuthLogs() : <Fragment/>}
        </div>
    )
}

function Applications() {
    useQuery<Message<OAuth[]>>({
        queryKey: ['oauth-application'],
        queryFn: () => listOAuth(1, 10)
    })

    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <div>
                <p className="text-2xl">OAuth Applications</p>
            </div>
        </div>
    )
}

function OAuthClients() {
    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <p className="text-2xl">OAuth Clients</p>
            <p className="text-sm text-gray-500">Manage OAuth Clients</p>
        </div>
    )
}

function OAuthLogs() {
    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <p className="text-2xl">OAuth Logs</p>
            <p className="text-sm text-gray-500">Here are recent account activity, client changes, and new
                user approval of new OAuth applications.</p>
        </div>
    )
}