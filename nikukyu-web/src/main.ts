import './style.css'
import {createApp} from "vue";
// WebStorm 莫名其妙 TS2307: Cannot find module './App.vue' or its corresponding type declarations.
// 能过编译的
// @ts-ignore
import App from "./App.vue";

createApp(App).mount('#app');