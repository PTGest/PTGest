<template>
    <div v-if="!filtersOpen" class="exercises-container">
        <div class="title-row">
            <h1 class="title">Exercises</h1>
            <button @click="openAddExercise" class="add-btn">
                <font-awesome-icon :icon="faPlus" />
                Add Exercise
            </button>
        </div>

        <FiltersRow @input="searchBar = $event" @open="filtersOpen = true" @reset="resetFilters" placeholder="Search exercise name" />

        <ExercisesTable v-if="exercises.nOfExercises > 0" :exercises="exercises.exercises.filter((exercise) => exercise.name.includes(searchBar))" />
        <div v-else class="not-found">Does not found any exercise</div>
    </div>
    <Filters v-else @filtersApplied="applyFilters($event)" @close="filtersOpen = false" filters-type="exercises" />
</template>

<script setup lang="ts">
import { Ref, ref } from "vue"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faPlus } from "@fortawesome/free-solid-svg-icons"
import ExercisesTable from "./components/exercises/ExercisesTable.vue"
import Exercises from "./models/exercises/Exercises.ts"
import router from "@/plugins/router.ts"
import { getExercises } from "@/services/TrainerServices/exercises/exerciseServices.ts"
import FiltersRow from "@/views/user/TrainerViews/components/utils/FiltersRow.vue"
import Filters from "@/views/user/TrainerViews/components/utils/Filters.vue"

const filtersOpen = ref(false)
const searchBar = ref("")
const exercises: Ref<Exercises> = ref({
    exercises: [],
    nOfExercises: 0,
})

const applyFilters = (newExercises: any) => {
    console.log("Filters applied", newExercises)
    exercises.value = newExercises
}
const resetFilters = async () => {
    filtersOpen.value = false
    exercises.value = await getExercises(null)
}

const openAddExercise = () => {
    router.push({ name: "addExercise" })
}

;(async () => {
    try {
        exercises.value = await getExercises(null)
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
.reset-filters-icon {
    color: whitesmoke;
    font-size: 1.1rem;
    cursor: pointer;
    transition: 0.2s ease-in;
}
</style>
