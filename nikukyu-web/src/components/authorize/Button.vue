<script lang="ts" setup>
interface Props {
  text: string
}

const props = withDefaults(defineProps<Props>(), {
      text: "Button"
    }
);
</script>

<template>
  <button class="text-gray-50 font-semi-bold rounded-lg text-sm px-8 py-2.5 mr-2 mb-2"
          style="height: 42px;"
          type="button">
    {{ props.text }}
  </button>
</template>