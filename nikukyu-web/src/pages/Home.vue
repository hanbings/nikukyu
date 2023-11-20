<script lang="ts" setup>
import Button from "../components/Button.vue";
</script>
<template>
  <div class="h-[100vh] w-[100vw] flex flex-col gap-6 justify-center items-center">
    <p class="text-green-400 text-4xl">🍀 This is Nikukyu, A Open Source SSO （Single Sign-On) software.</p>
    <div class="flex gap-3">
      <Button>
        <RouterLink to="/login">
          Get Started
        </RouterLink>
      </Button>
      <Button>
        <a href="https://github.com/hanbings/nikukyu">
          Open Source
        </a>
      </Button>
      <Button>
        <a href="https://github.com/hanbings/nikukyu">
          Document
        </a>
      </Button>
    </div>
  </div>
</template>