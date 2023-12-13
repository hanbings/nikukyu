import {Button} from "@nextui-org/react";

export function Account() {
    return (
        <div className="w-screen h-screen flex flex-col gap-3 bg-green-100 p-2 sm:p-12">
            <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
                <div>
                    <p className="text-2xl">Yours Account</p>
                    <p className="text-sm text-gray-500">Click card to edit your account</p>

                </div>
                <div>

                </div>
                <div className="flex flex-row gap-2">
                    <Button className="bg-green-200">Log out Account</Button>
                    <Button className="bg-red-500 text-white">Delete Account</Button>
                </div>
            </div>
            <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
                <div>
                    <p className="text-2xl">Authorized Applications</p>
                    <p className="text-sm text-gray-500">Click card to change access or delete it</p>
                </div>
            </div>
            <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
                <div>
                    <p className="text-2xl">Developer Settings</p>
                    <p className="text-sm text-gray-500">Create your own application</p>
                </div>
                <div>
                    <Button className="bg-green-200">Apply a new OAuth Application</Button>
                </div>
            </div>
        </div>
    )
}