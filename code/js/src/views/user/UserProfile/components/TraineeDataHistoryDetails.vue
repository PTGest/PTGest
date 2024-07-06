<template>
    <div class="data-details">
        <h1>
            {{store.getters.traineeInfo.name + ' Body Data' + ` #${router.currentRoute.value.params.dataId}`}}
            <font-awesome-icon :icon="faX" class="icon" @click="router.back()"/>
        </h1>
        {{dateFormatter(dataDetails.date.toString())}}
        <BodyData :body-data="dataDetails.bodyData"></BodyData>
    </div>
</template>

<script setup lang="ts">
import {getTraineeDataDetails} from "@/services/TrainerServices/traineeDataServices/traineeDataServices.ts";
import router from "@/plugins/router.ts";
import TraineeDataDetails from "@/views/user/TrainerViews/models/traineeData/TraineeDataDetails.ts";
import {ref} from "vue";
import BodyData from "@/views/user/UserProfile/components/BodyData.vue";
import store from "../../../../store";
import dateFormatter from "../../../../services/utils/dateUtils/dateFormatter.ts";
import {faX} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import RBAC from "@/services/utils/RBAC/RBAC.ts";
import {getTTraineeDataDetails} from "@/services/TraineeServices/TraineeServices.ts";

const dataDetails = ref(new TraineeDataDetails());

(async() => {
    if(RBAC.isHiredTrainer()|| RBAC.isTrainer()){
        dataDetails.value = await getTraineeDataDetails(router.currentRoute.value.params.traineeId, router.currentRoute.value.params.dataId);
    }else{
        dataDetails.value = await getTTraineeDataDetails(router.currentRoute.value.params.dataId);
    }
})();
</script>

<style scoped>
.data-details{
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

h1{
    position: relative;
    color: whitesmoke;
    font-size: 2em;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;

}

.icon{
    position: absolute;
    right: 0.2em;
    top: -1em;
    color: whitesmoke;
    cursor: pointer;
    font-size: 0.6em;
}
</style>
