<template>
    <div>
        <h1>Sessions</h1>
        <div class="sessions-container">
            <Calendar @get-date="dateClicked" :train-days="trainerSessions.sessions"></Calendar>
            <div class="session-info-container">
               <div v-for="session in daySessions" class="teste">
                     <p>{{session.traineeName}}</p>
                     <p>{{ session.beginDate }}</p>
                     <p>{{ session.endDate }}</p>
                     <p>{{ session.type }}</p>
               </div>
<!--                <button class="btn">Add Session</button>-->
                <div class="no-sessions" v-if="trainerSessions.total==0">No Sessions Today</div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import Calendar from "../../../components/calendar/Calendar.vue";

import TrainerSession from "@/views/user/TrainerViews/models/sessions/TrainerSession.js";
import {Ref, ref} from "vue";
import formattedDate from "@/components/utils/formatDate.js";
import TrainerSessions from "@/views/user/TrainerViews/models/sessions/TrainerSessions.ts";
import sessions from "@/views/user/TrainerViews/models/sessions/Sessions.ts";
import getTrainerSessions from "@/services/TrainerServices/sessions/getTrainerSessions.ts";

const selectedDay : Ref<string> = ref(formattedDate(new Date( new Date().getFullYear(), new Date().getMonth(), new Date().getDate())))
const daySessions: Ref<TrainerSession[]> = ref([]);

const trainerSessions : Ref<TrainerSessions> = ref({
    sessions: [],
    total: 0
});

(async () => {
    trainerSessions.value.sessions =  await getTrainerSessions(null);
    console.log(sessions.value);

})();

console.log(selectedDay.value)

const dateClicked = (date: string) => {
    daySessions.value = [];
    selectedDay.value = date.trim(); // Trim any extra spaces
    //filter Sessions by date

    // trainerSessions.value.sessions.forEach((session: TrainerSession) => {
    //     if (session.beginDate.trim() === selectedDay.value.trim()) { // Trim the session date too
    //         daySessions.value.push(session);
    //     }
    // });
    console.log(daySessions.value);
}
</script>



<style scoped>
.sessions-container{
    height: 100%;
    display:flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin: 1em;
    gap : 1em;
}
.session-info-container{
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
}

.teste{
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding:1em;
    margin-top: 1em;
    font-size: 0.9em;
    width: 95%;
    height: 15%;
    background-color: var(--main-secondary-color);
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}


.btn{
    margin-top: 1em;
    background-color: var(--main-secondary-color);
    color: whitesmoke;
}

.btn:hover{
    border: 1px solid var(--button-border-color);
}
.no-sessions{
    margin-top: 1em;
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: 500;
}
</style>
