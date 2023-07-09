<script lang="ts" setup>
// 获取配置文件
import axios from "axios";
import {useConfigStore} from "./stores/store.ts";

const config = useConfigStore();

axios.get("/config.json").then((res) => {
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
    });
});
</script>

<template>
  <router-view/>
</template>