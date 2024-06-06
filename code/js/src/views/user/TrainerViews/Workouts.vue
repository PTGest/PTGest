<template>
    <div>
        <div v-if="!isAddWorkoutOpen" class="workouts-container">
            <div class="title-row">
                Workouts
                <button @click="handleAddWorkoutVisibility" class="add-workout">
                    <font-awesome-icon :icon="faPlus"/>
                    Add Workout
                </button>
            </div>
            <WorkoutsRow  :workouts="workouts.workouts"/>
        </div>
        <AddWorkout @closeAddWorkout="handleAddWorkoutVisibility" v-if="isAddWorkoutOpen"></AddWorkout>
    </div>
</template>

<script setup lang="ts">

import WorkoutsRow from "./components/workouts/WorkoutsRow.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import AddWorkout from "@/views/user/TrainerViews/components/workouts/AddWorkout.vue";
import {Ref, ref} from "vue";
import Workouts from "@/views/user/TrainerViews/models/workouts/Workouts.ts";
import getWorkouts from "@/services/TrainerServices/workouts/getWorkouts.ts";

const workouts : Ref<Workouts> = ref(new Workouts());
const isAddWorkoutOpen = ref(false);

const handleAddWorkoutVisibility = () => {
    isAddWorkoutOpen.value = !isAddWorkoutOpen.value;
}

(async () => {
    workouts.value = await getWorkouts();
    console.log(workouts.value);
})()


</script>

<style scoped>
.workouts-container{
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.title-row{
    position: relative;
    display: flex;
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    font-size: 2rem;
    color: whitesmoke;
    margin: 0 1em 0 1em;
    padding: 1.5rem;
}

.add-workout{
    position: relative;
    top: 0;
    color: whitesmoke;
    font-size: 1rem;
    background-color: var(--sign-up-blue);
}

</style>
