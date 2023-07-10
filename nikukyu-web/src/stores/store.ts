import {createPinia, defineStore} from "pinia";
import piniaPluginPersistedState from 'pinia-plugin-persistedstate'

export const store = createPinia();
store.use(piniaPluginPersistedState);

export function loading() {
    useStatusStore().$patch(state => state.loading = true);
}

export function loaded() {
    useStatusStore().$patch(state => state.loading = false);
}

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