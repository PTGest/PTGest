<template>
    <div class="cancel-container">
        <input v-model="reason" placeholder="Reason" />
        <p>Are you sure you want to cancel this session?</p>
        <div class="buttons-row">
            <button class="yes-btn btn" @click="handleCancelSession">Yes</button>
            <button class="no-btn btn" @click="router.back()">No</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import router from "@/plugins/router.ts"
import { ref } from "vue"
import { cancelSession } from "@/services/TrainerServices/sessions/sessionServices.ts"
import CancelSessionRequest from "@/views/user/TrainerViews/models/sessions/CancelSessionRequest.ts"

const reason = ref("")

const handleCancelSession = async () => {
    await cancelSession(router.currentRoute.value.params.sessionId, new CancelSessionRequest(reason.value))
    console.log("Session Cancelled")
}
</script>

<style scoped>
.cancel-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: var(--main-primary-color);
    width: 25em;
    margin: 0 auto;
    padding: 1em;
    border: 1px solid black;
    border-radius: 10px;
}
.buttons-row {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 1em;
    width: 100%;
}
input {
    width: 100%;
    padding: 0.5em;
    border-radius: 5px;
    border: none;
    outline: none;
    font-size: 1.2em;
}
.btn {
    padding: 0.5em 1em;
    border-radius: 5px;
    background-color: var(--main-secondary-color);
    color: whitesmoke;
    border: none;
    outline: none;
    cursor: pointer;
    transition: 0.2s background-color;
}

.btn:hover {
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    transition: 0.2s background-color;
}
</style>
