import './style.css'
import App from "./App.vue";
import {createApp} from "vue";
import {store} from "./stores/store.ts";
import {router} from "./router/router.ts";

createApp(App).use(store).use(router).mount('#app');