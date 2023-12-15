import {Button} from "@nextui-org/react";
import {Link} from "react-router-dom";

export function Home() {
    return (
        <div className={"h-screen w-screen bg-green-100 flex flex-col gap-2 justify-center items-center"}>
            <p className={"text-2xl text-green-500"}>🍀 This is Nikukyu, A Open Source SSO （Single Sign-On) software.</p>
            <div className={"flex flex-row gap-3"}>
                <Link to="/login">
                    <Button className={"bg-green-400"} variant="shadow">Get Started</Button>
                </Link>
                <Button className={"bg-green-300"}>Document</Button>
                <Button className={"bg-green-300"}>Open Source</Button>
            </div>
        </div>
    )
}