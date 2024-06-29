<template>
    <div class="exercise-container">
        <div class="name">
            {{ props.exercise.name }}
        </div>

        <table>
            <thead>
                <tr>
                    <th>Muscle Group</th>
                    <th>Modality</th>
                    <th>Details</th>
                </tr>
            </thead>

            <tbody>
                <tr>
                    <td class="exercise-details-box">{{ props.exercise.muscleGroup.join(", ") }}</td>
                    <td class="exercise-details-box">{{ props.exercise.modality }}</td>
                    <td class="exercise-details-box">
                        <div class="label">Reps: {{ props.exercise.details["reps"] }}</div>
                        <div v-if="props.exercise.details['weight'] != null" class="label">Weight: {{ props.exercise.details["weight"] }}</div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script setup lang="ts">
import SetExerciseDetails from "@/views/user/TrainerViews/models/sets/SetExerciseDetails.ts"

const props = defineProps<{
    exercise: SetExerciseDetails
}>()

console.log(props.exercise.details)
</script>

<style scoped>
.exercise-container {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    width: 100%;
    background-color: var(--main-primary-color);
    border-radius: 5px;
    color: whitesmoke;
    margin-bottom: 1em;
}

.name {
    font-size: 1.5rem;
    font-weight: bold;
    padding: 0.5em;
}

.exercise-details-box {
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    padding: 0.5em 1em 0.5em 1em;
    margin: 0.5em;
}

td {
    padding: 10px;
}

table {
    border-spacing: 10px 5px;
    margin-bottom: 1em;
}
</style>
