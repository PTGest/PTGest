<template>
    <div class="exercise-details-row">
        <div class="exercise-details">
            <div class="exercise-name">{{props.exercise.name}}</div>
        </div>
        <SetExerciseDetailBoxes @details="handleDetails"/>
    </div>
</template>

<script setup lang="ts">
import SetExerciseDetailBoxes from "./SetExerciseDetailBoxes.vue";
import SetExerciseDetails from "../../models/setExerciseDetails.ts";


const props = defineProps<{
    exercise: {
        id: number;
        name: string;
    };
}>();

const emit = defineEmits(['exerciseDetails']);


const handleDetails = (details: Map<string, any>) => {
    emit('exerciseDetails', new SetExerciseDetails(props.exercise.id, props.exercise.name, details));
}



console.log("ExerciseDetails", props.exercise);
</script>



<style scoped>

.exercise-details-row{
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    top:0;
    padding: 1rem;
    border-radius : 10px;
    background-color: var(--main-primary-color);
}


.exercise-details-row{
    animation: slideInFromLeft 0.5s ease-in-out;
}
@keyframes  slideInFromLeft {
    0% {
        transform: translateX(-100%);
    }
    100% {
        transform: translateY(0);
    }
}
</style>
