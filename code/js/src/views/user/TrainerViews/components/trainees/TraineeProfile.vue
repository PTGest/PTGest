<template>
    <div>{{ trainee.traineeName }}</div>
    <router-link :to="{name:'traineeReports', params:{traineeId:router.currentRoute.value.params.traineeId}}" class="reports-link">Reports</router-link>
</template>

<script setup lang="ts">
import router from "@/plugins/router.ts";
import {ref} from "vue";
import TraineeInfo from "@/views/user/TrainerViews/models/trainees/TraineeInfo.ts";
import Trainee from "@/views/user/CompaniesViews/models/Trainee.ts";
import getTrainerTrainees from "@/services/TrainerServices/trainees/traineesServices.js";
import TrainerTrainees from "@/views/user/TrainerViews/models/trainees/TrainerTrainees.ts";
import store from "@/store";
const trainee = ref(new Trainee());

(async () => {
    trainee.value = await getTrainerTrainees(null, null,store.getters.traineeInfo.name,null).then((trainees: TrainerTrainees) => {
        return trainees.trainees[0];
    });
    console.log("Hello from TraineeProfile.vue")
})();
</script>

<style scoped>
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
    cursor: pointer;
}

.reports-link:hover{
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-in;
}
</style>
