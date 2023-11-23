<script lang="ts" setup>
import Panel from "../components/Panel.vue";
import Card from "../components/Card.vue";
import Avatar from "../components/Avatar.vue";
import Button from "../components/Button.vue";
import {ref} from "vue";
import {Account, AccountOAuth} from "../data/account.ts";
import {OAuth} from "../data/oauth.ts";
import Input from "../components/Input.vue";
import Checkbox from "../components/Checkbox.vue";
import {AccessType} from "../data/token.ts";

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
       class="no-scrollbar hs-overlay hidden w-full h-full fixed top-0 start-0 z-[60] overflow-x-hidden overflow-y-auto pointer-events-none">
    <div
        class="hs-overlay-open:mt-7 hs-overlay-open:opacity-100 hs-overlay-open:duration-500 mt-0 opacity-0 ease-out transition-all sm:max-w-lg sm:w-full m-3 sm:mx-auto">
      <div
          class="flex flex-col bg-white border shadow-sm rounded-lg pointer-events-auto dark:bg-gray-800 dark:border-gray-700 dark:shadow-slate-700/[.7]">
        <div class="flex justify-between items-center py-3 px-4 border-b dark:border-gray-700">
          <h3 class="text-gray-800 dark:text-white font-semi-bold">
            {{ modalTitle[opened.type] }}
          </h3>
        </div>
        <div v-show="opened.type === 'edit_account'" class="p-4 overflow-y-auto flex flex-col gap-3">
          <p class="text-gray-700 dark:text-gray-300">Edit your account</p>
          <div class="flex flex-col gap-2">
            <Card class="flex flex-col gap-2">
              <Input placeholder="Nickname"/>
              <p class="text-sm text-gray-500 dark:text-gray-300">
                This name is a custom nickname and can be repeated with others.
              </p>
            </Card>
            <Card class="flex flex-col gap-2">
              <Avatar size="lg"/>
              <div>
                <Button>Set a new Avatar</Button>
              </div>
              <div>
                <p class="text-sm text-yellow-300 dark:text-yellow-500">Your avatar may be reviewed by us.</p>
                <p class="text-sm text-gray-500 dark:text-gray-300">Your avatar may be visible to other users.</p>
              </div>
            </Card>
            <Card class="flex flex-col gap-2">
              <div>
                <Button>Set a Background</Button>
              </div>
              <div>
                <p class="text-sm text-gray-500 dark:text-gray-300">It is recommended to use an image that fits the
                  screen of your device, and we will automatically crop it to the appropriate size.</p>
              </div>
            </Card>
            <div id="color picker"></div>
          </div>
        </div>
        <div v-show="opened.type === 'create_application'" class="p-4 overflow-y-auto flex flex-col gap-3">
          <p class="text-gray-700 dark:text-gray-300">Create your own application</p>

          <Card class="flex flex-col gap-2">
            <Input placeholder="Application Name"/>
            <Input placeholder="Application Description"/>
            <Input placeholder="Application Homepage"/>
            <Input placeholder="Private Policy"/>
            <Input placeholder="Teams of Use"/>
            <Input placeholder="Theme Color (Using HEX RGB value)"/>
            <p class="text-sm text-gray-500 dark:text-gray-300">The content here will be displayed on the authorization
              page.</p>
          </Card>
          <Card class="flex flex-col gap-2">
            <div>
              <Button>Set a Background</Button>
            </div>
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-300">
                It is recommended to use an image that fits the
                screen of your device, and we will automatically crop it to the appropriate size.
              </p>
            </div>
          </Card>
          <Card id="redirect list" class="flex flex-col gap-3">
            <div class="flex flex-col gap-1">
              <!-- Input Group -->
              <div id="hs-wrapper-for-copy" class="space-y-3">
                <Input id="hs-content-for-copy"
                       type="text"
                       placeholder="Redirect"/>
              </div>

              <p class="mt-3 text-end">
                <button type="button"
                        data-hs-copy-markup='{ "targetSelector": "#hs-content-for-copy", "wrapperSelector": "#hs-wrapper-for-copy", "limit": 10 }'
                        id="hs-copy-content"
                        class="py-1.5 px-2 inline-flex items-center gap-x-1 text-xs font-medium rounded-full border border-dashed border-gray-200 bg-white text-gray-800 hover:bg-gray-50 disabled:opacity-50 disabled:pointer-events-none dark:bg-gray-800 dark:border-gray-700 dark:text-gray-300 dark:hover:bg-gray-700 dark:focus:outline-none dark:focus:ring-1 dark:focus:ring-gray-600">
                  <svg class="flex-shrink-0 w-3.5 h-3.5" xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                       viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                       stroke-linejoin="round">
                    <path d="M5 12h14"/>
                    <path d="M12 5v14"/>
                  </svg>
                  Add Item
                </button>
              </p>
              <!-- End Input Group -->
            </div>
            <div class="flex flex-col">
              <p class="text-sm text-gray-500 dark:text-gray-300">Limit up to 10 redirect addresses.</p>
              <p class="text-sm text-gray-500 dark:text-gray-300">The redirect url address of the application.</p>
              <a class="text-sm text-green-500" href="https://www.oauth.com/oauth2-servers/redirect-uris/">
                About redirect url
              </a>
            </div>
          </Card>
          <Card id="access multiple checkbox" class="flex flex-col gap-2">
            <div class="flex flex-col gap-1">
              <Checkbox v-for="access in AccessType" :title="access"></Checkbox>
            </div>
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-300">
                Check the permissions that need to be requested from the user.
              </p>
              <p class="text-sm text-yellow-300 dark:text-yellow-500">Some permissions are sensitive permissions and the
                user will be prompted on the authorization page.</p>
            </div>
          </Card>
        </div>
        <div class="flex justify-end items-center gap-x-2 py-3 px-4 border-t dark:border-gray-700">
          <Button type="success" data-hs-overlay="#hs-slide-down-animation-modal">Cancel</Button>
          <Button type="success" data-hs-overlay="#hs-slide-down-animation-modal">Submit</Button>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.no-scrollbar {
  -ms-overflow-style: none;
}

.no-scrollbar::-webkit-scrollbar {
  width: 0 !important
}
</style>