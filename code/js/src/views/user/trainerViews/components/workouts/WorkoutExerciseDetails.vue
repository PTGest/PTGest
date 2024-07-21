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
                        <div v-if="props.exercise.details['REPS'] != null" class="label">Reps: {{ props.exercise.details["REPS"] }}</div>
                        <div v-if="props.exercise.details['WEIGHT'] != null" class="label">Weight: {{ props.exercise.details["WEIGHT"] }}</div>
                        <div v-if="props.exercise.details['DURATION'] != null" class="label">Duration: {{ props.exercise.details["DURATION"] }}</div>
                        <div v-if="props.exercise.details['DISTANCE'] != null" class="label">Distance: {{ props.exercise.details["DISTANCE"] }}</div>
                        <div v-if="props.exercise.details['SPEED'] != null" class="label">Speed: {{ props.exercise.details["SPEED"] }}</div>
                        <div v-if="props.exercise.details['REST_TIME'] != null" class="label">RestTime: {{ props.exercise.details["REST_TIME"] }}</div>
                        <div v-if="props.exercise.details['RESISTANCE'] != null" class="label">Resistance: {{ props.exercise.details["RESISTANCE"] }}</div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script setup lang="ts">
import SetExerciseDetails from "@/views/user/trainerViews/models/sets/SetExerciseDetails.ts"

const props = defineProps<{
    exercise: SetExerciseDetails
}>()

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
