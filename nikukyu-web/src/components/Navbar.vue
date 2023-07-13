<script lang="ts" setup>
import Avatar from "./Avatar.vue";
import {useAccountStore} from "../stores/store.ts";

const account = useAccountStore();

console.log(account.avatar);
</script>

<template>
  <div class="w-full shadow-lg flex items-center" style="height: 60px; background-color:rgba(255,255,255,0.8)">
    <Avatar :avatar="account.avatar" class="ml-auto mr-2" size="48px"/>
  </div>
</template>
