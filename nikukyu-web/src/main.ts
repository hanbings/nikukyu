import {createApp} from 'vue';
import './style.css';
import App from './App.vue';
import router from "./router.ts";
import {createPinia} from "pinia";
import 'preline';

const route = router;
const store = createPinia();
const app = createApp(App);
app.use(route).use(store).mount('#app');
