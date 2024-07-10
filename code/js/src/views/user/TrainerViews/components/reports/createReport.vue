<template>
    <h1>Add Report</h1>
    <div class="add-report-container">
        <textarea class="report-input" v-model="reportRequestData.report" placeholder="Report"></textarea>
        <SelectButton class="privacy-button" v-model="isPrivate" :options="options" aria-labelledby="basic" />
        <button class="add-button" @click="addReport">Add Report</button>
    </div>
</template>

<script setup lang="ts">
import SelectButton from "primevue/selectbutton"
import CreateReportRequest from "@/views/user/TrainerViews/models/reports/CreateReportRequest.ts"
import { ref } from "vue"
import { createReport } from "@/services/TrainerServices/reports/reportServices.js"
import router from "@/plugins/router.ts"

const isPrivate = ref("Public")
const options = ["Public", "Private"]
const reportRequestData = ref(new CreateReportRequest())

const addReport = async () => {
    try {
        reportRequestData.value.visibility = isPrivate.value !== "Public"
        reportRequestData.value.traineeId = router.currentRoute.value.params.traineeId
        await createReport(reportRequestData.value)
        router.go(-1)
    } catch (e) {
        console.log(e)
    }
}
</script>

<style scoped>
.add-report-container {
    display: flex;
    width: 40em;
    height: 40em;
    flex-direction: column;
    justify-content: space-between;
    align-items: start;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    margin: 1em;
}

h1 {
    font-size: 2em;
}

.report-input {
    margin-top: 1em;
    padding: 2em;
    align-self: center;
    width: 90%;
    height: 80%;
}
textarea {
    resize: none;
    font-size: 1.5em;
    border-radius: 10px;
    border: none;
    background-color: var(--main-secondary-color);
    color: whitesmoke;
    font-family: "Poppins", sans-serif;
}

.report-input:focus {
    outline: none;
    border: 1px solid var(--button-border-color);
}

::placeholder {
    color: var(--main-tertiary-color);
    font-size: 1.2em;
    font-family: "Poppins", sans-serif;
}

.privacy-button {
    align-self: center;
    padding: 1em;
    margin-bottom: 1em;
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

.add-button {
    width: 20em;
    margin-bottom: 1em;
    height: 3em;
    align-self: center;
    padding: 0.8em;
    color: whitesmoke;
    background-color: var(--main-secondary-color);
}
</style>
