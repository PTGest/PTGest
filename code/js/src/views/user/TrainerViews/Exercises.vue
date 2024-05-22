<template>
    <div class="exercises-container">
        <div class="title-row">
            <h1 class="title">Exercises</h1>
            <button @click="openAddExercise" class="add-exercise-button">
                <font-awesome-icon :icon="faPlus"/>
                Add Exercise
            </button>
        </div>

        <AddExercise class="add-exercise-container" v-if="isAddExerciseOpen" @close="openAddExercise"/>

        <div class="input-row">
            <font-awesome-icon class="search-icon" :icon="faMagnifyingGlass"/>
            <input-bar class="bar" class-name="search-bar" height="2.5em" width="100%" padding="0.5em"
                       placeholder="Search exercise name">
            </input-bar>
            <button class="filters">
                <font-awesome-icon :icon="faFilter"/>
                Filters
            </button>

            <Filters v-if="filtersOpen"/>
        </div>

        <ExercisesTable :exercises="exercises"/>

    </div>

</template>

<script setup lang="ts">


import {Ref, ref, UnwrapRef} from "vue";
import Exercise from "../../../views/user/TrainerViews/models/Exercise.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faFilter, faMagnifyingGlass, faPlus} from "@fortawesome/free-solid-svg-icons";
import InputBar from "../../../components/utils/InputBar.vue";
import Filters from "../../../views/user/CompaniesViews/Components/Filters.vue";
import ExercisesTable from "../../../views/user/TrainerViews/components/ExercisesTable.vue";
import AddExercise from "@/views/user/TrainerViews/components/AddExercise.vue";

const isAddExerciseOpen = ref(false);
const filtersOpen = ref(false);
const exercises : Ref<UnwrapRef<Exercise[]>> = ref([
    new Exercise(1,"Bench Press", "Chest", "3x10"),
    new Exercise(1,"Bench Press", "Chest", "3x10"),
]);

const openAddExercise = () => {
    isAddExerciseOpen.value = !isAddExerciseOpen.value;
}

</script>


<style scoped>

.exercises-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
    height:90%;
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

.add-exercise-button{
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 10px;
    font-family: Poppins, sans-serif;
    font-size: 1rem;
    color : whitesmoke;
    background-color: var(--light-blue);
    white-space: nowrap;
}

.input-row{
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


.bar{
    margin-left: 1em;
    margin-bottom: 0;
}

.search-icon{
    position: relative;
    right: -1.5em;
}

.filters{
    background-color: var(--main-secundary-color);
    padding: 0.5em;
    border-radius: 5px;
    font-family: Poppins, sans-serif;
    font-size: 1rem;
    color : whitesmoke;
    transition: 0.2s ease-in;
}

.filters:hover{
    background-color: var(--light-blue);
    transition: 0.2s ease-out;
}

</style>
