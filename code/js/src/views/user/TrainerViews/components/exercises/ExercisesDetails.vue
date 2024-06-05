<template>
    <div class="exerciseDetails-container">
        <ExerciseDetails @exerciseDetails="handleDetails($event)"  v-for="exercise in props.exercises" :exercise="exercise"></ExerciseDetails>
    </div>
</template>

<script setup lang="ts">
import ExerciseDetails from "./ExerciseDetails.vue";
import SetExerciseDetails from "../../models/sets/SetExerciseDetails.ts";
import SetExercise from "../../models/sets/SetExercise.ts";
import {ref, Ref} from "vue";
const props = defineProps<{
    exercises: { id: number, name: string }[];
}>();

console.log("Exercises",props.exercises);

const emit = defineEmits(['exerciseDetails']);

const exerciseDetailsList : Ref<SetExercise[]> = ref([]);

const handleDetails = (details: SetExerciseDetails) => {

    console.log("Details2", details)


    const exercise = exerciseDetailsList.value.findIndex((exercise) => exercise.exerciseId === details.id);
    if(exercise !== -1){
        exerciseDetailsList.value[exercise] = new SetExercise(details.id, details.details);
    }
    else{
        exerciseDetailsList.value.push(new SetExercise(details.id, details.details));
    }
    emit ('exerciseDetails', exerciseDetailsList.value);
}

</script>


<style scoped>

.exerciseDetails-container{
    position : absolute;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width:fit-content;
    top:0;
    left:30rem;
    padding: 1rem;
    margin-left: 0.5rem;
    border-radius : 10px;
    background-color: var(--main-primary-color);
}

.exerciseDetails-container{
    animation: slideInFromLeft 0.4s ease-in-out;
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
