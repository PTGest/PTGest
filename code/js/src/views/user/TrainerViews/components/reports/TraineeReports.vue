<template>
    <h1>{{ store.getters.traineeInfo.name + " Reports" }}</h1>
    <div class="reports-container">
        <div class="no-reports" v-if="reports.total == 0">No Reports available</div>
        <div v-else>
            <font-awesome-icon class="icon" :icon="faX" @click="router.go(-1)"></font-awesome-icon>
            <div class="report-row-container" v-for="report in reports.reports" :key="report.id">
                <ReportBox v-if="(RBAC.isTrainee() && !report.visibility) || RBAC.isTrainer() || RBAC.isHiredTrainer()" :report="report"></ReportBox>
            </div>
        </div>
        <router-link v-if="RBAC.isHiredTrainer() || RBAC.isTrainer()" class="add-report" :to="{ name: 'addReport', params: { traineeId: router.currentRoute.value.params.traineeId } }"
            >Add Report</router-link
        >
    </div>
</template>

<script setup lang="ts">
import store from "../../../../../store"

import Reports from "@/views/user/TrainerViews/models/reports/Reports.ts"
import { ref } from "vue"
import ReportBox from "@/views/user/TrainerViews/components/reports/ReportBox.vue"
import RBAC from "@/services/utils/RBAC/RBAC.ts"
import { getReports } from "@/services/TrainerServices/reports/reportServices.ts"
import router from "@/plugins/router.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faX } from "@fortawesome/free-solid-svg-icons"
import { getTraineeReports } from "@/services/TraineeServices/TraineeServices.ts"

const reports = ref(new Reports())

;(async () => {
    if (RBAC.isTrainer() || RBAC.isHiredTrainer()) {
        reports.value = await getReports(router.currentRoute.value.params.traineeId, null)
        console.log(reports)
    } else {
        reports.value = await getTraineeReports(null)
    }
})()
</script>

<style scoped>
.reports-container {
    width: 30em;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    margin: 1em;
    gap: 1em;
}

.no-reports {
    align-self: center;
    padding: 1em;
    color: whitesmoke;
    font-size: 1.8em;
    font-weight: bold;
}

.add-report {
    height: 3em;
    width: 15em;
    align-self: center;
    margin-bottom: 1em;
    background-color: var(--main-secondary-color);
    color: whitesmoke;
    padding: 0.75em;
    border-radius: 5px;
    cursor: pointer;
    transition: 0.2s ease-out;
}

.add-report:hover {
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    transition: 0.2s ease-out;
}

.report-row-container {
    padding: 0.5em;
}

.icon {
    position: relative;
    left: 50%;
    color: whitesmoke;
    font-size: 1em;
    cursor: pointer;
    margin: 1em;
}
</style>
