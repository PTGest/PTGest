<template>
    <div class="session-info-container">
        <div v-bind="session" @click="sessionDetails(session.id)" v-for="session in daySessionsFiltered" class="session-row-container">
            <p>{{ store.getters.traineeInfo.name }}</p>
            <p>{{ dateFormatter(session.beginDate) }}</p>
            <p v-if="session.endDate != null">{{ dateFormatter(session.endDate) }}</p>
            <p>{{ session.type }}</p>
        </div>
        <div class="no-sessions" v-if="daySessionsFiltered.length == 0">No Sessions Today</div>
        <router-link v-if="isTraineeSessions" :to="{ name: 'addTraineeSessions', params: { traineeId: $route.params.traineeId } }" class="btn">Add Session</router-link>
    </div>
</template>

<script setup lang="ts">

import dateFormatter from "../../../../../services/utils/dateUtils/dateFormatter.ts"
import store from "../../../../../store"
import { getDayFromDate, getMonthFromDate } from "@/services/utils/dateUtils/getFromDateUtils.ts"
import { Ref, ref } from "vue"
import TrainerSession from "@/views/user/trainerViews/models/sessions/TrainerSession.ts";
import router from "@/plugins/router.ts";
import Session from "@/views/user/trainerViews/models/sessions/Session.ts";

const props = defineProps<{
    daySessions: TrainerSession[] | Session[]
    isTraineeSessions: boolean
}>()

const daySessionsFiltered: Ref<TrainerSession[] | Session[]> = ref(
    props.daySessions.filter(
        (session: TrainerSession) => getDayFromDate(session.beginDate) >= new Date().getDay() && getMonthFromDate(session.beginDate) >= new Date().getMonth() && !session.cancelled
    )
)

const sessionDetails = (id: number) => {
    router.push("/sessions/session/" + id)
}
</script>

<style scoped>
.session-info-container {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    width: 25em;
    height: 30em;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
    overflow-y: scroll;
}

.btn,
.btn:hover {
    padding: 0.5em;
    margin-top: 1em;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    color: whitesmoke;
}

.btn:hover {
    border: 1px solid var(--button-border-color);
}

.no-sessions {
    margin-top: 1em;
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: 500;
}

.session-row-container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    margin-top: 0.5em;
    margin-left: 0.5em;
    padding: 0.5em;
    background-color: var(--main-secondary-color);
    border-radius: 10px;
    color: whitesmoke;
    font-size: 1em;
    font-weight: 500;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    gap: 1em;
    transition: background-color 0.3s;
}

.session-row-container:hover {
    cursor: pointer;
    background-color: var(--main-tertiary-color);
    transition: 0.2s ease-out;
}

p {
    font-size: 0.8em;
}
</style>
