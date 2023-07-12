<script lang="ts" setup>
import axios from "axios";
import Warning from "../components/Warning.vue";
// @ts-ignore
import Loading from "../components/Loading.vue";
import {ref} from "vue";
import {router} from "../router/router.ts";
import {useAccountStore, useConfigStore, useStatusStore, useTokenStore} from "../stores/store.ts";
import {AccessType} from "../data/common.ts";
import {Message} from "../data/message.ts";
import {Token} from "../data/token.ts";
import {Account} from "../data/account.ts";

interface TokenResponse {
  token: Token,
  email: string
}

interface AccountResponse {
  account: Account
}

const status = useStatusStore();
const token = useTokenStore();
const account = useAccountStore();

const error = ref<boolean>(false);
const loading = ref<boolean>(true);
const message = ref<string | null>(null);

const isLogin = status.login && account.auid != null && token.token != null;

if (isLogin) {
  router.push("/home");
} else {
  // 从路由中获取信息
  let {state, code} = router.currentRoute.value.query;

  // 校验
  if (state == null || code == null) message.value = "无效请求，请检查参数是否正确";

  if (!error.value) {
    // 拼装后端数据
    let data = new FormData();

    state && data.append('state', state.toString());
    code && data.append('code', code.toString());

    // 向后端请求
    axios.post(`${useConfigStore().api}${router.currentRoute.value.path}`, data)
        .then(res => {
          let data = res.data as Message<TokenResponse>;

          console.log(data);

          if (data.code == 200) {
            // 处理全部的 Access
            let access: AccessType[] = data.data.token.access.map(a => a.replace(/_/g, '.').toLowerCase() as AccessType);

            if (!access.includes(AccessType.ACCOUNT_READ) && access.includes(AccessType.OAUTH_EMAIL_VERIFY)) {
              // 写入 token
              // @ts-ignore
              token.$patch(state => Object.keys(data.data.token).forEach(k => state[k] = data.data.token[k]));
              token.$patch(state => state.access = data.data.token.access.map(a => a.replace(/_/g, '.').toLowerCase() as AccessType));
              status.$patch(state => state.email = data.data.email);

              router.push('/login/email/verify');
            } else {
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
            }
          } else message.value = data.message;
        })
        .catch(err => message.value = err)
        .finally(() => {
          message && (error.value = true);
          loading.value = false
        });
  }
}
</script>
<template>
  <div v-if="loading" class="h-full w-full grid place-items-center">
    <Loading/>
  </div>
  <div v-if="error && message" class="h-full w-full backdrop-blur flex justify-center items-center">
    <Warning :text="message"/>
  </div>
</template>