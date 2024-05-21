<template>
    <div class="add-exercise-container">

        <div class="add-exercise-header">
            <input class="exercise-name-input" placeholder="Name your exercise" />
            <font-awesome-icon class="close-button" @click="$emit('close')" :icon="faTimes"/>
        </div>

        <ExercisesDropdown/>
        



    </div>
</template>

<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import ExercisesDropdown from "../../../../views/user/TrainerViews/components/ExercisesDropdown.vue";
</script>


<style scoped>
.add-exercise-container{
    background-color: var(--primary-color);
    border-radius: 10px;
}


.add-exercise-header{
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1em;
}


.close-button{
    position: relative;
    top: -1em;
    color: whitesmoke;
    font-size: 1.5em;
    margin-right: 0.5em;
    cursor: pointer;
}

.exercise-name-input{
    width: 50%;
    height: 1.5em;
    padding: 0.5em;
    font-family: Poppins, sans-serif;
    font-size: 1.5em;
    border-radius: 5px;
    background-color: var(--primary-color);
    color: whitesmoke;
    margin: 1em;
    outline: none;
    transition: 0.3s;
}

.exercise-name-input:focus{
    border: 1px solid rgba(245, 245, 245, 0.8);
}

.exercise-name-input:hover{
    background-color: var(--secundary-color);
}

input{
    border: none;
    outline: none;
}

.add-exercise-body{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

</style>
