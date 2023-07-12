<script lang="ts" setup>
// 获取状态
import Button from "../components/index/Button.vue";
import Slogan from "../components/Slogan.vue";
import {reactive} from "vue";
import {useAccountStore, useConfigStore, useStatusStore, useTokenStore} from "../stores/store.ts";
import {router} from "../router/router.ts";

const config = useConfigStore();
const status = useStatusStore();
const token = useTokenStore();
const account = useAccountStore();

const isLogin = status.login && account.auid != null && token.token != null;

if (isLogin) {
  router.push("/home");
}

// 背景图片 url
const background = reactive({background: `url('${config.indexBackground}')`});

</script>
<template>
  <div class="h-full w-full bg-fixed bg-cover bg-center" v-bind:style="{backgroundImage: background.background}">
    <div class="h-full w-full backdrop-blur flex items-center justify-center">
      <div class="flex flex-col gap-1.5">
        <Slogan :slogan="config.slogan"/>
        <div class="flex items-center justify-center m-2.5 gap-1.5">
          <router-link to='/login'>
            <Button text="Get Started"/>
          </router-link>
          <a href="https://github.com/hanbings/nikukyu" target="_blank">
            <Button text="Open Source"/>
          </a>
        </div>
      </div>
    </div>
  </div>
  <div class="w-full h-full">
  </div>
</template>