<template>
  <div>
    <div class="flex flex-row gap-2">
      <input class="input" type="text" placeholder="邮箱地址" v-model="email" @input="onInput">
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from "vue";

interface Props {
  text: string
}

const props = withDefaults(defineProps<Props>(), {
      text: ""
    }
);

const email = ref(props.text);
const emits = defineEmits(["update:email"]);

const onInput = () => {
  emits("update:email", email.value);
};
</script>

<style scoped>
.input {
  display: flex;
  min-width: 260px;
  height: 42px;
  font-size: 14px;
  font-weight: bold;
  color: #212529;
  text-align: center;
  outline: none;
  /* 四边都有细细的黑线 */
  border: 1px solid #9CA3AF;
  border-radius: 6px;

  @apply shadow-sm focus:border-primary-500 focus:border-2;
}
</style>