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
          focus:outline-none focus:ring-4 focus:ring-blue-300
          bg-blue-500 hover:bg-blue-700
          text-white font-medium rounded-lg text-sm
          px-5 py-2.5 mr-2 mb-2
          dark:bg-blue-400 dark:hover:bg-blue-500 dark:focus:ring-blue-600"
          type="button">
    {{ props.text }}
  </button>
</template>