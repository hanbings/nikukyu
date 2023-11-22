<script lang="ts" setup>
import Panel from "../components/Panel.vue";
import Button from "../components/Button.vue";
import Input from "../components/Input.vue";
import OAuth from "../components/OAuth.vue";

const logins = [
  {icon: 'resources/images/icons8-github.svg', color: '#24292f', scale: '1.5', api: '', name: 'QQ Login'},
  {icon: 'resources/images/icons8-discord.svg', color: '#6C19ff', scale: '1.5', api: '', name: 'QQ Login'},
  {icon: 'resources/images/icons8-qq.svg', color: '#dcfce7', scale: '1.5', api: '', name: 'QQ Login'},
  {icon: 'resources/images/icons8-weibo.svg', color: '#dcfce7', scale: '1.5', api: '', name: 'QQ Login'},
  {icon: 'resources/images/icons8-baidu.svg', color: '#dcfce7', scale: '1.5', api: '', name: 'QQ Login'},
  {icon: 'resources/images/icons8-google.svg', color: '#dcfce7', scale: '1.5', api: '', name: 'QQ Login'},
  {icon: 'resources/images/icons8-microsoft.svg', color: '#dcfce7', scale: '1.5', api: '', name: 'QQ Login'},
]
</script>
<template>
  <div class="h-full w-full flex justify-center items-center">
    <div class="h-full w-full flex flex-row justify-center items-center backdrop-blur-3xl bg-green-200">
      <Panel class="flex flex-col gap-3">
        <p class="text-2xl">Login Nests ID</p>
        <div class="w-[320px]">
          <p class="text-sm text-gray-500">Unregistered email addresses will automatically be used to register a new
            account.</p>
          <p class="text-sm text-gray-500">This means you have agreed to our
            <a class="text-sm text-green-500">Privacy Policy</a>
            and
            <a class="text-sm text-green-500">Terms of Use</a>
          </p>
        </div>
        <Input class="w-[320px]" placeholder="Email / Account ID / Username"/>
        <div class="flex flex-row flex-wrap gap-2 w-[323px]">
          <Button class="w-[124px]" type="success">Login</Button>
          <OAuth v-for="login in logins" :style="{ backgroundColor: login.color }">
            <img :alt="login.name"
                 :src="login.icon"
                 :style="{width: `${login.scale}rem`, height: `${login.scale}rem`}"
            />
          </OAuth>
        </div>
      </Panel>
    </div>
  </div>
</template>