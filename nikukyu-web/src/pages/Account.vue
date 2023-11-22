<script lang="ts" setup>
import Panel from "../components/Panel.vue";
import Card from "../components/Card.vue";
import Avatar from "../components/Avatar.vue";
import Button from "../components/Button.vue";
import {ref} from "vue";
import {Account, AccountOAuth} from "../data/account.ts";
import {OAuth} from "../data/oauth.ts";

// infer type
interface Modal {
  type: 'access_application' | 'edit_account' | 'create_application' | 'edit_application',
  id?: string
}

const modalTitle: Record<Modal['type'], string> = {
  'access_application': 'Access Application',
  'edit_account': 'Edit Account',
  'create_application': 'Create Application',
  'edit_application': 'Edit Application'
}

const opened = ref<Modal>({type: 'edit_account'});

// account
const account = ref<Account>()
const applications: Array<AccountOAuth> = []
const creates: Array<OAuth> = []
</script>
<template>
  <div class="h-full w-full bg-green-200 px-12 py-12 flex flex-col gap-6">
    <Panel class="flex flex-col gap-6">
      <div class="flex flex-col gap-3">
        <div>
          <p class="text-2xl">Yours Account</p>
          <p class="text-sm text-gray-500">Click card to edit your account</p>
        </div>
        <Card class="w-[320px] flex flex-col gap-3" data-hs-overlay="#hs-slide-down-animation-modal"
              @click="opened = {type: 'edit_account'}">
          <Avatar size="lg"/>
          <p class="text-xl font-bold text-gray-700">{{ account?.nick }}</p>
          <div>
            <p class="text-sm text-gray-500">email: <a class="text-sm text-green-500">{{ account?.email }}</a></p>
            <p class="text-sm text-gray-500">uuid: <a class="text-sm text-green-500">{{ account?.auid }}</a></p>
            <p class="text-sm text-gray-500">username: <a class="text-sm text-green-500">{{ account?.email }}</a></p>
          </div>
        </Card>
        <div class="flex flex-row gap-2">
          <Button type="warning">Log out Account</Button>
          <Button type="error">Delete Account</Button>
        </div>

      </div>
      <div class="flex flex-col gap-3">
        <div>
          <p class="text-2xl">Authorized Applications</p>
          <p class="text-sm text-gray-500">Click card to change access or delete it</p>
        </div>
        <div class="flex flex-wrap gap-3">
          <Card v-for="application in applications" class="w-[320px] h-[220px]"
                data-hs-overlay="#hs-slide-down-animation-modal"
                @click="opened = { type: 'access_application', id: application.aoid }"/>
        </div>
      </div>
      <div class="flex flex-col gap-3">
        <div>
          <p class="text-2xl">Developer Settings</p>
          <p class="text-sm text-gray-500">Create your own application</p>
        </div>
        <div>
          <Button data-hs-overlay="#hs-slide-down-animation-modal" @click="opened = { type: 'create_application' }">
            Apply a new OAuth Application
          </Button>
          <Card v-for="create in creates" class="w-[320px] h-[220px]" data-hs-overlay="#hs-slide-down-animation-modal"
                @click="opened = { type: 'edit_application', id: create.ouid }"/>
        </div>
      </div>
    </Panel>
  </div>

  <div id="hs-slide-down-animation-modal"
       class="hs-overlay hidden w-full h-full fixed top-0 start-0 z-[60] overflow-x-hidden overflow-y-auto pointer-events-none">
    <div
        class="hs-overlay-open:mt-7 hs-overlay-open:opacity-100 hs-overlay-open:duration-500 mt-0 opacity-0 ease-out transition-all sm:max-w-lg sm:w-full m-3 sm:mx-auto">
      <div
          class="flex flex-col bg-white border shadow-sm rounded-lg pointer-events-auto dark:bg-gray-800 dark:border-gray-700 dark:shadow-slate-700/[.7]">
        <div class="flex justify-between items-center py-3 px-4 border-b dark:border-gray-700">
          <h3 class="text-gray-800 dark:text-white font-semi-bold">
            {{ modalTitle[opened.type] }}
          </h3>
        </div>
        <div class="p-4 overflow-y-auto">
          <p class="mt-1 text-gray-800 dark:text-gray-400">
            This is a wider card with supporting text below as a natural lead-in to additional content.
          </p>
          <p class="text-2xl">{{ opened }}</p>
        </div>
        <div class="flex justify-end items-center gap-x-2 py-3 px-4 border-t dark:border-gray-700">
          <Button type="success" data-hs-overlay="#hs-slide-down-animation-modal">Cancel</Button>
          <Button type="success" data-hs-overlay="#hs-slide-down-animation-modal">Submit</Button>
        </div>
      </div>
    </div>
  </div>

</template>