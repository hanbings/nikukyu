<script lang="ts" setup>
import Panel from "../components/Panel.vue";
import Button from "../components/Button.vue";
import Checkbox from "../components/Checkbox.vue";
</script>
<template>
  <div class="h-full w-full flex justify-center items-center bg-green-200">
    <Panel class="flex flex-col gap-3">
      <p class="text-2xl">Authorize</p>
      <div class="w-[320px]">
        <p class="text-sm text-gray-500">The Application <a class="text-sm text-green-500 font-bold">Nests ID</a>
          requests access to your account.</p>
        <p class="text-sm text-gray-500">Current login: <a class="text-sm text-green-500">hanbings@hanbings.io</a></p>
        <p class="text-sm text-green-500">Is it not use this account?</p>
      </div>
      <div class="w-[320px]">
        <Checkbox checked description="Access your email address" title="email"/>
        <Checkbox description="Access your phone number" title="phone"/>
        <Checkbox description="Access your username" title="username"/>
      </div>
      <div class="w-[320px]">
        <p class="text-sm text-gray-500">You may want to check the app's
          <a class="text-sm text-green-500">Privacy Policy</a>
          and
          <a class="text-sm text-green-500">Terms of Use</a>
        </p>
      </div>
      <div class="flex flex-row gap-2">
        <Button type="success">Approve</Button>
        <Button type="warning">Cancel</Button>
      </div>
    </Panel>
  </div>
</template>