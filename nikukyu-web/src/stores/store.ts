import {createPinia, defineStore} from "pinia";
import piniaPluginPersistedState from 'pinia-plugin-persistedstate'

export const store = createPinia();
store.use(piniaPluginPersistedState);

export const useStatusStore = defineStore("status", {
    state: () => {
        return {
            login: false,
            loading: true
        }
    }
});

export const useConfigStore = defineStore("config", {
    state: () => ({
        site: "",
        api: "",
        avatar: "",
        slogan: "",
        indexBackground: "",
        loginBackground: "",
        authorizeBackground: ""
    }),
    persist: {
        storage: localStorage,
        key: "config"
    }
});