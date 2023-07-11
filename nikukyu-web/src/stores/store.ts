import {createPinia, defineStore} from "pinia";
import piniaPluginPersistedState from 'pinia-plugin-persistedstate'
import {AccessType} from "../data/common.ts";

export const store = createPinia();
store.use(piniaPluginPersistedState);

export const useStatusStore = defineStore("status", {
    state: () => {
        return {
            login: false
        }
    }
});

export const useConfigStore = defineStore("config", {
    state: () => ({
        site: "",
        api: "",
        avatar: "",
        slogan: "",
        docs: "",
        indexBackground: "",
        loginBackground: "",
        authorizeBackground: ""
    })
});

export const useAccountStore = defineStore("account", {
    state: () => ({
        auid: null,
        created: 0,
        verified: false,
        id: "",
        nick: "",
        avatar: "",
        background: "",
        color: "",
        email: ""
    }),
    persist: {
        storage: localStorage,
        key: "account"
    }
});

export const useTokenStore = defineStore("token", {
    state: () => ({
        token: null,
        belong: null,
        created: 0,
        expire: 0,
        access: new Array<AccessType>()
    }),
    persist: {
        storage: localStorage,
        key: "token"
    }
});