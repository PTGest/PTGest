<template>

    <div v-if="isLoading">
        <ProgressSpinner />
    </div>

    <div v-else>
        <div class="workouts-container" v-if="!filtersOpen">
            <div class="title-row">
                Workouts
                <button @click="openAddWorkout" class="add-workout">
                    <font-awesome-icon :icon="faPlus" />
                    Add Workout
                </button>
            </div>
            <FiltersRow @input="searchBar = $event" @open="filtersOpen = true" @reset="resetFilters"  placeholder="Search set name"/>
            <WorkoutsRow v-if="!isLoading" :workouts="workouts.workouts.filter((work: Workout) => work.name.includes(searchBar))" />
        </div>
        <Filters v-else @filtersApplied="applyFilters($event)" @close="filtersOpen = false"  filters-type="workouts"/>
        <AddWorkout @closeAddWorkout="openAddWorkout" v-if="isAddWorkoutOpen"></AddWorkout>
    </div>

</template>

<script setup lang="ts">
import WorkoutsRow from "./components/workouts/WorkoutsRow.vue"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faPlus } from "@fortawesome/free-solid-svg-icons"
import AddWorkout from "@/views/user/TrainerViews/components/workouts/AddWorkout.vue"
import {onMounted, Ref, ref} from "vue"
import Workouts from "@/views/user/TrainerViews/models/workouts/Workouts.ts"

import router from "@/plugins/router.ts"
import {getWorkouts} from "@/services/TrainerServices/workouts/workoutServices.js";
import FiltersRow from "@/views/user/TrainerViews/components/utils/FiltersRow.vue";
import Filters from "@/views/user/TrainerViews/components/utils/Filters.vue";
import {getSets} from "@/services/TrainerServices/sets/setServices.ts";
import Workout from "@/views/user/TrainerViews/models/workouts/Workout.ts";
import Exercises from "@/views/user/TrainerViews/models/exercises/Exercises.ts";
import ProgressSpinner from "primevue/progressspinner";
import workout from "@/views/user/TrainerViews/models/workouts/Workout.ts";

const workouts: Ref<Workouts> = ref(new Workouts())
const isAddWorkoutOpen = ref(false)
const searchBar = ref("")
const filtersOpen = ref(false)
const isLoading = ref(true)

const openAddWorkout = () => {
    router.push({ name: "addWorkout" })
}

const applyFilters = (newWorkouts: any) => {
        console.log("Filters applied", newWorkouts)
        workouts.value.workouts = newWorkouts
        console.log(workouts.value)
}

const resetFilters = async() => {
        filtersOpen.value = false
        workouts.value = await getWorkouts(null)
}

onMounted(async () => {
    workouts.value = await getWorkouts()
    isLoading.value = false
    console.log(workouts.value)
});

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

.title-row {
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

.add-workout {
    position: relative;
    top: 0;
    color: whitesmoke;
    font-size: 1rem;
    background-color: var(--sign-up-blue);
}
</style>
