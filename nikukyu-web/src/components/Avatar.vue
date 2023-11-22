<script lang="ts" setup>
export interface AvatarProps {
  size: 'sm' | 'md' | 'lg'
  src: string
  alt: string
}

const props = withDefaults(defineProps<AvatarProps>(), {
  size: 'md',
  src: 'https://avatars.githubusercontent.com/u/38599937',
  alt: 'Avatar'
})

const base = [
  'inline-block', 'rounded-full'
]

const sizes = {
  sm: ['w-8', 'h-8'],
  md: ['w-12', 'h-12'],
  lg: ['w-16', 'h-16']
}

base.push(...sizes[props.size])
</script>
<template>
  <img :alt="props.alt" :class="base" :src="props.src">
</template>