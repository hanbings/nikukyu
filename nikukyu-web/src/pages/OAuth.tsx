import {useState} from "react";
import {Button} from "@nextui-org/react";

export function OAuth() {
    const [isOwner] = useState(false);

    return (
        <div className="w-screen h-screen flex flex-col gap-3 bg-green-100 p-2 sm:p-12">
            <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
                <div>
                    <p className="text-2xl">OAuth Applications</p>
                    {
                        isOwner ?
                            <p className="text-sm text-gray-500">Click card to change access or delete it.</p> :
                            <p className="text-sm text-gray-500">View the app's information.</p>
                    }
                </div>
                {
                    isOwner ?
                        <div className="flex flex-row gap-2">
                            <Button className="bg-yellow-300">Revoke all authorized users</Button>
                            <Button className="bg-red-500 text-white">Delete OAuth Application</Button>
                        </div> :
                        <div/>
                }
            </div>
        </div>
    )
}