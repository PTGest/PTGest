<template>
    <div class="report-container">
        <font-awesome-icon @click="closeReport" class="x-icon" :icon="faX" />
        <h1 v-if="RBAC.isTrainer() || RBAC.isHiredTrainer()">{{ report.trainee + " Report" }}</h1>
        <div>{{ report.date }}</div>
        <font-awesome-icon v-if="report.visibility" :icon="faLock" />
        <div class="textarea-container">
            <font-awesome-icon @click="enableEdit" v-if="!isEdit && (RBAC.isTrainer() || RBAC.isHiredTrainer())" class="edit-icon" :icon="faPen" />
            <font-awesome-icon @click="handleEditReport" v-else-if="RBAC.isHiredTrainer() || RBAC.isTrainer()" class="edit-icon" :icon="faCheck" />
            <textarea v-model="report.report" :readonly="!isEdit"></textarea>
            <SelectButton v-if="isEdit" class="privacy-button" v-model="visibility" :options="options" aria-labelledby="basic" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import router from "@/plugins/router.ts"
import ReportDetails from "@/views/user/TrainerViews/models/reports/ReportDetails.ts"
import RBAC from "@/services/utils/RBAC/RBAC.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faCheck, faLock, faPen, faX } from "@fortawesome/free-solid-svg-icons"
import SelectButton from "primevue/selectbutton"
import EditReportRequest from "@/views/user/TrainerViews/models/reports/EditReportRequest.ts"
import { editReport, getReportDetails } from "@/services/TrainerServices/reports/reportServices.ts"
import { getTraineeReportDetails } from "@/services/TraineeServices/TraineeServices.ts"

const report = ref(new ReportDetails())
const isEdit = ref(false)
const visibility = ref("")
const options = ["Public", "Private"]
;(async () => {
    if (RBAC.isTrainer() || RBAC.isHiredTrainer()) {
        report.value = await getReportDetails(router.currentRoute.value.params.traineeId, router.currentRoute.value.params.reportId)
        visibility.value = report.value.visibility ? "Private" : "Public"
    } else {
        report.value = await getTraineeReportDetails(router.currentRoute.value.params.reportId)
        visibility.value = report.value.visibility ? "Private" : "Public"
    }
})()

const enableEdit = () => {
    isEdit.value = !isEdit.value
}

const handleEditReport = async () => {
    try {
        await editReport(router.currentRoute.value.params.reportId, new EditReportRequest(router.currentRoute.value.params.traineeId, report.value.report, visibility.value === "Private"))
        isEdit.value = false
    } catch (e) {
        console.log(e)
    }
}

const closeReport = () => {
    router.go(-1)
}
</script>

<style scoped>
.report-container {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    gap: 1em;
    padding: 1em;
    background-color: var(--main-secondary-color);
    border-radius: 10px;
    width: 30em;
    margin: 1em;
}

textarea {
    width: 98%;
    height: 30em;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    padding: 1em;
    font-size: 1.2em;
    color: whitesmoke;
    border: none;
    resize: none;
    outline: none;
}

h1 {
    font-size: 2em;
}

.x-icon {
    align-self: flex-end;
    cursor: pointer;
}

.textarea-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1em;
}
.edit-icon {
    align-self: flex-end;
    padding: 0 1em 0 0;
    cursor: pointer;
}

:deep(.p-component) {
    background-color: var(--main-primary-color);
    border-radius: 10%;
    border: none;
}

:deep(.p-selectbutton) {
    color: whitesmoke;
}
:global(.p-button) {
    color: whitesmoke;
    background-color: var(--main-secondary-color);
    border: none;
}
:global(.p-highlight) {
    color: var(--main-secondary-color);
}
</style>
