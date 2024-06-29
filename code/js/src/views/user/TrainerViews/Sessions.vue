<template>
    <div>
        <h1>Sessions</h1>
        <div class="sessions-container">
            <Calendar @get-date="dateClicked" :train-days="trainerSessions.sessions.map((value: TrainerSession) => value.beginDate)"></Calendar>
            <SessionInfoContainer :day-sessions="trainerSessions.sessions" />
        </div>
    </div>
</template>

<script setup lang="ts">
import Calendar from "../../../components/calendar/Calendar.vue"

import TrainerSession from "@/views/user/TrainerViews/models/sessions/TrainerSession.js"
import { Ref, ref } from "vue"
import formattedDate from "@/components/utils/formatDate.js"
import TrainerSessions from "@/views/user/TrainerViews/models/sessions/TrainerSessions.ts"
import getTrainerSessions from "@/services/TrainerServices/sessions/getTrainerSessions.ts"
import SessionInfoContainer from "@/views/user/TrainerViews/components/sessions/SessionInfoContainer.vue"


const selectedDay: Ref<string> = ref(formattedDate(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate())))
const daySessions: Ref<TrainerSession[]> = ref([])
const trainerSessions: Ref<TrainerSessions> = ref<TrainerSessions>(new TrainerSessions())

;(async () => {
    trainerSessions.value = await getTrainerSessions(null)
    console.log(trainerSessions.value)
})()

const dateClicked = (date: string) => {
    daySessions.value = []
    selectedDay.value = date.trim() // Trim any extra spaces
    //filter Sessions by date
    for (const element of trainerSessions.value.sessions) {
        if (element.beginDate.trim() === selectedDay.value.trim()) {
            // Trim the session date too
            daySessions.value.push(element)
        }
    }
    console.log(daySessions.value)
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
