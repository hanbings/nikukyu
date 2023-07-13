<script lang="ts" setup>
// 读取到路由向后端请求
import axios from "axios";
import Loading from "../../components/Loading.vue";
import Warning from "../../components/Warning.vue";
import {ref} from "vue";
import {router} from "../../router/router.ts";
import {useConfigStore} from "../../stores/store.ts";

const error = ref<boolean>(false);
const loading = ref<boolean>(true);
const message = ref<string>("");

axios.get(`${useConfigStore().api}${router.currentRoute.value.path}`)
    // 获取到后端内容
    .then(res => window.location.href = res.data.data.provider)
    .catch(err => {
      error.value = true;
      message.value = err;
      loading.value = false;
    });
</script>
<template>
  <div v-if="loading" class="h-full w-full grid place-items-center">
    <Loading/>
  </div>
  <div v-if="error" class="h-full w-full backdrop-blur flex justify-center items-center">
    <Warning :text="message"/>
  </div>
</template>
