<template>
    <div class="ExerciseFilters">
        <div class="title">Filters</div>
        <font-awesome-icon @click="emit('close')" class="close-icon" :icon="faTimes"/>

        <ExercisesDropdown @dropdownOption="modality = $event" :options="modalityOptions" placeholder="Filter by Modality"></ExercisesDropdown>
        <ExercisesDropdown @dropdownOption="selectedMuscleGroups = $event" :options="muscleGroupOptions"  placeholder="Filter by Muscle Groups"/>

        <button class="applyFilters" @click="applyFilters">Apply Filters</button>

    </div>
</template>

<script setup lang="ts">


import {ref} from "vue";
import getExercises from "@/services/TrainerServices/exercises/getExercises.ts";
import ExercisesDropdown from "../exercises/ExercisesDropdown.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";

const modality = ref<string>('');
const selectedMuscleGroups = ref<string>('');
const emit = defineEmits(['filtersApplied', 'close']);
const modalityOptions = [
    {name: 'BODYWEIGHT'},
    {name: 'WEIGHTLIFT'},
    {name: 'RUNNING_IN'},
    {name: 'RUNNING_OUT'},
    {name: 'CYCLING_IN'},
    {name: 'CYCLING_OUT'},
    {name: 'OTHER'}
];


const muscleGroupOptions = ref([
    {name: 'BICEPS'},
    {name:'CHEST'},
    {name:'CORE'},
    {name:'FOREARMS'},
    {name:'FULL_BODY'},
    {name:'GLUTES'},
    {name:'HAMSTRINGS'},
    {name:'HIP_GROIN'},
    {name:'LOWER_BACK'},
    {name:'LOWER_BODY'},
    {name:'LOWER_LEG'},
    {name:'MID_BACK'},
    {name:'QUADS'},
    {name:'SHOULDERS'},
    {name:'TRICEPS'},
    {name:'UPPER_BACK_NECK'},
    {name:'UPPER_BODY'}
]);

const applyFilters = async () => {
    const filters = new Map<string, any>();
    if (modality.value !== '') {
        filters.set('modality', modality.value.name);
    }
    if (selectedMuscleGroups.value !== '') {
        filters.set('muscleGroups', selectedMuscleGroups.value.name);
    }
    const exercises = await getExercises(filters)
    emit('filtersApplied', exercises);
    emit('close')
    console.log("Filters applied")
}

</script>

<style scoped>
.ExerciseFilters{
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 20em;
    height: 20em;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    border-radius: 5px;
}

:deep(.select){
    width: 15em;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    color: whitesmoke;
    font-weight: 600;
    margin: 0.5em;
}

:deep(.menu-open){
    width: 15em;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    color: whitesmoke;
    font-weight: 600;
    margin: 1.5em 0 0 0.5em;
}

:deep(.menu){
    width: 15em;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    color: whitesmoke;
    font-weight: 600;
    margin: 1.5em 0 0 0.5em;
}

.applyFilters{
    margin-top: 2em;
    color: whitesmoke;
}

.close-icon{
    position: absolute;
    top: 0.5em;
    right: 0.5em;
    width: 1em;
    color: whitesmoke;
    cursor: pointer;
}
</style>
