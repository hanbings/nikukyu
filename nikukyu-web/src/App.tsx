import {configureStore} from "@reduxjs/toolkit"
import {Provider} from "react-redux"
import {QueryClient, QueryClientProvider} from "react-query"
import {createBrowserRouter, RouterProvider} from "react-router-dom"
import {HomePage} from "./pages/Home.tsx"
import {LoginPage} from "./pages/Login.tsx"
import {AuthorizePage} from "./pages/Authorize.tsx"
import {AccountPage} from "./pages/Account.tsx"
import {OAuthPage} from "./pages/OAuth.tsx"

const store = configureStore({
    reducer: {},
})

const query = new QueryClient()

const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
    {path: "/login/*", element: <LoginPage/>},
    {path: "/authorize", element: <AuthorizePage/>},
    {path: "/account", element: <AccountPage/>},
    {path: "/oauth", element: <OAuthPage/>},
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