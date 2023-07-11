<script lang="ts" setup>
interface Props {
  text: string
}

const props = withDefaults(defineProps<Props>(), {
      text: "Button"
    }
);
</script>

<template>
  <button class="
          focus:outline-none focus:ring-4 focus:ring-primary-300
          bg-primary-500 hover:bg-primary-300
          text-white font-medium rounded-lg text-sm
          px-5 py-2.5 mr-2 mb-2"
          style="min-width: 72px"
          type="button">
    {{ props.text }}
  </button>
</template>