<template>
    <div class="session-info-container">
        <div v-for="session in props.daySessions" class="teste">
            <p>{{session.traineeName}}</p>
            <p>{{ session.beginDate }}</p>
            <p>{{ session.endDate }}</p>
            <p>{{ session.type }}</p>
        </div>
        <div class="no-sessions" v-if="props.daySessions.length==0">No Sessions Today</div>
        <router-link v-if="isTraineeSessions" :to="{name: 'addTraineeSessions', params: {traineeId: $route.params.traineeId}}"  class="btn">Add Session</router-link>
    </div>
</template>

<script setup lang="ts">
import TrainerSession from "@/views/user/TrainerViews/models/sessions/TrainerSession.ts";
import router from "@/plugins/router.ts";

const props = defineProps<{
    daySessions: TrainerSession[]
    isTraineeSessions: boolean
}>()

const userId = router.currentRoute.value.params.traineeId;
console.log(userId)

</script>



<style scoped>
.session-info-container{
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    width: 25em;
    height: 33.5em;
    background-color: var(--main-primary-color);
    border-radius: 5px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
    overflow-y: scroll;
}

.btn,.btn:hover{
    padding:0.5em;
    margin-top: 1em;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    color: whitesmoke;
}

.btn:hover{
    border: 1px solid var(--button-border-color);
}

.no-sessions{
    margin-top: 1em;
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: 500;
}

</style>
