<template>
    <h1>{{store.getters.traineeInfo.name + " Reports"}}</h1>
    <div class="reports-container">
       <div class="no-reports" v-if="reports.total == 0">
           No Reports available
       </div>
         <div v-else>
              <div class="report-row-container" v-for="report in reports.reports" :key="report.id">
                <ReportBox v-if="(RBAC.isTrainee() && !report.visibility)|| RBAC.isTrainer() || RBAC.isHiredTrainer()" :report="report"></ReportBox>
              </div>
         </div>
        <router-link class="add-report" :to="{name:'addReport', params:{traineeId:store.getters.traineeInfo.id}}">Add Report</router-link>
    </div>
</template>

<script setup lang="ts">
import store from "../../../../../store";
import getReports from "@/services/TrainerServices/reports/getReports.ts";
import Reports from "@/views/user/TrainerViews/models/reports/Reports.ts";
import {ref} from "vue";
import ReportBox from "@/views/user/TrainerViews/components/reports/ReportBox.vue";
import RBAC from "@/services/utils/RBAC/RBAC.ts";

const reports = ref(new Reports());

(async () => {
    const filters = new Map<string, string>();
    filters.set("traineeId", store.getters.traineeInfo.id);
    filters.set("traineeName", store.getters.traineeInfo.name);
   reports.value = await getReports(filters);
    console.log(reports);
})();


</script>



<style scoped>
.reports-container{
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

.no-reports{
    align-self: center;
    padding: 1em;
    color: whitesmoke;
    font-size: 1.8em;
    font-weight: bold;
}

.add-report{
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

.add-report:hover{
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    transition: 0.2s ease-out;
}

.report-row-container{
    padding: 0.5em;
}



</style>
