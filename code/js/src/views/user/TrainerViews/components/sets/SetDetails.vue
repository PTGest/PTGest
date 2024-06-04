<template>
    <div class="set-details">
        <h1>Set Details</h1>
        <div v-for="exercise in setDetails.setExerciseDetails">
            <p>{{ exercise.name }}</p>
            <p>Reps: {{ exercise.details['reps'] }}</p>
            <p>Modality: {{exercise.modality}}</p>
            <p>Exercise Muscle Group:
                <div v-for="muscleGroup in exercise.muscleGroup">
                    <div>{{muscleGroup}}</div>
                </div>
            </p>



        </div>

    </div>
</template>

<script setup lang="ts">
import getSetDetails from "@/services/TrainerServices/sets/getSetDetails.ts";
import {Ref, ref} from "vue";
import SetDetails from "@/views/user/TrainerViews/models/SetDetails.ts";
const props = defineProps<{
  setId: number;
}>();

const setDetails : Ref<SetDetails>= ref({});


(async() => {
   setDetails.value = await getSetDetails(props.setId);
    const exerciseDetails = setDetails.value.setExerciseDetails[0];

})();



</script>


<style scoped>
.set-details{
    position : absolute;
    top: 0;
    right: 0;
    background-color: var(--main-primary-color);
    padding: 1em;
    border-radius: 10px;
    z-index: 100;
}
</style>
