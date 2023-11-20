<script lang="ts" setup>
const base = [
  'py-3', 'px-4',
  'inline-flex', 'items-center', 'gap-x-2',
  'text-sm', 'font-semi-bold',
  'rounded-lg', 'border', 'border-transparent',
  'disabled:opacity-50', 'disabled:pointer-events-none',
  'dark:focus:outline-none', 'dark:focus:ring-1', 'dark:focus:ring-gray-600'
]
</script>
<template>
  <button :class="base" type="button">
    <slot/>
  </button>
</template>