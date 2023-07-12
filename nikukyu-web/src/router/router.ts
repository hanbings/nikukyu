import Home from "../pages/Home.vue";
import Index from "../pages/Index.vue";
import Login from "../pages/Login.vue";
import About from "../pages/About.vue";
import Verify from "../pages/Verify.vue";
import Authorize from "../pages/Authorize.vue";
import Callback from "../pages/Callback.vue";
import {createRouter, createWebHistory} from "vue-router";


const routes = [
    {path: '/', component: Index},
    {path: '/home', component: Home},
    {path: '/about', component: About},
    {path: '/login', component: Login},
    {path: '/login/email/verify', component: Verify},
    {path: '/login/oauth/:platform/authorize', component: Authorize},
    {path: '/login/oauth/:platform/callback', component: Callback},
];

export const router = createRouter({
    history: createWebHistory(),
    routes: routes
});
