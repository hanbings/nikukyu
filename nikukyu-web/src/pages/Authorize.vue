<script lang="ts" setup>

// 读取到路由向后端请求
import axios from "axios";
import {useConfigStore} from "../stores/store.ts";
import {router} from "../router/router.ts";

axios.get(`${useConfigStore().api}${router.currentRoute.value.path}`)
    // 获取到后端内容
    .then(res => window.location.href = res.data.data.provider)
    .catch(err => console.error(err));

</script>
<template></template>
