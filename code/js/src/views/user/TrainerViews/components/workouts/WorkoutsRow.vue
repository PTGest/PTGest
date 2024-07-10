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
                    <th>Favorite</th>
                    <th>Details</th>
                </tr>
            </thead>
            <tbody>
                <tr class="table-row" v-for="workout in props.workouts" :key="workout.id">
                    <td>{{ workout.name }}</td>
                    <td>
                        <textarea rows="5" readonly>{{ workout.description }}</textarea>
                    </td>
                    <td>{{ workout.muscleGroup.join(",") }}</td>
                    <td class="like-button-box">
                        <LikeExercise :id="workout.id" :is-liked="workout.isFavorite"></LikeExercise>
                    </td>
                    <td class="circle-info-box" @click="openDetails(workout)">
                        <font-awesome-icon :icon="faCircleInfo" />
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <WorkoutDetails @close="closeDetails" v-if="isDetailsOpen" :workoutId="workoutSelected.id" />
</template>

<script setup lang="ts">
import Workout from "@/views/user/TrainerViews/models/workouts/Workout.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faCircleInfo, faDumbbell } from "@fortawesome/free-solid-svg-icons"
import WorkoutDetails from "@/views/user/TrainerViews/components/workouts/WorkoutDetails.vue"
import { Ref, ref } from "vue"
import LikeExercise from "@/views/user/TrainerViews/components/utils/LikeButton.vue"
import workout from "@/views/user/TrainerViews/models/workouts/Workout.ts"

const props = defineProps<{
    workouts: Workout[]
}>()
console.log("WORKOUTS", props.workouts)

const isDetailsOpen = ref(false)
const workoutSelected: Ref<Workout | null> = ref(null)

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

.circle-info-box {
    color: whitesmoke;
    font-size: 1.5em;
    cursor: pointer;
}
</style>
