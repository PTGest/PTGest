
<template>
  <div class="user-icon">
    <img :src="url" alt="User Icon">
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, watch } from 'vue';
import store from "../../../store";

const props = defineProps({
  is_open: Boolean,
  is_mobile_view: Boolean
});

// Use a reactive ref for the image URL
const url = ref("");

// Watch for changes in store data and update the URL accordingly
watch(() => store.getters.userData.profileImage, (newVal) => {
  url.value = require(`../../../assets/${newVal}`);
});

// Initialize URL with the initial value from the store
url.value = require(`../../../assets/${store.getters.userData.profileImage}`);

</script>




<style scoped>

</style>