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
      link: "/home/authorize/github/authorize",
      alt: "github",
      color: "#24282f",
      size: "32px"
    }
);
</script>

<template>
  <div :style="{ backgroundColor: props.color }" class="oauth-button">
    <router-link :to="props.link">
      <img :alt=props.alt :src=props.path :style="{height: size, width: size}">
    </router-link>
  </div>
</template>

<style scoped>
.oauth-button {
  height: 42px;
  width: 42px;

  border-radius: 8px;

  @apply shadow-lg flex justify-center items-center;
}
</style>