<template>
    <MainTextContainer v-if="!is_logged_in" class="home-container" />
</template>

<script setup lang="ts">
import MainTextContainer from "../home/components/MainTextContainer.vue"

import store from "../../store"
const is_logged_in = store.getters.isLogged
</script>

<style>
.home-container {
    position: relative;
    top: -15em;
    right: 30.5em;
}

body {
    background: linear-gradient(rgba(244, 244, 244, 0), rgba(0, 0, 0, 0.92)), url(../../assets/gym-home.jpg);
}
</style>
