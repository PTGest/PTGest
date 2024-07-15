<template>
    <div class="set-details-container">
        <h1>Set Details</h1>
        <div class="set-details">
            <div class="exercise-details" v-for="exercise in setDetails.setExerciseDetails">
                <SetDetailsExerciseDetails :exercise="exercise"></SetDetailsExerciseDetails>
            </div>
        </div>
        <font-awesome-icon class="x-icon" :icon="faX" @click="$router.go(-1)" />
    </div>
</template>

<script setup lang="ts">
import { getSetDetails } from "@/services/TrainerServices/sets/setServices.ts"
import { ref } from "vue"
import SetDetails from "@/views/user/TrainerViews/models/sets/SetDetails.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faX } from "@fortawesome/free-solid-svg-icons"
import SetDetailsExerciseDetails from "@/views/user/TrainerViews/components/sets/SetDetailsExerciseDetails.vue"
import router from "@/plugins/router.ts"

const setDetails = ref(new SetDetails())

;(async () => {
    setDetails.value = await getSetDetails(router.currentRoute.value.params.setId)
})()
</script>

<style scoped>
.set-details-container {
    position: absolute;
    top: 25%;
    left: 25%;
    background-color: var(--main-primary-color);
    padding: 1em;
    border-radius: 10px;
    z-index: 100;
}

.set-details-container {
    animation: p-component-overlay-enter-animation 0.4s;
}

@keyframes p-component-overlay-enter-animation {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}

.x-icon {
    position: absolute;
    top: 2em;
    right: 2em;
    cursor: pointer;
}

.set-details {
    display: flex;
    flex-direction: row;
    gap: 1em;
    justify-content: space-between;
    align-items: center;
}
.exercise-details {
    width: 100%;
}
</style>
