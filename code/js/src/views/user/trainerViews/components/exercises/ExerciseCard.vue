<template>
    <div class="exercise-card">
        <h2>{{ props.exercise.name }}</h2>
        <div class="exercise-property">{{ props.exercise.description }}</div>
        <div class="exercise-property">{{ props.exercise.sets }}</div>
    </div>
</template>

<script setup lang="ts">
import Exercise from "../../models/exercises/Exercise.ts"

const props = defineProps<{
    exercise: Exercise
}>()
</script>

<style scoped>
.exercise-card {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
    grid-template-rows: 1.5em;
    grid-column-gap: 2em;
    justify-items: center;
    align-items: center;
    color: whitesmoke;
    padding: 2em;
    border-top: 1px solid rgba(245, 245, 245, 0.2);
}

.exercise-property {
    font-family: Poppins, sans-serif;
    font-size: 15px;
    padding: 0.5em;
}
</style>
