import {NextUIProvider} from "@nextui-org/react";
import {Route, Routes} from "react-router-dom";
import {Home} from "./pages/Home.tsx";
import {Login} from "./pages/Login.tsx";
import {Account} from "./pages/Account.tsx";
import {Authorize} from "./pages/Authorize.tsx";

function App() {
    return (
        <NextUIProvider>
            <main>
                <Routes>
                    <Route path="/" element={<Home/>}></Route>
                    <Route path="/login" element={<Login/>}></Route>
                    <Route path="/account" element={<Account/>}></Route>
                    <Route path="/authorize" element={<Authorize/>}></Route>
                </Routes>
            </main>
        </NextUIProvider>
    )
}

export default App
