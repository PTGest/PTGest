<template>
    <div>
        <h1>Sessions</h1>
        <div class="sessions-container">
            <Calendar @getDate="dateClicked" :train-days="trainerSessions.sessions.map((value: TrainerSession) => value.beginDate)"></Calendar>
            <SessionInfoContainer :day-sessions="trainerSessions.sessions" />
        </div>
    </div>
</template>

<script setup lang="ts">
import Calendar from "../../../components/calendar/Calendar.vue"

import TrainerSession from "@/views/user/trainerViews/models/sessions/TrainerSession.ts"
import { Ref, ref } from "vue"
import formattedDate from "@/components/utils/formatDate.ts"
import TrainerSessions from "@/views/user/trainerViews/models/sessions/TrainerSessions.ts"
import SessionInfoContainer from "@/views/user/trainerViews/components/sessions/SessionInfoContainer.vue"
import { getTrainerSessions } from "@/services/trainerServices/sessions/sessionServices.ts"
import RBAC from "@/services/utils/RBAC/RBAC.ts"
import { getTraineeSessions } from "@/services/traineeServices/traineeServices.js"

const selectedDay: Ref<string> = ref(formattedDate(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate())))
const daySessions: Ref<TrainerSession[]> = ref([])
const trainerSessions: Ref<TrainerSessions> = ref<TrainerSessions>(new TrainerSessions())

;(async () => {
    const map = new Map<string, any>()
    map.set("date", new Date())
    if (RBAC.isTrainer() || RBAC.isHiredTrainer(map)) {
        trainerSessions.value = await getTrainerSessions()
        trainerSessions.value.sessions = trainerSessions.value.sessions.filter((session: TrainerSession) => !session.cancelled)
    } else {
        trainerSessions.value = await getTraineeSessions()
        trainerSessions.value.sessions = trainerSessions.value.sessions.filter((session: TrainerSession) => !session.cancelled)
    }
})()

const dateClicked = async (date: string) => {
    daySessions.value = []
    selectedDay.value = date.trim() // Trim any extra spaces
    //filter Sessions by date
}
</script>

<style scoped>
.sessions-container {
    height: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin: 1em;
    gap: 1em;
}
</style>
