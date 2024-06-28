<template>
    <div @click="openReport" class="report-info">
       Report {{ props.report.id }}
        <font-awesome-icon v-if="report.visibility" :icon="faLock"/>
    </div>
</template>

<script setup lang="ts">
import Report from "@/views/user/TrainerViews/models/reports/Report.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faLock} from "@fortawesome/free-solid-svg-icons";
import router from "@/plugins/router.ts";


const props = defineProps<{
    report: Report
}>();


const openReport = () => {
    router.push({name: 'report', params: {reportId: props.report.id, traineeId: props.report.traineeId}});
}

</script>



<style scoped>
.report-info{
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 25em;
    padding: 0.5em 1em 0.5em 1em;
    background-color: var(--main-secondary-color);
    border-radius: 10px;
}

.report-info:hover{
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    border-radius: 10px;
    transition: 0.2s ease-out;
    cursor: pointer;

}
</style>
