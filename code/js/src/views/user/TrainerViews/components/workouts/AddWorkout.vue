<template>
    <div>
        <div class="add-workout-container">
            <div class="title-row">
                <input class="name-input" placeholder="Enter Workout Name"/>
                <font-awesome-icon @click="closeAddWorkout" class="icon" :icon="faX"></font-awesome-icon>
            </div>

           <div class="dropdown-container">
               <div class="dropdowns">
                   <div class="label">Workout options</div>
                   <MultiSelect v-model="selectedMuscleGroups" :options="muscleGroupOptions" optionLabel="name" placeholder="Select Muscle groups"
                                :maxSelectedLabels="3" class="w-full md:w-20rem" />

                   <MultiSelect v-model="selectedSets" :options="options" optionLabel="name" placeholder="Select sets"
                                :maxSelectedLabels="3" class="w-full md:w-20rem" />
               </div>
               <div class="text-area-container">
                   <div class="label">Details</div>
                   <textarea class="text-area" placeholder="" />
               </div>
           </div>


        </div>
    </div>
</template>

<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faX} from "@fortawesome/free-solid-svg-icons";
import MultiSelect from "primevue/multiselect";
import {ref} from "vue";

const emit = defineEmits(['closeAddWorkout']);

const selectedSets = ref();
const selectedMuscleGroups = ref();
const options = [{name:'1'}, {name:'2'}, {name:'3'}]
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
const closeAddWorkout = () => {
    emit('closeAddWorkout');
    console.log('close');
}

</script>

<style scoped>
.add-workout-container{
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.icon{
    cursor: pointer;
}

.title-row{
    display : flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 1em 2em 0 1.5em;
}


.name-input{
    width: 13.5em;
    height: 2em;
    padding: 0.5em;
    font-family: Poppins, sans-serif;
    font-size: 1.5em;
    border-radius: 5px;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    margin: 1em;
    outline: none;
    transition: 0.3s;
    border: 1px solid rgba(245, 245, 245, 0.2);
}

.name-input:hover{
   background-color: var(--main-secondary-color);
    transition: background-color 0.2s ease-out;

}
.name-input:focus{
    border: 1px solid var(--main-secondary-color);
}

::placeholder{
    color: var(--main-tertiary-color);
}


:deep(.p-multiselect-label-container){
    display: flex;
    flex-direction: row;
    justify-content: start;
}

:deep(.p-multiselect){
    margin : 0.5em;
    width: 21.5em;
    border: 1px solid rgba(245, 245, 245, 0.2);
    background-color: var(--main-primary-color);
    outline: none;
}

:deep(.p-icon){
    color: whitesmoke;
}

:global(.p-multiselect-items-wrapper) {
    background: var(--main-primary-color);
    color: whitesmoke;
}

:global(.p-multiselect-item){
    background: var(--main-primary-color);
    color: whitesmoke;
}
:global(.p-multiselect-item:hover){
    background: var(--main-secondary-color);
    color: whitesmoke;
}

:global(.p-multiselect-panel){
    border : 1px solid rgba(245, 245, 245, 0.2);
    outline: none;
}

:global(.p-multiselect-header) {
    background: var(--main-primary-color);
    padding: 0.5em 0.5em 0.5em 1em;
    color: whitesmoke;
}

:deep(.p-multiselect .p-multiselect-label.p-placeholder){
    color : whitesmoke;
}

:global(.p-multiselect-close){
    background: var(--main-secondary-color);
    color: whitesmoke;
    padding:0;
}

:global(.p-multiselect-trigger-icon){
    color: whitesmoke;
}
:global(::-webkit-scrollbar), :global(.menu-open::-webkit-scrollbar){
    width: 7px;

}
:global(::-webkit-scrollbar-thumb),:global( ::-webkit-scrollbar-thumb){
    background-color: var(--main-secondary-color);
    border-radius: 10px;
}

.text-area-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.text-area{
    padding: 1em;
    border-radius: 5px;
    border: 1px solid var(--main-secondary-color);
    margin: 0 0.5em 1.5em 0.5em;
    font-size: 1.3rem;
    font-family: 'Poppins', sans-serif;
    color: whitesmoke;
    width: 84%;
    background-color: var(--main-primary-color);
}

textarea{
    resize: none;
}

.dropdown-container{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap : 1em;
    width: 100%;
    padding: 1em;
}
.dropdowns{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.label{
    font-size: 0.6rem;
    width: 84%;
    text-align: left;
}

</style>
