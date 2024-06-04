<template>
    <table>
        <tr>
            <th class="properties-text">Name</th>
            <th class="properties-text">Muscle Group</th>
            <th class="properties-text">Modality</th>
        </tr>

        <tr class="table-row" v-for="exercise in props.exercises" :key="exercise.id">
            <td class="exercise-name">
                {{exercise.name}}
            </td>
            <td class="normal-text">
                {{exercise.muscleGroup.map((muscle: string) => muscle).join(", ")}}
            </td>
            <td>
                {{exercise.modality}}
            </td>
        </tr>

    </table>
</template>


<script setup lang="ts">

import Exercise from "../../models/Exercise.ts";

const props = defineProps<{
    exercises: Exercise[]
}>();

</script>



<style scoped>

table{
    margin : 0em 1em 1em 1em;
    width: calc(100% - 2em);
    border-collapse: collapse;
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



.table-row{
    border-top: 1px solid rgba(245, 245, 245, 0.2);
    border-radius: 10px;
}

.table-row:hover{
    cursor: pointer;
    background-color: var(--main-secundary-color);
    border-radius: 10px;
}

</style>
