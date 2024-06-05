<template>
   <div class="exerciseDetails-container">
     <h2>{{ exerciseDetails.name }}</h2>
        <h4 v-if="exerciseDetails.details.size != 0">{{exerciseDetails.details}}</h4>
        <h3 v-else-if="exerciseDetails.ref.length != 0 " >{{exerciseDetails.ref}}</h3>
        <h3 v-else>There is no reference or details for this exercise</h3>
   </div>
</template>


<script setup lang="ts">
import {ref} from "vue";
import getExerciseDetails from "@/services/TrainerServices/exercises/getExerciseDetails.ts";


const props = defineProps<{
    exerciseId: number;
}>();



const exerciseDetails = ref({
    id: -1,
    name: "",
    modality: "",
    description: "",
    ref: "",
    details: new Map<string, any>(),
    muscleGroup: []
});


(async () => {
    exerciseDetails.value = await getExerciseDetails( props.exerciseId )
    console.log("ExerciseDetails", exerciseDetails.value);
})();
</script>

<style scoped>
.exerciseDetails-container{
    position: absolute;
    top: 0;
    right: -52%;
    width: 50%;
    border-radius: 5px;
    background-color: var(--main-primary-color);
}

.exerciseDetails-container{
    animation: slideInFromLeft 0.5s ease-in-out;

}

@keyframes slideInFromLeft {
    0% {
        transform: translateX(-100%);
    }
    100% {
        transform: translateX(0);
    }
}
</style>
