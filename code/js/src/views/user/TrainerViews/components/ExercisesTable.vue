<template>
    <table>
        <tr>
            <th class="properties-text">Name</th>
            <th class="properties-text">Muscle Group</th>
            <th class="properties-text">Sets</th>

        </tr>

        <tr v-for="exercise in props.exercises" :key="exercise.id">
            <td class="exercise-name">
                {{exercise.name}}
            </td>
            <td class="normal-text">
                {{exercise.description}}
            </td>
            <td>
                {{exercise.sets}}
            </td>
        </tr>

    </table>
</template>


<script setup lang="ts">

import Exercise from "../../../../views/user/TrainerViews/models/Exercise.ts";

const props = defineProps<{
    exercises: Exercise[]
}>();

</script>



<style scoped>

table{
    margin-left: 1em;
    margin-right: 1em;
    width: calc(100% - 2em);
}

.exercise-name{
    font-family: Poppins, sans-serif;
    font-weight: bold;
    font-size: 20px;
    padding: 0.5em;
}

.normal-text{
    font-family: Poppins, sans-serif;
    font-size: 15px;
    padding: 0.5em;
}

td,tr{
    border-top: 1px solid rgba(245, 245, 245, 0.2);
}



</style>
