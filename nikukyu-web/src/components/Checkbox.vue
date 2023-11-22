<script lang="ts" setup>
export interface CheckboxProps {
  title: string,
  description: string,
  checked: boolean
}

const props = withDefaults(defineProps<CheckboxProps>(), {
  title: 'Archive',
  description: 'Notify me when this action happens.',
  checked: false
})
</script>
<template>
  <div class="relative flex items-start">
    <div class="flex items-center h-5 mt-1">
      <input id="hs-checkbox-archive" :checked="props.checked" aria-describedby="hs-checkbox-archive-description"
             class="
               border-gray-200 rounded text-green-500
               focus:ring-green-300 disabled:opacity-50
               disabled:pointer-events-none
               dark:bg-gray-800 dark:border-gray-700
               dark:checked:bg-blue-500 dark:checked:border-blue-500
               dark:focus:ring-offset-gray-800
             "
             name="hs-checkbox-archive"
             type="checkbox">
    </div>
    <label class="ms-3" for="hs-checkbox-archive">
      <span class="block text-sm font-semi-bold text-gray-800 dark:text-gray-300">{{ props.title }}</span>
      <span id="hs-checkbox-archive-description"
            class="block text-sm text-gray-400 dark:text-gray-500">{{ props.description }}</span>
    </label>
  </div>
</template>