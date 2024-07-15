<template>
    <h1>Trainee Sessions</h1>
    <h2>{{ store.getters.traineeInfo.name }}</h2>
    <div class="sessions-container">
        <Calendar :train-days="traineeTrainDays.sessions.map((session: Session) => getDayFromDate(session.beginDate))"></Calendar>
        <SessionInfoContainer :day-sessions="traineeTrainDays.sessions" is-trainee-sessions />
        <router-link :to="{ name: 'traineeProfile', params: { traineeId: router.currentRoute.value.params.traineeId } }" class="reports-link">
            <img class="user-icon" src="@/assets/userIcons/man.png" alt="User Icon" />
            Profile
        </router-link>
    </div>
</template>

<script setup lang="ts">
import Calendar from "@/components/calendar/Calendar.vue"

import router from "@/plugins/router.ts"
import { Ref, ref } from "vue"
import Sessions from "@/views/user/TrainerViews/models/sessions/Sessions.ts"
import Session from "@/views/user/TrainerViews/models/sessions/Session.ts"
import SessionInfoContainer from "@/views/user/TrainerViews/components/sessions/SessionInfoContainer.vue"
import { getDayFromDate } from "@/services/utils/dateUtils/getFromDateUtils.ts"
import { getTraineeSessions } from "@/services/TrainerServices/sessions/sessionServices.ts"
import store from "../../../../../store"

const traineeTrainDays: Ref<Sessions> = ref(new Sessions())

;(async () => {
    // const map = new Map<string, any>()
    // map.set('date', new Date())
    traineeTrainDays.value = await getTraineeSessions(router.currentRoute.value.params.traineeId, null)
    traineeTrainDays.value.sessions = traineeTrainDays.value.sessions.filter((session: Session) => !session.cancelled)
    traineeTrainDays.value.total = traineeTrainDays.value.sessions.length

    console.log("Trainee Train Days", traineeTrainDays.value)
})()
</script>

<style scoped>
.sessions-container {
    height: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: start;
    margin: 1em;
    gap: 1em;
}
.reports-link {
    position: relative;
    top: 0;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    padding: 0.5em;
    border-radius: 10px;
    text-decoration: none;
    font-size: 1.5em;
    font-weight: bold;
    cursor: pointer;
}

.reports-link:hover {
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-in;
}

.user-icon {
    margin-top: 0.5em;
    width: 5em;
    height: 5em;
    border-radius: 50%;
    object-fit: cover;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    cursor: pointer;
}
</style>
