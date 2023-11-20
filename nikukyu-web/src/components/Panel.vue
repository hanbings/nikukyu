<script lang="ts" setup>
export interface PanelProps {
  shadow?: boolean
  round?: boolean,
}

const props = withDefaults(defineProps<PanelProps>(), {shadow: true, round: true})
const base = ['py-12', 'px-12', 'bg-white']

if (props.round) base.push('rounded-xl')
if (props.shadow) base.push('shadow-lg')
</script>
<template>
  <div :class="base">
    <slot/>
  </div>
</template>