import {Button, Checkbox} from "@nextui-org/react";

export function Authorize() {
    return (
        <div className="w-screen h-screen flex justify-center items-center bg-green-100">
            <div
                className="w-screen h-screen sm:w-[420px] sm:h-auto bg-white rounded-2xl p-12 flex flex-col gap-3">
                <p className="text-2xl">Authorize</p>
                <div>
                    <p className="text-sm text-gray-500">The Application <a
                        className="text-sm text-green-500 font-bold">Nests ID </a>
                        requests access to your account.</p>
                    <p className="text-sm text-gray-500">Current login: <a
                        className="text-sm text-green-500">hanbings@hanbings.io</a></p>
                    <p className="text-sm text-green-500">Is it not use this account?</p>
                </div>
                <div className="flex flex-col gap-1">
                    <Checkbox color="success"><p className="text-sm text-gray-500">查看账号信息的权限</p></Checkbox>
                    <Checkbox color="success"><p className="text-sm text-gray-500">查看账号设置的权限</p></Checkbox>
                    <Checkbox color="success"><p className="text-sm text-gray-500">查看 OAuth 记录的权限</p></Checkbox>
                </div>
                <div>
                    <p className="text-sm text-gray-500">You may want to check the app's
                        <a className="text-sm text-green-500"> Privacy Policy </a>
                        and
                        <a className="text-sm text-green-500"> Terms of Use </a>
                    </p>
                </div>
                <div className="flex flex-row gap-2">
                    <Button className="bg-green-200">Approve</Button>
                    <Button className="bg-yellow-100">Cancel</Button>
                </div>
            </div>
        </div>
    )
}