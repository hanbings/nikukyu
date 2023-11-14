<script lang="ts" setup>
export interface ButtonProps {
  type?: 'message' | 'warning' | 'error' | 'success' | 'login'
}

const props = withDefaults(defineProps<ButtonProps>(), {type: 'message'})
</script>
<template>
  <button class="
            py-3 px-4
            inline-flex items-center gap-x-2
            text-sm font-semi-bold
            rounded-lg border border-transparent
            bg-blue-100 text-blue-800 hover:bg-blue-200
            disabled:opacity-50 disabled:pointer-events-none
            dark:hover:bg-blue-900
            dark:text-blue-400 dark:focus:outline-none dark:focus:ring-1 dark:focus:ring-gray-600
          "
          type="button">
    <slot/>
  </button>
</template>