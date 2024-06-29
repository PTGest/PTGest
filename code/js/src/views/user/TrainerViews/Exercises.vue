<template>
    <div v-if="!filtersOpen" class="exercises-container">
        <div class="title-row">
            <h1 class="title">Exercises</h1>
            <button @click="openAddExercise" class="add-btn">
                <font-awesome-icon :icon="faPlus" />
                Add Exercise
            </button>
        </div>

        <div class="input-row">
            <font-awesome-icon class="search-icon" :icon="faMagnifyingGlass" />
            <input v-model="searchBar" class="bar" placeholder="Search exercise name" />
            <button class="filters" @click="handleFiltersView">
                <font-awesome-icon :icon="faFilter" />
                Filters
            </button>
            <font-awesome-icon @click="resetFilters" class="reset-filters-icon" :icon="faArrowsRotate" />
        </div>

        <ExercisesTable v-if="exercises.nOfExercises > 0" :exercises="exercises.exercises.filter((exercise) => exercise.name.includes(searchBar))" />
        <div v-else class="not-found">Does not found any exercise</div>
    </div>
    <ExerciseFilters v-else @filtersApplied="applyFilters($event)" @close="filtersOpen = false" />
</template>

<script setup lang="ts">
import { Ref, ref } from "vue"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import {faArrowsRotate, faFilter, faMagnifyingGlass, faPlus} from "@fortawesome/free-solid-svg-icons"
import ExercisesTable from "./components/exercises/ExercisesTable.vue"
import Exercises from "./models/exercises/Exercises.ts"
import getExercises from "../../../services/TrainerServices/exercises/getExercises.ts"
import router from "@/plugins/router.ts"
import ExerciseFilters from "@/views/user/TrainerViews/components/exercises/exerciseFilters.vue"

const filtersOpen = ref(false)
const searchBar = ref("")
const exercises: Ref<Exercises> = ref({
    exercises: [],
    nOfExercises: 0,
})
const handleFiltersView = () => {
    console.log("openFilters", filtersOpen.value)
    filtersOpen.value = !filtersOpen.value
}

const applyFilters = (newExercises: any) => {
    console.log("Filters applied", newExercises)
    exercises.value = newExercises
}
const resetFilters = async () => {
    searchBar.value = ""
    filtersOpen.value = false
    exercises.value = await getExercises(null)
}


const openAddExercise = () => {
    router.push({ name: "addExercise" })
};

(async () => {
    try {
        exercises.value = await getExercises(null)
        console.log(exercises.value)
    } catch (error) {
        console.error("Error getting user info:", error)
    }
})()
</script>

<style scoped>
.exercises-container {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
    height: 90%;
    width: 100%;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.title-row {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 1em;
    gap: 10vw;
}

.title {
    font-size: 2rem;
    margin-left: 0.5em;
    font-family: Poppins, sans-serif;
    color: whitesmoke;
}

.add-btn {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 0.5em;
    border-radius: 5px;
    background-color: var(--sign-up-blue);
    color: whitesmoke;
    cursor: pointer;
    font-size: 15px;
}

.input-row {
    position: relative;
    left: -0.5em;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 1em;
    margin-bottom: 1em;
    gap: 1em;
}

.bar {
    margin-left: 1em;
    margin-bottom: 0;
    height: 2.5em;
    width: 100%;
    padding: 0.5em;
    border-radius: 5px;
    font-family: Poppins, sans-serif;
    font-size: 0.9rem;
    border: none;
}

.search-icon {
    position: relative;
    right: -1.5em;
}

.filters {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    background-color: var(--main-secondary-color);
    padding: 0.5em;
    border-radius: 5px;
    font-family: Poppins, sans-serif;
    font-size: 1.05rem;
    color: whitesmoke;
    transition: 0.2s ease-in;
}

.filters:hover {
    background-color: var(--light-blue);
    transition: 0.2s ease-out;
}

.not-found {
    display: flex;
    flex-direction: row;
    justify-content: center;
    width: 100%;
    height: 5em;
    font-size: 1.5rem;
    margin-top: 2em;
    color: whitesmoke;
    text-align: center;
}
.reset-filters-icon{
    color: whitesmoke;
    font-size: 1.1rem;
    cursor: pointer;
    transition: 0.2s ease-in;
}
</style>
