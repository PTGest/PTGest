<template>
    <div class="workout-details-container">
        <div class="name-row">
            {{ workoutDetails.name }}
            <font-awesome-icon @click="$emit('close')" class="icon" :icon="faX" />
        </div>

        <WorkoutSetsDetails :sets="workoutDetails.sets" :is-session-details="props.isSessionDetails"></WorkoutSetsDetails>
    </div>
</template>

<script setup lang="ts">
import Workout from "@/views/user/TrainerViews/models/workouts/Workout.ts"

import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faX } from "@fortawesome/free-solid-svg-icons"
import WorkoutSetsDetails from "@/views/user/TrainerViews/components/workouts/WorkoutSetsDetails.vue"
import WorkoutDetails from "@/views/user/TrainerViews/models/workouts/WorkoutDetails.ts"
import { Ref, ref } from "vue"
import { getWorkoutDetails } from "@/services/TrainerServices/workouts/workoutServices.ts"
import { getTraineeWorkoutDetails } from "@/services/TraineeServices/TraineeServices.ts"
import RBAC from "@/services/utils/RBAC/RBAC.ts"

const props = defineProps<{
    workoutId: number
    isSessionDetails: boolean
}>()

const workoutDetails: Ref<WorkoutDetails> = ref(new WorkoutDetails())
;(async () => {
    if (RBAC.isTrainer() || RBAC.isHiredTrainer()) {
        workoutDetails.value = await getWorkoutDetails(props.workoutId)
    } else {
        workoutDetails.value = await getTraineeWorkoutDetails(props.workoutId)
    }
})()
</script>

<style scoped>
.workout-details-container {
    position: absolute;
    top: -10em;
    right: -10em;
    display: flex;
    flex-direction: column;
    justify-content: start;
    width: 45em;
    height: 45em;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    z-index: 100;
}

.icon {
    color: whitesmoke;
    font-size: 1.5rem;
    cursor: pointer;
    margin-right: 0.5em;
}

.name-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 1rem;
    text-align: center;
    font-size: 2rem;
    font-weight: bold;
    color: whitesmoke;
}
</style>
