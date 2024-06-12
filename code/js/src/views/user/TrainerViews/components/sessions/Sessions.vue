<template>
    <div>
        <h1>Sessions</h1>
        <div class="sessions-container">
            <Calendar @get-date="dateClicked($event)"></Calendar>
            <div class="session-info-container">
               <div v-for="session in daySessions" class="teste">
                     <p>{{session.traineeName}}</p>
                     <p>{{ session.beginDate }}</p>
                     <p>{{ session.endDate }}</p>
                     <p>{{ session.type }}</p>
               </div>
                <button class="btn">Add Session</button>
            </div>

        </div>



    </div>
</template>

<script setup lang="ts">
import Calendar from "../../../../../components/calendar/Calendar.vue";

import TrainerSession from "@/views/user/TrainerViews/models/sessions/TrainerSessions.ts";
import {Ref, ref} from "vue";
import formattedDate from "@/components/utils/formatDate.ts";
const selectedDay : Ref<string> = ref(formattedDate(new Date( new Date().getFullYear(), new Date().getMonth(), new Date().getDate())))
const daySessions: Ref<TrainerSession[]> = ref([]);

const sessions : Ref<TrainerSession[]> = ref([
    new TrainerSession("1", "Manel", "2024-6-12", "10:00", "BODYWEIGHT",false),
    new TrainerSession("2", "Joca", "2024-6-12", "10:00", "BODYWEIGHT",false),
    new TrainerSession("3", "Manel", "2024-6-12", "10:00", "BODYWEIGHT",false),
    new TrainerSession("4", "Manel", "2024-6-13", "10:00", "BODYWEIGHT",false),
    new TrainerSession("5", "Manel", "2024-6-13", "10:00", "BODYWEIGHT",false),
    new TrainerSession("6", "Manel", "2024-6-13", "10:00", "BODYWEIGHT",false),
    new TrainerSession("7", "Manel", "2024-6-14", "10:00", "BODYWEIGHT",false),
    new TrainerSession("8", "Manel", "2024-6-14", "10:00", "BODYWEIGHT",false),
    new TrainerSession("9", "Manel", "2024-6-14", "10:00", "BODYWEIGHT",false),
]);



(function(){
    for (const element of sessions.value) {
        if (element.beginDate.trim() == selectedDay.value.trim()) { // Trim the session date too
            daySessions.value.push(element)
        }
    }
})()

console.log(selectedDay.value)

const dateClicked = (date: string) => {
    daySessions.value = []
    selectedDay.value = date.trim(); // Trim any extra spaces
    for (const element of sessions.value) {
        if (element.beginDate.trim() == selectedDay.value.trim()) { // Trim the session date too
            daySessions.value.push(element)
        }
    }
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

</style>
