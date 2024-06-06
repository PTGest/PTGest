<template>
    <div class="details-container">
        <div class="name-row">
            {{props.workout.name}}
            <font-awesome-icon @click="$emit('close')" class="icon" :icon="faX"/>
        </div>

        <WorkoutSetsDetails :sets="workoutDetails.sets"></WorkoutSetsDetails>
    </div>
</template>

<script setup lang="ts">
import Workout from "@/views/user/TrainerViews/models/workouts/Workout.ts";
import getWorkoutDetails from "@/services/TrainerServices/workouts/getWorkoutDetails.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faX} from "@fortawesome/free-solid-svg-icons";
import WorkoutSetsDetails from "@/views/user/TrainerViews/components/workouts/WorkoutSetsDetails.vue";
import WorkoutDetails from "@/views/user/TrainerViews/models/workouts/WorkoutDetails.ts";
import {Ref, ref} from "vue";

const props = defineProps<{
  workout: Workout;
}>();

const workoutDetails : Ref<WorkoutDetails> = ref(new WorkoutDetails());
(async () => {
    workoutDetails.value = await getWorkoutDetails(props.workout.id);
})()


</script>


<style scoped>
.details-container{
    position: absolute;
    top: -10em;
    right: -10em;
    display: flex;
    flex-direction: column;
    justify-content: start;
    width: 45em;
    height: 45em;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.icon{
    color: whitesmoke;
    font-size: 1.5rem;
    cursor: pointer;
    margin-right:0.5em;
}

.name-row{
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 1rem;
    text-align: center;
    font-size: 2rem;
    font-weight: bold;
    color: whitesmoke;
}
</style>
