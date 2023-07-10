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
  <div class="h-full w-full">{{ props.text }}</div>
</template>