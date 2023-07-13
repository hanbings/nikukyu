<script lang="ts" setup>
import axios from "axios";
import Warning from "../../components/Warning.vue";
// @ts-ignore
import Loading from "../../components/Loading.vue";
import {onMounted, onUnmounted, ref} from "vue";
import {OAuth} from "../../data/oauth.ts";
import {router} from "../../router/router.ts";
import {Message} from "../../data/message.ts";
import {AccessType} from "../../data/common.ts";
import {useAccountStore, useConfigStore, useStatusStore, useTokenStore} from "../../stores/store.ts";
import Avatar from "../../components/Avatar.vue";
import Title from "../../components/Title.vue";
import Button from "../../components/verify/Button.vue";

const status = useStatusStore();
const config = useConfigStore();
const token = useTokenStore();
const account = useAccountStore();

const error = ref<boolean>(false);
const loading = ref<boolean>(true);
const message = ref<string | null>(null);
const oauth = ref<OAuth>();

interface CodeResponse {
  code: string,
  state: string
}

onMounted(() => status.navbar = false);
onUnmounted(() => status.navbar = true);

if (!status.login) {
  status.redirect = router.currentRoute.value.fullPath;
  router.push("/login");
} else {
  status.redirect = "";
}

if (oauth) {
  let clientId = router.currentRoute.value.query.client_id;

  axios.get(`${config.api}/oauth/${clientId}`, {headers: {'Authorization': `Bearer ${token.token}`}})
      .then(res => {
        let data = res.data as Message<OAuth>;

        if (data.code == 200) {
          let access: AccessType[] = data.data.access.map(a => a.replace(/_/g, '.').toLowerCase() as AccessType);

          // @ts-ignore
          Object.keys(data.data).forEach(k => oauth.value[k] = data.data[k]);
          // @ts-ignore
          oauth['access'] = access;
        } else {
          //error.value = true;
          message.value = data.message;
        }
      })
      .catch(err => {
        error.value = true;
        message.value = err;
      }).finally(() => loading.value = false);
}

const approve = () => {
  let {clientId, state, redirectUri, scope} = router.currentRoute.value.query;

  let body = new FormData();
  redirectUri && body.append("redirect_uri", redirectUri.toString());
  clientId && body.append("client_id", clientId.toString());
  state && body.append("state", state.toString());
  scope && body.append("scope", scope.toString());
  body.append("response_type", "code");

  axios.post(`${config.api}/oauth/authorize`, body, {headers: {'Authorization': `Bearer ${token.token}`}})
      .then(res => {
        let data = res.data as Message<CodeResponse>;

        if (data.code == 200) {
          router.push(`/oauth/authorize?code=${data.data.code}&state=${data.data.state}`);
        } else {
          error.value = true;
          message.value = data.message;
        }
      })
      .catch(err => {
        error.value = true;
        message.value = err;
      }).finally(() => loading.value = false);
}

// 背景图片 url
// @ts-ignore
const background = ref({background: oauth && oauth.background ? `url('${oauth.background}')` : `url('${config.indexBackground}')`});
</script>
<template>
  <div v-if="loading" class="h-full w-full grid place-items-center">
    <Loading/>
  </div>

  <div v-if="!loading" class="h-full w-full bg-fixed bg-cover bg-center"
       v-bind:style="{backgroundImage: background.background}">
    <div class="h-full w-full backdrop-blur flex justify-center items-center">
      <Warning v-if="error" text="发生错误"/>

      <div v-if="!error" class="shadow-lg flex flex-col bg-white rounded-2xl px-20 py-24">
        <div class="flex">
          <Avatar :avatar="oauth ? oauth.avatar : account.avatar" size="72px"/>
          <Avatar :avatar="account.avatar" class="-ml-8" size="72px"/>
        </div>
        <div class="mt-6">
          <Title :text="oauth ? oauth.name : '该应用没有设置名称'"/>
        </div>
        <div class="mt-6">
          <p class="text-gray-400 text-xs">{{ oauth ? oauth.description : '该应用没有设置描述' }}</p>
        </div>
        <div class="mt-2">
          <p class="text-gray-400 text-xs">{{ `正在使用 ${account.nick} 进行授权` }}</p>
          <p class="text-gray-400 text-xs">授权后应用将获得</p>
          <li v-for="item in oauth ? oauth.access : []" :key="item"> {{ item }}</li>
        </div>
        <div class="mt-6">
          <p class="text-gray-400 text-xs">{{ oauth ? `查看应用的 ` : `该应用没有设置隐私协议` }}
            <a v-if="oauth && oauth.policy" class="font-medium text-blue-400 hover:no-underline text-xs">隐私政策</a>
          </p>
          <p class="text-gray-400 text-xs">{{ oauth ? `查看应用的 ` : `该应用没有设置用户条款` }}
            <a v-if="oauth && oauth.tos" class="font-medium text-blue-400 hover:no-underline text-xs">隐私政策</a>
          </p>
        </div>
        <div class="mt-6">
          <Button text="授权" @click="approve"/>
        </div>
      </div>
    </div>
  </div>
</template>