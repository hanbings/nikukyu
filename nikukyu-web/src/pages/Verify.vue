<script lang="ts" setup>
import axios from "axios";
// @ts-ignore
import Loading from "../components/Loading.vue";
import Warning from "../components/Warning.vue";
import Title from "../components/Title.vue";
import Button from "../components/verify/Button.vue";
// @ts-ignore
import CodeInput from "../components/verify/CodeInput.vue";
import {ref} from "vue";
import {AccessType} from "../data/common.ts";
import {useConfigStore, useStatusStore, useTokenStore} from "../stores/store.ts";
import {Message} from "../data/message.ts";
import {Token} from "../data/token.ts";
import {Account} from "../data/account.ts";
import {router} from "../router/router.ts";

interface TokenResponse {
  token: Token
}

interface AccountResponse {
  account: Account
}

const config = useConfigStore();
const status = useStatusStore();
const token = useTokenStore();

const error = ref<boolean>(false);
const loading = ref<boolean>(true);
const message = ref<string | null>(null);
const unverified = ref<boolean | null>(null);

const input = ref(CodeInput);
const background = ref({background: `url('${config.loginBackground}')`});

// 检查权限捏
if (
    !token.token ||
    !token.access.includes(AccessType.EMAIL_VERIFY) ||
    !token.access.includes(AccessType.OAUTH_EMAIL_VERIFY)
) {
  loading.value = false;
  message.value = "未登录";
  error.value = true;
} else {
  // 等待输入验证码
  loading.value = false;
}

const resetVerify = () => unverified.value = false;
const sendVerify = () => {
  loading.value = true;

  let inputCode = "";
  input.value.data().box.forEach((v: string) => {
    if (v != "") inputCode += v
  });

  if (inputCode.length != 6) return;

  // 请求 token
  axios.post(
      `${useConfigStore().api}$/login/token`,
      {email: status.email, code: inputCode},
      {headers: {Authorization: `Bearer ${token.token}`}}
  ).then(res => {
    let data = res.data.data as Message<TokenResponse>;

    // 检查是否成功
    if (data.code != 200) {
      message.value = data.message;
      unverified.value = true;
      return;
    }

    // 更新 token
    // @ts-ignore
    token.$patch(state => Object.keys(data.data.token).forEach(k => state[k] = data.data.token[k]));
    token.$patch(state => state.access = data.data.token.access.map(a => a.replace(/_/g, '.').toLowerCase() as AccessType));

    // 请求账号信息
    axios.get(`${useConfigStore().api}/account`, {headers: {'Authorization': `Bearer ${token.token}`}})
        .then(res => {
          let data = res.data as Message<AccountResponse>;

          console.log(data);
          console.log(data.data.account);

          // @ts-ignore
          account.$patch(state => Object.keys(data.data.account).forEach(k => state[k] = data.data.account[k]));
          status.$patch(state => state.login = true);

          router.push('/home');
        });

  }).catch(err => {
    message.value = err
    unverified.value = true;
  }).finally(() => {
    message && (error.value = true);
    loading.value = false;
  });
}

loading.value = false;
</script>
<template>
  <div v-if="loading" class="h-full w-full grid place-items-center">
    <Loading/>
  </div>
  <div v-if="error && message" class="h-full w-full backdrop-blur flex justify-center items-center">
    <Warning :text="message"/>
  </div>
  <div v-if="(!loading) && (!error)" class="h-full w-full bg-fixed bg-cover bg-center"
       v-bind:style="{backgroundImage: background.background}">
    <div class="h-full w-full backdrop-blur flex justify-center items-center">
      <div class="shadow-lg flex flex-col bg-white rounded-2xl px-20 py-24">
        <div>
          <Title text="验证邮箱"/>
        </div>
        <div class="mt-6">
          <p class="text-gray-400 text-xs">{{ `验证邮件将发送到 ` }}
            <a class="font-medium text-blue-400 hover:no-underline text-xs">{{ `${status.email}` }}</a>
          </p>
          <p class="text-gray-400 text-xs">未注册的电子邮箱地址将自动注册</p>
          <p class="text-gray-400 text-xs">注册代表您已同意我们的 <a
              class="font-medium text-blue-400 hover:no-underline text-xs">隐私政策</a> 与 <a
              class="font-medium text-blue-400 hover:no-underline text-xs">用户条款</a></p>
        </div>
        <div class="mt-6">
          <CodeInput ref="input" :onchange="resetVerify"/>
          <p v-if="unverified" class="text-error mt-2 text-xs">验证码错误</p>
        </div>
        <div class="mt-6">
          <Button class="w-full" text="验证邮箱" @click="sendVerify"/>
        </div>
      </div>
    </div>
  </div>
</template>
