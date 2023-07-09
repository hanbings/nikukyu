<script lang="ts" setup>
interface Props {
  slogan: string
}

const props = withDefaults(defineProps<Props>(), {
      slogan: "🍀 This is Nikukyu, A Open Source SSO （Single Sign-On) software."
    }
);
</script>

<template>
  <div class="text-2xl text-blue-500">{{ props.slogan }}</div>
</template>