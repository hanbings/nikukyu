<script lang="ts" setup>
import Title from "../components/Title.vue";
import Button from "../components/login/Button.vue";
import {reactive, ref} from "vue";
import {useConfigStore} from "../stores/store.ts";

const config = useConfigStore();

// 背景图片 url
const background = reactive({background: `url('${config.loginBackground}')`});

// 状态
let chooseLogin = ref<boolean>(true);
let needVerify = ref<boolean>(false);
</script>

<template>
  <div class="h-full w-full bg-fixed bg-cover bg-center" v-bind:style="{backgroundImage: background.background}">
    <!-- 登录 -->
    <div v-if="chooseLogin" class="h-full w-full backdrop-blur flex justify-center items-center">
      <div class="shadow-lg flex flex-col bg-white rounded-2xl px-20 py-24">
        <div>
          <Title text="登录"/>
        </div>
        <div class="mt-6">
          <p class="text-gray-400 text-xs">未注册的电子邮箱地址将自动注册</p>
          <p class="text-gray-400 text-xs">注册代表您已同意我们的 <a
              class="font-medium text-blue-400 hover:no-underline text-xs">隐私政策</a> 与 <a
              class="font-medium text-blue-400 hover:no-underline text-xs">用户条款</a></p>
        </div>
        <div></div>
        <div class="flex flex-row gap-3 mt-6">
          <Button alt="github" color="#29242f" link="/login/oauth/github/authorize"
                  path="resources/images/icons8-github.svg" size="32px"></Button>
          <Button alt="discord" color="#6c19ff" link="/login/oauth/discord/authorize"
                  path="resources/images/icons8-discord.svg" size="24px"></Button>
          <Button alt="microsoft" color="#ffffff" link="/login/oauth/microsoft/authorize"
                  path="resources/images/icons8-microsoft.svg" size="24px"></Button>
          <Button alt="google" color="#ffffff" link="/login/oauth/google/authorize"
                  path="resources/images/icons8-google.svg" size="24px"></Button>
          <Button alt="more" color="#bbc5d0" link="/login/oauth/google/authorize"
                  path="resources/images/more.svg" size="32px"></Button>
        </div>
      </div>
    </div>

    <!-- 需要邮件验证 -->
    <div v-if="needVerify" class="h-full w-full backdrop-blur flex justify-center items-center">
      <div class="shadow-lg flex flex-col bg-white rounded-2xl px-20 py-24">
      </div>
    </div>

  </div>
</template>