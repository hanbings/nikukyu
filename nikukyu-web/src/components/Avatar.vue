<script lang="ts" setup>
interface Props {
  avatar?: string,
  alt?: string,
  size?: string
}

withDefaults(defineProps<Props>(), {
      avatar: "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png",
      alt: "Avatar",
      size: "64px"
    }
);

</script>

<template>
  <img class="rounded-full shadow-lg" v-bind:style="{ width: size, height: size }" v-bind:alt="alt" v-bind:src="avatar"/>
</template>