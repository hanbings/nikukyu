<script lang="ts" setup>
interface Props {
  path: string,
  link: string,
  alt: string,
  color: string,
  size: string
}

const props = withDefaults(defineProps<Props>(), {
      path: "./resources/images/github.svg",
      link: "/login/authorize/github/authorize",
      alt: "github",
      color: "#24282f",
      size: "32px"
    }
);
</script>

<template>
  <div :style="{ backgroundColor: props.color }" class="oauth-button">
    <router-link :to="props.link" class="flex flex-row items-center pr-4 pl-2">
      <img :alt=props.alt :src=props.path :style="{height: size, width: size}">
      <p class="text-white text-xs ml-2">使用 Github 登录</p>
    </router-link>
  </div>
</template>

<style scoped>
.oauth-button {
  height: 42px;
  border-radius: 8px;

  @apply shadow-md flex flex-row items-center hover:shadow-xl transition duration-500 ease-in-out;
}
</style>