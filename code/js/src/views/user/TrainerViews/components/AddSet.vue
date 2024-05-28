<template>
    <div>
       <div class="add-set-container">
           <div class="set-header-row">
               <input v-model="setName" class="exercise-name-input" placeholder="Name your set" />
                <button @click="$emit('close')" class="close-button">
                     <FontAwesomeIcon :icon="faTimes"/>
                </button>
           </div>

           <div class="dropdown-wrapper">
               <label class="label">Type</label>
               <ExercisesDropdown :options="setTypes" placeholder="Set Type" @dropdownOption="updateSetType($event)"/>

               <label class="label">Exercise</label>
               <ExercisesDropdown :options="[]" placeholder="Select Exercise" @dropdownOption="updateSetType($event)"/>

               <label class="label">Notes</label>
               <Textarea class="text-area" v-model="setNotes" rows="5" cols="30" />
           </div>
           <div class="submit-row">
               <Button class="submit-btn" disabled>Create Set</Button>
           </div>
       </div>
    </div>

</template>

<script setup lang="ts">

import {ref} from "vue";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import ExercisesDropdown from "../../../../views/user/TrainerViews/components/ExercisesDropdown.vue";

const setName = ref("");
const setNotes = ref("");
const modalityOption = ref("");
const setTypes = ["SIMPLESET", "DROPSET", "SUPERSET"];

const updateSetType = (type: string) => {
    modalityOption.value = type;
}
</script>


<style scoped>
.add-set-container{
    position : absolute;
    top: 20%;
    left: 38%;
    display: flex;
    flex-direction: column;
    padding: 1rem;
    background-color: var(--main-primary-color);
    color : whitesmoke;
    border-radius: 10px;
    width: 30em;
    z-index: 100;
}

.exercise-name-input{
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
}

.exercise-name-input:focus{
    border: 1px solid rgba(245, 245, 245, 0.2);
}

.exercise-name-input:hover{
    background-color: var(--main-secundary-color);
}

input{
    border: none;
    outline: none;
}

.set-header-row{
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    width: 100%;
    padding: 1em;
    gap: 1em;
}

.dropdown-wrapper{
    position: relative;
    bottom: 2em;
    display: flex;
    flex-direction: column;
    justify-items: start;
    align-items: start;
    gap: 0.5em;
    padding: 2em 2em 0 2em;
}

.label{
    position: relative;
    left: 1em;
    top: 1em;
    display: flex;
    flex-direction: row;
    justify-content: start;
    width: 100%;
    font-size: 0.6em;
    color: whitesmoke;
}

.text-area{
    background-color: var(--main-primary-color);
    width: 20em;
    height: 10em;
    margin:0.5em;
    color: whitesmoke;
    font-family: Poppins, sans-serif;
    font-size: 1em;
    border-radius: 5px;
    border: 1px solid rgba(245, 245, 245, 0.2);
    outline: none;
    transition: 0.2s ease-in;
}
.text-area:focus{
    outline: 1px solid rgba(245, 245, 245, 0.2);
}

.text-area:hover{
    background-color: var(--main-secundary-color);
    transition : 0.2s ease-out;
}
textarea{
    resize: none;
}

.close-button{
    position : absolute;
    right: 2em;
    top: 3.3em;
    color: whitesmoke;
    font-size: 1.1em;
    background-color: var(--main-primary-color);
    transition: 0.2s ease-in;
}

.close-button:hover{
   background-color: var(--main-secundary-color);
    border-color: var(--sign-up-blue);
    transition: 0.2s ease-out;
}

.submit-btn{
    position : relative;
    left : -1em;
    padding: 1em;
    width: 15em;
    color: whitesmoke;
    font-size: 13px;
    transition : 0.2s ease-in;
}

.submit-btn:hover{
    background-color: var(--main-secundary-color);
    transition: 0.2s ease-out;
    border-color: var(--sign-up-blue);
}


.submit-row{
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 1em;
    width: 100%;
}

::placeholder{
    color: rgba(245, 245, 245, 0.4);
}

</style>
