<template>
    <div>
        <h1>Sessions</h1>
        <div class="sessions-container">
            <Calendar @getDate="dateClicked" :train-days="trainerSessions.sessions.map((value: TrainerSession) => value.beginDate)"></Calendar>
            <SessionInfoContainer :day-sessions="trainerSessions.sessions"/>
        </div>
    </div>
</template>

<script setup lang="ts">
import Calendar from "../../../components/calendar/Calendar.vue"

import TrainerSession from "@/views/user/TrainerViews/models/sessions/TrainerSession.js"
import { Ref, ref } from "vue"
import formattedDate from "@/components/utils/formatDate.js"
import TrainerSessions from "@/views/user/TrainerViews/models/sessions/TrainerSessions.ts"
import SessionInfoContainer from "@/views/user/TrainerViews/components/sessions/SessionInfoContainer.vue"
import {getTrainerSessions} from "@/services/TrainerServices/sessions/sessionServices.js";
import RBAC from "@/services/utils/RBAC/RBAC.ts";
import {getTraineeSessions} from "@/services/TraineeServices/TraineeServices.js";

const selectedDay: Ref<string> = ref(formattedDate(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate())))
const daySessions: Ref<TrainerSession[]> = ref([])
const trainerSessions: Ref<TrainerSessions> = ref<TrainerSessions>(new TrainerSessions())

;(async () => {
    const map = new Map<string, any>()
    map.set("date", new Date())
    if(RBAC.isTrainer() || RBAC.isHiredTrainer(map)){
        trainerSessions.value = await getTrainerSessions()
        trainerSessions.value.sessions = trainerSessions.value.sessions.filter((session: TrainerSession) => !session.cancelled)
        console.log(trainerSessions.value)
    }else{
        trainerSessions.value = await getTraineeSessions()
        trainerSessions.value.sessions = trainerSessions.value.sessions.filter((session: TrainerSession) => !session.cancelled)
    }
})()

const dateClicked = async (date: string) => {
    console.log("HELLOOOO")
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
