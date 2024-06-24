<template>
    <div @click="handleInfo" class="trainer">
        <div class="text">
            <h4>{{props.trainee.traineeName}}</h4>
        </div>
        <div class="text">
            <p>Gender</p>
            <div class="gender-text">{{props.trainee.gender}}</div>
        </div>

        <div class="text">
            <p>Trainer</p>
            <div class="gender-text">{{props.trainee.trainerName}}</div>
        </div>

        <router-link v-if="RBAC.isCompany()" :to="{ name: 'assignTrainer', params: {traineeId: trainee.traineeId, assignTrainer : 'reassignTrainer'}}">
            <button class="reassign-btn">Reassign Trainer</button>
        </router-link>
    </div>
</template>

<script setup lang="ts">

import Trainee from "../../../CompaniesViews/models/Trainee.ts";
import RBAC from "@/services/utils/RBAC/RBAC.js";
import router from "@/plugins/router.ts";
import store from "@/store";
import TraineeInfo from "@/views/user/TrainerViews/models/trainees/TraineeInfo.ts";

;

const props = defineProps<{
    trainee: Trainee
}>()

const handleInfo = () => {
    if(RBAC.isCompany()){
        // router.push({name: 'traineeInfo', params: {traineeId: props.trainee.traineeId}})
    }else{
        console.log(props.trainee.traineeId)
        const traineeInfo = new TraineeInfo(props.trainee.traineeId, props.trainee.traineeName);
        store.commit('setTraineeInfo', traineeInfo);
        router.push({name: 'traineeSessions', params: {traineeId: props.trainee.traineeId}})
    }
}


</script>

<style scoped>
.trainer{
    width: 20em;
    height: 4em;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 1em 1em 1em 1em;
    border-radius: 10px;
    margin: 0.5em;
    background-color: var(--main-primary-color);
    border : 1px solid var(--main-secondary-color);
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: bold;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
.icon{
    position: relative;
    top: 0.8em;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}
p, h4{
    padding: 0;
    margin: 0;
}

h4{
    text-shadow: var(--main-secondary-color) 2px 2px 2px;
}
p{
    font-size: 0.8em;
}
.text{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 0 0.5em 0 0.5em;
}


.delete-icon{
    position: relative;
    top: -1.5em;
    right: -0.5em;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}
.gender-text{
    font-size: 0.6em;
}

.reassign-btn{
    margin-left:1em;
    font-size: 0.6em;
    border-radius: 10px;
    color: var(--main-primary-color);
    background-color: whitesmoke;
    cursor: pointer;
    transition: 0.2s ease-in;
}
.reassign-btn:hover{
    color: whitesmoke;
    background-color: var(--main-primary-color);
    transition: 0.2s ease-out;
}

</style>

