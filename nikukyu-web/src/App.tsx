import {configureStore} from "@reduxjs/toolkit";
import {Provider} from "react-redux";
import {QueryClient, QueryClientProvider} from "react-query";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {Home} from "./pages/Home.tsx";
import {Login} from "./pages/Login.tsx";
import {Authorize} from "./pages/Authorize.tsx";
import {Account} from "./pages/Account.tsx";
import {OAuth} from "./pages/OAuth.tsx";

const store = configureStore({
    reducer: {},
})

const query = new QueryClient()

const router = createBrowserRouter([
    {path: "/", element: <Home/>},
    {path: "/login/*", element: <Login/>},
    {path: "/authorize", element: <Authorize/>},
    {path: "/account", element: <Account/>},
    {path: "/oauth", element: <OAuth/>},
])

export default function App() {
    return (
        <Provider store={store}>
            <QueryClientProvider client={query}>
                <RouterProvider router={router}/>
            </QueryClientProvider>
        </Provider>
    )
}