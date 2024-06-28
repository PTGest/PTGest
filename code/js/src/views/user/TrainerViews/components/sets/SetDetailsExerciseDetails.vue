<template>
    <div @click="openExerciseDetails" class="exercise-container">
        <h2>{{ props.exercise.name }}</h2>
        <h3>{{ props.exercise.modality }}</h3>
        <h3 v-if="props.exercise.details['REPS']!=null">Reps: {{ props.exercise.details["REPS"] }}</h3>
        <h3 v-if="props.exercise.details['WEIGHT']!=null">WEIGHT: {{ props.exercise.details["WEIGHT"] }}</h3>
        <h3>
            Muscle Group:
            <div  v-for="muscleGroup in props.exercise.muscleGroup">
                <div>{{ muscleGroup }}</div>
            </div>
        </h3>
    </div>

    <SetExerciseDetails v-if="isExerciseDetailsOpen" :exercise-id="exercise.id"></SetExerciseDetails>
</template>

<script setup lang="ts">
import SetExerciseDetails from "@/views/user/TrainerViews/components/sets/SetExerciseDetails.vue"
import SetExerciseDetailsModel from "@/views/user/TrainerViews/models/sets/SetExerciseDetails.ts"
import { ref } from "vue"
const props = defineProps<{
    exercise: SetExerciseDetailsModel
}>()

const isExerciseDetailsOpen = ref(false)
const openExerciseDetails = () => {
    isExerciseDetailsOpen.value = true
}
</script>

<style scoped>
.exercise-container {
    background-color: var(--main-secondary-color);
    padding: 1em;
    border-radius: 10px;
    margin-top: 1em;
    cursor: pointer;
    transition: background-color 0.3s;
}

.exercise-container:hover {
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    transition: background-color 0.3s;
}

h2 {
    padding-top: 0.5em;
}

.exercises-details {
    position: absolute;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: fit-content;
    top: 0;
    left: 30rem;
    padding: 1rem;
    margin-left: 0.5rem;
    border-radius: 10px;
    background-color: var(--main-primary-color);
}
</style>
