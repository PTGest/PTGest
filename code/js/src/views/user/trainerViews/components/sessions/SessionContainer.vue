<template>
    <div v-if="isLoading">
        <ProgressSpinner />
    </div>
    <div v-else class="details-container">
        <font-awesome-icon class="x-icon" @click="$router.go(-1)" :icon="faX" />
        <div class="details-header">
            <h1>Session Details</h1>
            <font-awesome-icon @click="handleEdit" v-if="!isEdit && canCancel" class="icon" :icon="faPenToSquare"></font-awesome-icon>
        </div>
        <div class="details">
            <p v-if="RBAC.isCompany()">Session Trainer: {{ sessionDetails.trainer }}</p>
            <h2>{{ store.getters.traineeInfo.name }}</h2>
            <div class="details-btn" @click="workoutDetailsOpen = true">Workout Details</div>
            <p>{{ sessionDetails.type }}</p>
            <p>Begin Date: {{ dateFormatter(sessionDetails.beginDate) }}</p>
            <p v-if="sessionDetails.endDate != null">Session End Date: {{ dateFormatter(sessionDetails.endDate) }}</p>
            <p v-if="sessionDetails.location != null">Session Location: {{ sessionDetails.location }}</p>
            <p v-if="sessionDetails.notes != null">Session notes: {{ sessionDetails.notes }}</p>
            <p v-if="sessionDetails.source != null">Session Status: {{ sessionDetails.source }}</p>
            <p v-if="sessionDetails.reason != null">Cancel Reason: {{ sessionDetails.reason }}</p>
        </div>
        <WorkoutDetails @close="workoutDetailsOpen = false" v-if="workoutDetailsOpen" :workoutId="sessionDetails.workoutId" is-session-details></WorkoutDetails>
        <button v-if="!sessionDetails.cancelled && canCancel" class="cancel-btn" @click="cancelSession">Cancel Session</button>
        <SessionFeedback v-if="canGiveFeedback" class="session-feedback" :session-feedback="sessionDetails.feedback"></SessionFeedback>
    </div>
</template>

<script setup lang="ts">
import TrainerSessionDetails from "@/views/user/trainerViews/models/sessions/TrainerSessionDetails.ts"
import { computed, Ref, ref } from "vue"
import router from "@/plugins/router.ts"

import RBAC from "@/services/utils/RBAC/RBAC.ts"
import store from "../../../../../store"
import dateFormatter from "../../../../../services/utils/dateUtils/dateFormatter.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faInfo, faPenToSquare, faX } from "@fortawesome/free-solid-svg-icons"
import { getSessionDetails } from "@/services/trainerServices/sessions/sessionServices.ts"
import SessionFeedback from "@/views/user/trainerViews/components/sessions/SessionFeedback.vue"
import ProgressSpinner from "primevue/progressspinner"
import { getTraineeSessionDetails } from "@/services/traineeServices/traineeServices.js"
import WorkoutDetails from "@/views/user/trainerViews/components/workouts/WorkoutDetails.vue"
import workout from "@/views/user/trainerViews/models/workouts/Workout.ts"

const workoutDetailsOpen = ref(false)
const isEdit = ref(false)
const isLoading = ref(true)
const sessionDetails: Ref<TrainerSessionDetails> = ref(new TrainerSessionDetails())
const canGiveFeedback = computed(() => {
    const today = new Date()
    return sessionDetails.value.beginDate != null && new Date(sessionDetails.value.beginDate) <= today
})

const canCancel = computed(() => {
    const today = new Date()
    const sessionDate = new Date(sessionDetails.value.beginDate)

    // Calculate the difference in milliseconds
    const diff = sessionDate.getTime() - today.getTime()

    // Convert difference to hours
    const diffInHours = diff / (1000 * 60 * 60)

    // Check if the difference is more than 24 hours
    return diffInHours > 24
})

;(async () => {
    if (RBAC.isTrainer() || RBAC.isHiredTrainer()) {
        sessionDetails.value = await getSessionDetails(router.currentRoute.value.params.sessionId)
        isLoading.value = false
        store.commit("setSessionDetails", sessionDetails.value)
    } else {
        sessionDetails.value = await getTraineeSessionDetails(router.currentRoute.value.params.sessionId)
        isLoading.value = false
        store.commit("setSessionDetails", sessionDetails.value)
    }
})()

const handleEdit = () => {
    router.push(`/sessions/session/${router.currentRoute.value.params.sessionId}/edit`)
}

const cancelSession = () => {
    router.push({ name: "cancelSession", params: { sessionId: router.currentRoute.value.params.sessionId } })
}
</script>

<style scoped>
.details-container {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    width: 25em;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
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
.x-icon {
    align-self: flex-end;
    cursor: pointer;
}
.cancel-btn {
    margin-top: 3em;
    margin-bottom: 2em;
    background-color: rgba(255, 0, 0, 0.5);
    color: whitesmoke;
}
.session-feedback {
    position: absolute;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    top: 0;
    left: 110%;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    padding: 1em;
}

.details-btn {
    padding: 1em;
    background-color: var(--main-secondary-color);
    border-radius: 10px;
}

.details-btn:hover {
    cursor: pointer;
    color: var(--main-primary-color);
    transition: 0.2s ease-in;
}
</style>
