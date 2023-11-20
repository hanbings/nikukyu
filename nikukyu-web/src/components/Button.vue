<script lang="ts" setup>
export interface ButtonProps {
  type?: 'message' | 'warning' | 'error' | 'success',
}

const props = withDefaults(defineProps<ButtonProps>(), {type: 'message'})

const colors = {
  'message': ['bg-green-100', 'text-green-800', 'hover:bg-green-200', 'dark:hover:bg-green-900', 'dark:text-green-400'],
  'warning': ['bg-yellow-100', 'text-yellow-800', 'hover:bg-yellow-200', 'dark:hover:bg-yellow-900', 'dark:text-yellow-400'],
  'error': ['bg-red-100', 'text-red-800', 'hover:bg-red-200', 'dark:hover:bg-red-900', 'dark:text-red-400'],
  'success': ['bg-green-200', 'text-green-900', 'hover:bg-green-300', 'dark:hover:bg-green-900', 'dark:text-green-400'],
}

const base = [
  'py-3', 'px-4',
  'inline-flex', 'items-center', 'gap-x-2',
  'text-sm', 'font-semi-bold',
  'rounded-lg', 'border', 'border-transparent',
  'disabled:opacity-50', 'disabled:pointer-events-none',
  'dark:focus:outline-none', 'dark:focus:ring-1', 'dark:focus:ring-gray-600',
]

base.push(...colors[props.type])
</script>
<template>
  <button :class="base" type="button">
    <slot/>
  </button>
</template>