<template>
    <div class="trainee-profile-layout">
        <UserProfile :user-id="router.currentRoute.value.params.traineeId"></UserProfile>
        <div class="trainer-options" v-if="RBAC.isHiredTrainer() || RBAC.isTrainer() || RBAC.isTrainee()">
            <router-link :to="{name:'traineeReports', params:{traineeId:router.currentRoute.value.params.traineeId}}" class="reports-link">
                Reports
                <font-awesome-icon :icon="faBookOpen" class="icon"/>
            </router-link>
            <router-link :to="{name: 'dataHistory', params:{traineeId: router.currentRoute.value.params.traineeId}}" class="reports-link">
                Data History
                <font-awesome-icon :icon="faFileWaveform" class="icon"/>
            </router-link>
        </div>
    </div>
</template>

<script setup lang="ts">
import router from "@/plugins/router.ts";
import UserProfile from "@/views/user/UserProfile/UserProfile.vue";
import RBAC from "@/services/utils/RBAC/RBAC.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faBookOpen, faFileWaveform} from "@fortawesome/free-solid-svg-icons";
import store from "@/store";

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

.trainer-options{
    display: flex;
    flex-direction: column;
    gap: 1em;
    align-items: center;
    justify-content: center;
    margin-top: 2em;
    margin-bottom: 2em;
    padding: 1em;
    border-radius: 10px;
    background-color: var(--main-primary-color);
    width: 100%;
}
.icon{
    margin-left: 0.5em;
}
</style>
