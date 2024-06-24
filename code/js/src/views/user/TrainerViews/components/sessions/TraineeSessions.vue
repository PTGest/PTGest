<template>
    <h1>Trainee Sessions</h1>
    <div class="sessions-container">
        <Calendar :train-days="traineeTrainDays.sessions.map((session:Session) => session.beginDate)"></Calendar>
        <SessionInfoContainer :day-sessions="traineeTrainDays.sessions" is-trainee-sessions/>
    </div>
</template>

<script setup lang="ts">
import Calendar from "@/components/calendar/Calendar.vue"
import getTraineeSessions from "@/services/TrainerServices/sessions/getTraineeSessions.ts";
import router from "@/plugins/router.ts";
import {Ref, ref} from "vue";
import Sessions from "@/views/user/TrainerViews/models/sessions/Sessions.ts";
import Session from "@/views/user/TrainerViews/models/sessions/Session.ts";
import SessionInfoContainer from "@/views/user/TrainerViews/components/sessions/SessionInfoContainer.vue";
import {getUserInfo} from "@/services/UserServices/profileServices.ts";

const traineeTrainDays : Ref<Sessions> = ref (new Sessions());

( async () => {
    traineeTrainDays.value = await getTraineeSessions(router.currentRoute.value.params.traineeId)
})();
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
</style>
