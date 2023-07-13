<script lang="ts" setup>
import Warning from "../components/Warning.vue";
import {reactive} from "vue";
import {useAccountStore, useConfigStore, useStatusStore, useTokenStore} from "../stores/store.ts";
import Title from "../components/Title.vue";
import Avatar from "../components/Avatar.vue";
import {router} from "../router/router.ts";

const config = useConfigStore();
const status = useStatusStore();
const account = useAccountStore();
const token = useTokenStore();

const isLogin = status.login && account.auid != null && token.token != null;

// 背景图片 url
const background = reactive({background: (!isLogin || account.background != "") ? `url('${account.background}')` : `url('${config.indexBackground}')`});

if (status.redirect) {
  const redirect: string = status.redirect;
  status.redirect = "";

  router.push(redirect);
}

</script>
<template>
  <div class="h-full w-full bg-fixed bg-cover bg-center" v-bind:style="{backgroundImage: background.background}">
    <div class="h-full w-full backdrop-blur flex justify-center items-center">
      <Warning v-if="!isLogin" text="未登录"/>

      <div v-if="isLogin" class="shadow-lg flex flex-col bg-white rounded-2xl px-20 py-24">
        <Avatar :avatar="account.avatar" size="72px"/>
        <div class="mt-6">
          <Title :text="account.nick"/>
        </div>
        <div class="mt-6">
          <p class="text-gray-400 text-xs">{{ `邮箱 ${account.email}` }}</p>
          <p class="text-gray-400 text-xs">{{ `账号 ID  ${account.id}` }}</p>
          <p class="text-gray-400 text-xs">{{ `账号 UID ${account.auid}` }}</p>
        </div>
      </div>
    </div>
  </div>
</template>