<template>
    <AddTraineeSession is-edit :session-data="sessionDetails" />
</template>

<script setup lang="ts">
import AddTraineeSession from "@/views/user/trainerViews/components/sessions/AddTraineeSession.vue"
import store from "@/store"

const sessionDetails = store.getters.sessionDetails
</script>

<style scoped></style>
