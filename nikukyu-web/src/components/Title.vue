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
  <p class="text-gray-900 text-2xl">{{ props.text }}</p>
</template>