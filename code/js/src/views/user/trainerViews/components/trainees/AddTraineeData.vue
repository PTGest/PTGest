<template>
    <div class="ctner">
        <div class="trainee-profile">{{ trainee.traineeName }}</div>
        <div class="add-data-container">
            <font-awesome-icon :icon="faX" @click="router.back()" class="icon" />
            <TraineeNormalData @normalData="handleNormalData($event)" :data="new TraineeNormalDataModel(trainee.traineeName, null, null, gender, null)" />
            <div v-if="isSkinFold == 'Skin Fold'" class="skin-fold-container">
                <h2>Skin Folds</h2>
                <SiteSkinFold3 @skinFoldData="handleSkinFold($event)" v-if="skinFoldType == '3 Site'" :gender="gender"></SiteSkinFold3>
                <SiteSkinFold4 @skinFoldData="handleSkinFold($event)" v-else-if="skinFoldType == '4 Site'"></SiteSkinFold4>
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
            <Button class="add-btn" @click="addData">Add Data</Button>
        </div>
    </div>
</template>

<script setup lang="ts">
import Button from "primevue/button"
import { computed, ref } from "vue"
import Trainee from "@/views/user/companiesViews/models/Trainee.ts"
import getTrainerTrainees from "@/services/trainerServices/trainees/traineesServices.ts"
import TrainerTrainees from "@/views/user/trainerViews/models/trainees/TrainerTrainees.ts"
import store from "@/store"
import SelectButton from "primevue/selectbutton"
import SiteSkinFold3 from "@/views/user/trainerViews/components/trainees/SiteSkinFold3.vue"
import SiteSkinFold4 from "@/views/user/trainerViews/components/trainees/SiteSkinFold4.vue"
import SiteSkinFold7 from "@/views/user/trainerViews/components/trainees/SiteSkinFold7.vue"
import TraineeNormalData from "@/views/user/trainerViews/components/trainees/TraineeNormalData.vue"
import TraineeNormalDataModel from "@/views/user/trainerViews/models/traineeData/TraineeNormalData.ts"
import FloatLabel from "primevue/floatlabel"
import InputText from "primevue/inputtext"
import AddTraineeDataRequest from "@/views/user/trainerViews/models/traineeData/AddTraineeDataRequest.ts"
import { addTraineeData } from "@/services/trainerServices/traineeDataServices/traineeDataServices.ts"
import BodyCircumferences from "@/views/user/trainerViews/models/traineeData/BodyCircumferences.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import router from "@/plugins/router.ts"
import { faX } from "@fortawesome/free-solid-svg-icons"

const bodyFatPercentage = ref(0)
const gender = ref("")
const trainee = ref(new Trainee())
const skinFoldMap = ref(new Map<string, number>())
const isSkinFold = ref("Skin Fold")
const skinFoldType = ref("3 Site")
const options = ["Skin Fold", "Body Fat Percentage"]
const skinFoldTypeOptions = ["3 Site", "4 Site", "7 Site"]
const normalData = ref(new TraineeNormalDataModel())

const data = ref(new AddTraineeDataRequest())

;(async () => {
    trainee.value = await getTrainerTrainees(null, null, store.getters.traineeInfo.name, null).then((trainees: TrainerTrainees) => {
        return trainees.trainees[0]
    })
    gender.value = trainee.value.gender
})()

const handleSkinFold = (skinFolds: Map<string, number>) => {
    skinFoldMap.value = skinFolds
}

const handleNormalData = (normalDataValues: TraineeNormalDataModel) => {
    normalData.value = normalDataValues
    gender.value = normalData.value.gender
}

const handleBodyFat = (e: any) => {
    if (e.target.value > 100) {
        e.target.value = 100
    }
    bodyFatPercentage.value = e.target.value
}

const isDisable = computed(() => {
    return !(
        normalData.value.gender != null &&
        normalData.value.height != null &&
        normalData.value.weight != null &&
        normalData.value.bodyCircumferences != null &&
        ((isSkinFold.value == "Skin Fold" &&
            ((skinFoldType.value == "3 Site" && skinFoldMap.value.size == 3) ||
                (skinFoldType.value == "4 Site" && skinFoldMap.value.size == 4) ||
                (skinFoldType.value == "7 Site" && skinFoldMap.value.size == 7))) ||
            (isSkinFold.value == "Body Fat Percentage" && bodyFatPercentage.value != 0))
    )
})

const addData = async () => {
    data.value = new AddTraineeDataRequest(trainee.value.traineeId, normalData.value.gender, normalData.value.weight, normalData.value.height, normalData.value.bodyCircumferences)

    if (isSkinFold.value == "Skin Fold") {
        data.value.skinFold = skinFoldMap.value
    } else {
        data.value.bodyFatPercentage = bodyFatPercentage.value
    }
    await addTraineeData(data.value)
    router.back()
}
</script>

<style scoped>
.trainee-profile {
    font-size: 2em;
    font-weight: bold;
    color: whitesmoke;
}
.trainee-profile-layout {
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

.reports-link {
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

.reports-link:hover {
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-in;
}
.add-data-container {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1.8em;
    background-color: var(--main-primary-color);
    padding: 2em;
    border-radius: 10px;
}

::placeholder {
    color: var(--main-tertiary-color);
    font-size: 1.2em;
    font-family: "Poppins", sans-serif;
}

:global(.p-button) {
    color: whitesmoke;
    background-color: var(--main-secondary-color);
    border: none;
}
:global(.p-highlight) {
    color: var(--main-secondary-color);
}
:deep(.p-inputtext) {
    color: whitesmoke;
    background-color: var(--main-secondary-color);
    border: none;
}
:global(.p-inputtext:focus) {
    outline: none;
}
input {
    padding: 0.5em;
    border-radius: 10px;
    border: 1px solid var(--main-tertiary-color);
    font-size: 1em;
    font-family: "Poppins", sans-serif;
}
label {
    color: whitesmoke;
}

.add-btn {
    border-radius: 5px;
    border: none;
    font-size: 1em;
    font-family: "Poppins", sans-serif;
    background-color: var(--sign-up-blue);
    color: whitesmoke;
    cursor: pointer;
    transition: 0.2s ease-in;
}

.add-btn:hover {
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    transition: 0.2s ease-out;
}
.icon {
    position: absolute;
    top: 1.5em;
    right: 2em;
    cursor: pointer;
}
</style>
