<script lang="ts" setup>
interface Props {
  text: string
}

const props = withDefaults(defineProps<Props>(), {
      text: ""
    }
);

</script>

<template>
  <p class="text-gray-900 text-2xl break-all">{{ props.text }}</p>
</template>