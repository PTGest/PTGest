<template>
    <div class="report-container">
        <font-awesome-icon @click="closeReport" class="x-icon" :icon="faX"/>
        <h1 v-if="RBAC.isTrainer() || RBAC.isHiredTrainer()">{{report.trainee + ' Report'}}</h1>
        <div>{{report.date}}</div>
        <font-awesome-icon v-if="report.visibility" :icon="faLock"/>
        <textarea v-model="report.report" readonly></textarea>
    </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import getReportDetails from "@/services/TrainerServices/reports/getReportDetails.ts";
import router from "@/plugins/router.ts";
import ReportDetails from "@/views/user/TrainerViews/models/reports/ReportDetails.ts";
import RBAC from "@/services/utils/RBAC/RBAC.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faLock, faX} from "@fortawesome/free-solid-svg-icons";
const report = ref(new ReportDetails());

(async () => {
    report.value = await getReportDetails(router.currentRoute.value.params.reportId);
})();

const closeReport = () => {
    router.go(-1);
}

</script>

<style scoped>
.report-container{
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

textarea{
    width: 98%;
    height: 30em;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    padding: 1em;
    font-size: 1.2em;
    color: whitesmoke;
    border: none;
    resize: none;
}

h1{
    font-size: 2em;
}

.x-icon{
    align-self: flex-end;
    cursor: pointer;
}
</style>
