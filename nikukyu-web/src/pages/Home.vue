<script lang="ts" setup>
import Warning from "../components/Warning.vue";
import {reactive} from "vue";
import {useAccountStore, useConfigStore, useStatusStore, useTokenStore} from "../stores/store.ts";

const config = useConfigStore();
const status = useStatusStore();
const account = useAccountStore();
const token = useTokenStore();

const isLogin = status.login && account.auid != null && token.token != null;

// 背景图片 url
const background = reactive({background: isLogin ? `url('${account.background}')` : `url('${config.indexBackground}')`});

</script>
<template>
  <div class="h-full w-full bg-fixed bg-cover bg-center" v-bind:style="{backgroundImage: background.background}">
    <div class="h-full w-full backdrop-blur flex justify-center items-center">
      <Warning v-if="!isLogin" text="未登录"/>
    </div>
  </div>
</template>