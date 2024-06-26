<template>
    <h1>Trainee Sessions</h1>
    <div class="sessions-container">
        <Calendar :train-days="traineeTrainDays.sessions.map((session: Session) => session.beginDate)"></Calendar>
        <SessionInfoContainer :day-sessions="traineeTrainDays.sessions" is-trainee-sessions />
        <router-link :to="{name:'traineeReports', params:{traineeId:router.currentRoute.value.params.traineeId}}" class="reports-link">Reports</router-link> <!-- Add this line -->
    </div>
</template>

<script setup lang="ts">
import Calendar from "@/components/calendar/Calendar.vue"
import getTraineeSessions from "@/services/TrainerServices/sessions/getTraineeSessions.ts"
import router from "@/plugins/router.ts"
import { Ref, ref } from "vue"
import Sessions from "@/views/user/TrainerViews/models/sessions/Sessions.ts"
import Session from "@/views/user/TrainerViews/models/sessions/Session.ts"
import SessionInfoContainer from "@/views/user/TrainerViews/components/sessions/SessionInfoContainer.vue"

const traineeTrainDays: Ref<Sessions> = ref(new Sessions())

;(async () => {
    traineeTrainDays.value = await getTraineeSessions(router.currentRoute.value.params.traineeId)
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
.reports-link{
    position: relative;
    top: 0;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    padding: 0.5em;
    border-radius: 10px;
    text-decoration: none;
    font-size: 1.5em;
    font-weight: bold;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    cursor: pointer;
}

.reports-link:hover{
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-in;
}
</style>
