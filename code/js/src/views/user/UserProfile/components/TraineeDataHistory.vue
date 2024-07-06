<template>
    <div>
        <h1>Trainee Data History</h1>
        <div class="data-history-container">
            <font-awesome-icon :icon="faX" class="icon" @click="router.back()"/>
            <router-link v-if="traineeData.totalData != 0" :to="{name:'dataHistoryDetails',
            params:{traineeId:router.currentRoute.value.params.traineeId,dataId:data.id}}"
                         class="data-row" v-for="data in traineeData.traineeData">
                {{store.getters.traineeInfo.name + ' Data ' + data.id}}
            </router-link>
            <div v-else class="no-data">No Data Available</div>
            <router-link v-if="RBAC.isHiredTrainer()|| RBAC.isTrainer() " :to="{name: 'addDataHistory', params:{traineeId:router.currentRoute.value.params.traineeId}}"
                         class="btn" >Add Data</router-link>
        </div>
    </div>
</template>

<script setup lang="ts">

import {ref} from "vue";
import {getTraineeData} from "@/services/TrainerServices/traineeDataServices/traineeDataServices.ts";
import router from "@/plugins/router.ts";
import TraineeDataHistory from "@/views/user/TrainerViews/models/trainees/TraineeDataHistory.ts";
import store from "../../../../store";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faX} from "@fortawesome/free-solid-svg-icons";
import RBAC from "@/services/utils/RBAC/RBAC.ts";
import {getTTraineeData} from "@/services/TraineeServices/TraineeServices.ts";

const traineeData = ref(new TraineeDataHistory());

(async () => {

    if(RBAC.isTrainer() || RBAC.isHiredTrainer()){
        traineeData.value = await getTraineeData(router.currentRoute.value.params.traineeId);

    }else{
        traineeData.value = await getTTraineeData();
    }



})();

</script>

<style scoped>
.data-history-container{
    position: relative;
    display: flex;
    flex-direction: column;
    gap: 1em;
    margin-top: 2em;
    margin-bottom: 2em;
    padding: 1em;
    border-radius: 10px;
    background-color: var(--main-primary-color);
    width: 100%;
    align-items: center;
}

.data-row{
    display: flex;
    flex-direction: row;
    gap: 1em;
    margin-top: 1.5em;
    padding: 1em;
    border-radius: 10px;
    background-color: var(--main-secondary-color);
    color: whitesmoke;
    width: 100%;
    justify-content: space-between;
    transition: 0.2s ease-in;
}

.data-row:hover{
    cursor: pointer;
    background-color: var(--main-tertiary-color);
    transition: 0.2s ease-out;
}

.btn{
    color: whitesmoke;
    padding: 0.5em 1em 0.5em 1em;
    border-radius: 10px;
    background-color: var(--button-border-color);
    transition: 0.2s ease-in;
}
.btn:hover{
    border: 1px solid var(--button-border-color);
    background-color: var(--main-primary-color);
    transition: 0.2s ease-out;
}

.icon{
    position: absolute;
    right: 1em;
    top: 0.5em;
    color: whitesmoke;
    cursor: pointer;
    font-size: 1em;
}

.no-data{
    padding: 0.5em;
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: bold;
    align-self: center;
}
</style>
