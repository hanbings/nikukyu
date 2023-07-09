import Index from "../pages/Index.vue";
import Login from "../pages/Login.vue";
import Authorize from "../pages/Authorize.vue";
import Account from "../pages/Account.vue";
import Home from "../pages/Home.vue";
import {createRouter, createWebHistory} from "vue-router";

const routes = [
    {path: '/', component: Index},
    {path: '/home', component: Home},
    {path: '/login', component: Login},
    {path: '/home/authorize/:platform/authorize', component: Authorize},
    {path: '/home/authorize/:platform/callback', component: Authorize},
    {path: '/account', component: Account},
]

export const router = createRouter({
    history: createWebHistory(),
    routes: routes
});