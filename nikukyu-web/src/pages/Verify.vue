<script lang="ts" setup>
// 读取到路由向后端请求
import Loading from "../components/Loading.vue";
import Warning from "../components/Warning.vue";
import {ref} from "vue";
import {useStatusStore, useTokenStore} from "../stores/store.ts";
import {AccessType} from "../data/common.ts";
import Title from "../components/Title.vue";

const status = useStatusStore();
const token = useTokenStore();

const error = ref<boolean>(false);
const loading = ref<boolean>(true);
const message = ref<string | null>(null);

// 检查权限捏
if (
    token.token == null ||
    status.login ||
    !token.access.includes(AccessType.EMAIL_VERIFY) ||
    !token.access.includes(AccessType.OAUTH_EMAIL_VERIFY)
) {
  loading.value = false;
  message.value = "未登录";
  error.value = true;
} else {
  // 发送邮件
  loading.value = false;
}
</script>
<template>
  <div v-if="loading" class="h-full w-full grid place-items-center">
    <Loading/>
  </div>
  <div v-if="error && message" class="h-full w-full backdrop-blur flex justify-center items-center">
    <Warning :text="message"/>
  </div>
  <div v-if="(!loading) && (!error)" class="shadow-lg flex flex-col bg-white rounded-2xl px-20 py-24">
    <div>
      <Title text="验证邮箱"/>
    </div>
    <div class="mt-6">
      <p class="text-gray-400 text-xs">{{ `验证邮件将发送到 ${status.email}` }}</p>
      <p class="text-gray-400 text-xs">未注册的电子邮箱地址将自动注册</p>
      <p class="text-gray-400 text-xs">注册代表您已同意我们的 <a
          class="font-medium text-blue-400 dark:text-blue-400 hover:no-underline text-xs">隐私政策</a> 与 <a
          class="font-medium text-blue-400 dark:text-blue-400 hover:no-underline text-xs">用户条款</a></p>
    </div>
    <div></div>
  </div>
</template>
