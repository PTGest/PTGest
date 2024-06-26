<template>
    <div class="workouts-container">
        <table class="table">
            <thead>
                <tr>
                    <th>
                        <font-awesome-icon :icon="faDumbbell" />
                        Name
                    </th>
                    <th>Description</th>
                    <th>Muscle Group</th>
                </tr>
            </thead>
            <tbody>
                <tr @click="openDetails(workout)" class="table-row" v-for="workout in props.workouts" :key="workout.id">
                    <td>{{ workout.name }}</td>
                    <td>
                        <textarea rows="5" readonly>{{ workout.description }}</textarea>
                    </td>
                    <td>{{ workout.muscleGroup.join(",") }}</td>
                </tr>
            </tbody>
        </table>
    </div>
    <WorkoutDetails @close="closeDetails" v-if="isDetailsOpen" :workout="workoutSelected" />
</template>

<script setup lang="ts">
import Workout from "@/views/user/TrainerViews/models/workouts/Workout.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faDumbbell } from "@fortawesome/free-solid-svg-icons"
import WorkoutDetails from "@/views/user/TrainerViews/components/workouts/WorkoutDetails.vue"
import { ref } from "vue"

const props = defineProps<{
    workouts: Workout[]
}>()
const isDetailsOpen = ref(false)
const workoutSelected = ref<Workout | null>(null)

const openDetails = (workout: Workout) => {
    isDetailsOpen.value = !isDetailsOpen.value
    workoutSelected.value = workout
    console.log("WORKOUT", workoutSelected.value)
}
const closeDetails = () => {
    isDetailsOpen.value = false
    workoutSelected.value = null
}
</script>

<style scoped>
.workouts-container {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    width: 30em;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin: 1rem;
}

th {
    padding: 1rem;
}

td {
    padding: 1rem;
    text-align: center;
}

tr {
    border-bottom: 1px solid var(--main-secondary-color);
}

.table-row:hover {
    border: 2px solid var(--button-border-color);
    color: whitesmoke;
    cursor: pointer;
}

textarea {
    resize: none;
    text-align: center;
    background-color: var(--main-primary-color);
    border-radius: 5px;
    color: whitesmoke;
    font-size: 1em;
    align-content: center;
    overflow-y: scroll !important;
    border: 1px solid var(--main-secondary-color);
}

textarea:focus {
    outline: none;
}
</style>
