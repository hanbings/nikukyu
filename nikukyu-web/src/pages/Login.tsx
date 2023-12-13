import {Button, Input} from "@nextui-org/react";

export function Login() {
    return (
        <div className="w-screen h-screen flex justify-center items-center bg-green-100">
            <div className="w-screen h-screen sm:w-[420px] sm:h-[430px] bg-white rounded-2xl p-12 flex flex-col gap-3">
                <p className="text-2xl">Login Nests ID</p>
                <div>
                    <p className="text-sm text-gray-500">Unregistered email addresses will automatically be used to
                        register a new
                        account.</p>
                    <p className="text-sm text-gray-500">This means you have agreed to our
                        <a className="text-sm text-green-500"> Privacy Policy </a>
                        and
                        <a className="text-sm text-green-500"> Terms of Use </a>
                    </p>
                </div>
                <Input className="" type="email" label="Email" placeholder="email / account id"/>
                <Button className="bg-green-300" size="lg">Login</Button>
                <Button className="bg-black text-white" size="lg">
                    <img src={"github-mark.svg"} className="scale-50" alt="Login with Github"/>
                    Login with Github
                </Button>
            </div>
        </div>
    )
}