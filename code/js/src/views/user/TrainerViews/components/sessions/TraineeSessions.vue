<template>
    <div>
        <h1>Trainee Sessions</h1>
        <Calendar :train-days="traineeTrainDays.sessions"></Calendar>
    </div>
</template>

<script setup lang="ts">
import Calendar from "@/components/calendar/Calendar.vue"
import getTraineeSessions from "@/services/TrainerServices/sessions/getTraineeSessions.ts";
import router from "@/plugins/router.ts";
import {Ref, ref} from "vue";
import Sessions from "@/views/user/TrainerViews/models/sessions/Sessions.ts";

const traineeTrainDays : Ref<Sessions> = ref({
    traineeSessions: [],
    total: 0
});


( async () => {
    traineeTrainDays.value = await getTraineeSessions(router.currentRoute.value.params.traineeId)

})();
</script>



<style scoped>

</style>
