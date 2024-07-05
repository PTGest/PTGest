<template>
    <div class="trainee-profile-layout">
        <UserProfile :user-id="router.currentRoute.value.params.traineeId"></UserProfile>
        <router-link :to="{name:'traineeReports', params:{traineeId:router.currentRoute.value.params.traineeId}}" class="reports-link">Reports</router-link>
        <AddTraineeData v-if="isAddDataOpen"></AddTraineeData>
    </div>
</template>

<script setup lang="ts">
import AddTraineeData from "@/views/user/TrainerViews/components/trainees/AddTraineeData.vue";
import router from "@/plugins/router.ts";
import {
    getTraineeData,
} from "@/services/TrainerServices/traineeDataServices/traineeDataServices.ts";
import {ref} from "vue";
import TraineeDataHistory from "@/views/user/TrainerViews/models/trainees/TraineeDataHistory.ts";
import UserProfile from "@/views/user/UserProfile/UserProfile.vue";

const traineeData = ref(new TraineeDataHistory());
const isAddDataOpen = ref(false);

(async () => {
    traineeData.value = await getTraineeData(router.currentRoute.value.params.traineeId);
    console.log("Hello from TraineeProfile.vue")
})();

</script>

<style scoped>
.trainee-profile-layout{
    position: relative;
    left: -10em;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 2em;
    margin-top: 2em;
    margin-bottom: 2em;
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
    cursor: pointer;
}

.reports-link:hover{
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-in;
}

</style>
