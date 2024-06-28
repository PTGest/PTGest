<template>
    <div class="details-container">
        <div class="details-header">
            <h1>Session Details</h1>
            <font-awesome-icon @click="handleEdit" v-if="!isEdit" class="icon" :icon="faPenToSquare"></font-awesome-icon>
            <!--            <font-awesome-icon @click="editSessionDetails" v-else class="icon" :icon="faCheck"></font-awesome-icon>-->
        </div>
        <div>
            <p v-if="RBAC.isCompany()">Session Trainer: {{ sessionDetails.trainer }}</p>
            <h2>{{ store.getters.traineeInfo.name }}</h2>
            <p>{{ sessionDetails.type }}</p>
            <p>Begin Date: {{ dateFormatter(sessionDetails.beginDate) }}</p>
            <p v-if="sessionDetails.endDate != null">Session End Date: {{ sessionDetails.endDate }}</p>
            <p v-if="sessionDetails.location != null">Session Location: {{ sessionDetails.location }}</p>
            <p v-if="sessionDetails.notes != null">Session Status: {{ sessionDetails.notes }}</p>

            <p v-if="sessionDetails.reason != null">Session Status: {{ sessionDetails.reason }}</p>
            <p v-if="sessionDetails.source != 'NONE'">Session Status: {{ sessionDetails.source }}</p>
        </div>
    </div>
</template>

<script setup lang="ts">
import TrainerSessionDetails from "@/views/user/TrainerViews/models/sessions/TrainerSessionDetails.ts"
import { Ref, ref } from "vue"
import router from "@/plugins/router.ts"
import getSessionDetails from "@/services/TrainerServices/sessions/getSessionDetails.ts"
import RBAC from "@/services/utils/RBAC/RBAC.ts"
import store from "../../../../../store"
import dateFormatter from "../../../../../services/utils/dateFormatter.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faPenToSquare } from "@fortawesome/free-solid-svg-icons"

const isEdit = ref(false)
const sessionDetails: Ref<TrainerSessionDetails> = ref(new TrainerSessionDetails())

;(async () => {
    sessionDetails.value = await getSessionDetails(router.currentRoute.value.params.sessionId)
    store.commit("setSessionDetails", sessionDetails.value)
})()

const handleEdit = () => {
    router.push(`/sessions/session/${router.currentRoute.value.params.sessionId}/edit`)
}
</script>

<style scoped>
.details-container {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    width: 25em;
    height: 33.5em;
    background-color: var(--main-primary-color);
    border-radius: 5px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
    overflow-y: scroll;
    padding: 1em;
}

h1 {
    font-size: 2em;
}

.details-header {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    margin-bottom: 1em;
}
.icon {
    position: relative;
    right: -2em;
    cursor: pointer;
}
</style>
