<template>
    <div class="trainee-profile-layout">
        <div>
            <div class="trainee-profile">{{ trainee.traineeName }}</div>
            <div class="add-data-container">
                <TraineeNormalData @normalData="handleNormalData($event)" :data="new TraineeNormalDataModel(trainee.traineeName,null, null, gender, null)"/>
                <div v-if="isSkinFold == 'Skin Fold'" class="skin-fold-container">
                    Skin Fold
                    <SiteSkinFold3 v-if="skinFoldType == '3 Site' " :gender="gender"></SiteSkinFold3>
                    <SiteSkinFold4 v-else-if="skinFoldType == '4 Site'"></SiteSkinFold4>
                    <SiteSkinFold7 @skinFoldData="handleSkinFold($event)" v-else></SiteSkinFold7>
                    <SelectButton class="skin-fold-button" v-model="skinFoldType" :options="skinFoldTypeOptions" aria-labelledby="basic" />
                </div>
                <div v-else>

                    <FloatLabel>
                        <InputText @change="handleBodyFat" id="bodyFatPercentage" v-model="bodyFatPercentage" />
                        %
                        <label for="pectoral">BodyFatPercentage</label>
                    </FloatLabel>
                </div>
                <SelectButton class="skin-fold-button" v-model="isSkinFold" :options="options" aria-labelledby="basic" />
                <button class="p-button" @click="addData">Add Data</button>
            </div>
        </div>
        <router-link :to="{name:'traineeReports', params:{traineeId:router.currentRoute.value.params.traineeId}}" class="reports-link">Reports</router-link>
    </div>

</template>

<script setup lang="ts">
import router from "@/plugins/router.ts";
import {ref} from "vue";
import Trainee from "@/views/user/CompaniesViews/models/Trainee.ts";
import getTrainerTrainees from "@/services/TrainerServices/trainees/traineesServices.js";
import TrainerTrainees from "@/views/user/TrainerViews/models/trainees/TrainerTrainees.ts";
import store from "@/store";
import SelectButton from "primevue/selectbutton";
import SiteSkinFold3 from "@/views/user/TrainerViews/components/trainees/SiteSkinFold3.vue";
import SiteSkinFold4 from "@/views/user/TrainerViews/components/trainees/SiteSkinFold4.vue";
import SiteSkinFold7 from "@/views/user/TrainerViews/components/trainees/SiteSkinFold7.vue";
import TraineeNormalData from "@/views/user/TrainerViews/components/trainees/TraineeNormalData.vue";
import TraineeNormalDataModel from "@/views/user/TrainerViews/models/traineeData/TraineeNormalData.ts";
import FloatLabel from "primevue/floatlabel";
import InputText from "primevue/inputtext";


const bodyFatPercentage = ref(0);
const gender = ref('');
const trainee = ref(new Trainee());
const skinFoldMap = ref(new Map<string, number>());
const isSkinFold = ref('Skin Fold');
const skinFoldType = ref('3 Site');
const options = ['Skin Fold', 'Body Fat Percentage'];
const skinFoldTypeOptions = ['3 Site', '4 Site', '7 Site'];
const normalData = ref(new TraineeNormalDataModel());
(async () => {
    trainee.value = await getTrainerTrainees(null, null,store.getters.traineeInfo.name,null).then((trainees: TrainerTrainees) => {
        return trainees.trainees[0];
    });
    gender.value = trainee.value.gender
    console.log("Hello from TraineeProfile.vue")
})();


const handleSkinFold = ((skinFolds: Map<string,number>) => {
    skinFoldMap.value = skinFolds;
    console.log(skinFolds)
})

const handleNormalData = ((normalDataValues: TraineeNormalDataModel) => {
    normalData.value = normalDataValues;
    gender.value = normalData.value.gender;
    console.log(normalData)
})

const handleBodyFat = (e: any) => {
    if(e.target.value > 100){
        e.target.value = 100;
    }
    bodyFatPercentage.value = e.target.value;
    console.log(bodyFatPercentage.value)
}

</script>

<style scoped>
.trainee-profile-layout{
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
.add-data-container{
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1.8em;
    background-color: var(--main-primary-color);
    padding: 2em;
    border-radius: 10px;
}

::placeholder{
    color: var(--main-tertiary-color);
    font-size: 1.2em;
    font-family: 'Poppins', sans-serif;
}

:global(.p-button){
    color: whitesmoke;
    background-color: var(--main-secondary-color);
    border: none;
}
:global(.p-highlight){
    color: var(--main-secondary-color);
}
:deep(.p-inputtext){
    color: whitesmoke;
    background-color: var(--main-secondary-color);
    border: none;
}
:global(.p-inputtext:focus){
    outline: none;
}
input{
    padding: 0.5em;
    border-radius: 10px;
    border: 1px solid var(--main-tertiary-color);
    font-size: 1em;
    font-family: 'Poppins', sans-serif;
}
label{
    color: whitesmoke;
}
</style>
