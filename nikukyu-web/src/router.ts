import Home from './pages/Home.vue'
import Login from "./pages/Login.vue";
import {createRouter, createWebHistory} from "vue-router";
import Account from "./pages/Account.vue";
import Authorize from "./pages/Authorize.vue";

const routes = [
    {path: '/', name: 'Home', component: Home},
    {path: '/login', name: 'Login', component: Login},
    {path: '/account', name: 'Account', component: Account},
    {path: '/oauth/authorize', name: 'Authorize', component: Authorize},
    {path: '/oauth/callback', name: 'OAuth', component: Authorize},
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router