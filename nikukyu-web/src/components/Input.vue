<script lang="ts" setup>
export interface InputProps {
  placeholder?: string
}

const props = withDefaults(defineProps<InputProps>(), {placeholder: "This is Placeholder"})
</script>
<template>
  <input :placeholder="props.placeholder"
         class="
           py-3 px-4 block w-full
           border-gray-200 rounded-lg
           text-sm
           focus:border-green-500 focus:ring-green-500
           disabled:opacity-50 disabled:pointer-events-none
           dark:bg-slate-900 dark:border-gray-700 dark:text-gray-400 dark:focus:ring-gray-600
         "
         type="text">
</template>