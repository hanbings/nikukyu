<script lang="ts" setup>
// 获取配置文件
import axios from "axios";
import Loading from "./components/Loading.vue";
import Navbar from "./components/Navbar.vue";
import {useConfigStore, useStatusStore} from "./stores/store.ts";
import {ref} from "vue";

const config = useConfigStore();
const status = useStatusStore();
const loading = ref<boolean>(true);

axios.get("/config.json")
    .then((res) => {
      let data = res.data;

      // 缓存
      config.$patch(state => {
        state.api = data.api;
        state.site = data.site;
        state.avatar = data.avatar;
        state.slogan = data.slogan;
        state.indexBackground = data.index_background;
        state.loginBackground = data.login_background;
        state.authorizeBackground = data.authorize_background;
      })

      loading.value = false;
    })
    .catch((err) => console.warn(err));
</script>

<template>
  <div v-if="loading" class="h-full w-full grid place-items-center">
    <Loading/>
  </div>
  <div v-if="!loading" class="h-full w-full">
    <Navbar v-if="status.login && status.navbar" class="glass fixed z-50"/>
    <router-view/>
  </div>
</template>