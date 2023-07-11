<script lang="ts" setup>
import Title from "./Title.vue";
import Button from "./index/Button.vue";
import {router} from "../router/router.ts";

interface Props {
  text: string,
  title?: string,
  continueText?: string,
  cancelText?: string,
  continue?: string,
  cancel?: string,
  isContinue?: boolean,
  isCancel?: boolean,
}

const props = withDefaults(defineProps<Props>(), {
      title: "遇到了错误",
      cancelText: "取消",
      continueText: "继续",
      continue: "/login",
      cancel: "/",
      isContinue: true,
      isCancel: true,
    }
);

function go(path: string) {
  router.push(path);
}

</script>

<template>
  <div class="h-full w-full backdrop-blur flex justify-center items-center">
    <div class="shadow-lg flex flex-col bg-white rounded-2xl px-16 py-16">
      <div class="flex flex-col" style="width: 320px">
        <Title :text="props.title"/>
        <div class="mt-6">
          <p class="text-gray-400 text-xs break-all">{{ props.text }}</p>
        </div>
        <div class="mt-16 flex flex-row-reverse">
          <Button v-if="isContinue" :text="continueText" class="bg-warning hover:bg-warning-200 focus:ring-warning-200"
                  @click="go(props.continue)"/>
          <Button v-if="isCancel" :text="cancelText" @click="go(props.cancel)"/>
        </div>
      </div>
    </div>
  </div>
</template>