<template>
    <h2>Add new Session</h2>
    <div class="add-session-container">
        <div class="item">
            <label for="workout">Workout</label>
            <MultiSelect v-model="selectedWorkout" display="chip" :options="workouts.workouts.map((workout:Workout) => {
            return {
                id: workout.id,
                name: workout.name
            }
        })" optionLabel="name" filter placeholder="Select Workout" :maxSelectedLabels="3" class="w-full md:w-80" />
        </div>
        <div class="item">
            <label for="workout">Begin Date</label>
            <Calendar id="calendar-24h" v-model="beginDate" showTime hourFormat="24" />
        </div>
        <div class="item">
            <label for="workout">End Date</label>
            <Calendar id="calendar-timeonly" v-model="endTime" timeOnly />
        </div>
        <div class="radio-buttons">
            <div class="radio-btn">
                <RadioButton class="btn" v-model="workoutType" inputId="ingredient1" name="PlanBased" value="PLAN_BASED" />
                <label for="plan-based" class="ml-1">Plan Based</label>
            </div>
            <div class="radio-btn">
                <RadioButton v-model="workoutType" inputId="ingredient2" name="TrainerGuided" value="TRAINER_GUIDED" />
                <label for="trainer-guided" class="ml-2">Trainer Guided</label>
            </div>
        </div>

        <textarea class="text-area" v-model="setNotes"/>
        <button class="submit-btn">Add Session</button>
    </div>
</template>

<script setup lang="ts">


import {Ref, ref} from "vue";
import MultiSelect from "primevue/multiselect";
import getWorkouts from "@/services/TrainerServices/workouts/getWorkouts.ts";
import Workouts from "@/views/user/TrainerViews/models/workouts/Workouts.ts";
import CreateSessionRequest from "@/views/user/TrainerViews/models/sessions/CreateSessionRequest.ts";
import Workout from "@/views/user/TrainerViews/models/workouts/Workout.ts";
import Calendar from "primevue/calendar";
import RadioButton from "primevue/radiobutton";

const workoutType = ref('');
const workouts = ref(new Workouts());
const selectedWorkout = ref<string>('');
const setNotes = ref<string>('');
const beginDate : Ref<Date> = ref(new Date());
const endTime : Ref<Date> = ref(new Date());
const sessionRequestData : Ref<CreateSessionRequest> = ref(new CreateSessionRequest());

(async () => {
    workouts.value = await getWorkouts();
})()

</script>

<style scoped>
.add-session-container{
    display: flex;
    padding: 1em;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 25em;
    background-color: var(--main-primary-color);
    border-radius: 5px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
    overflow-y: scroll;
    gap: 1em;
}


:deep(.p-multiselect-trigger-icon){
    color: whitesmoke;
}
:deep(.p-multiselect){
    width: 15em;
    background-color: var(--main-primary-color);
    outline: var(--main-secondary-color);
}
:global(.p-multiselect-panel){
    width: 15em;
    position:relative;
    left: 0;
    background-color: transparent;
}
:global(.p-multiselect-empty-message){
    color: whitesmoke;
}

:deep(.p-placeholder){
    color: whitesmoke;
}

:deep(.p-inputtext){
    text-align: center;
    width: 15em;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    outline: whitesmoke;
}

:global(.p-datepicker-header){
    background-color: var(--main-primary-color);
    color: whitesmoke;
    outline: whitesmoke;
}

:global(.p-datepicker){

    left: -2em;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    outline: whitesmoke;
}

:global(.p-link){
    color: whitesmoke;
}
:global(.p-datepicker-next){
    color: whitesmoke;
}

.end-time{
    width: 2em;
}

.radio-buttons{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
.radio-btn{
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    margin: 0.5em;
    gap:1em;
}

.btn{
    position: relative;
    right:0.85em;
}

textarea{
    padding: 0.5em;
    resize: none;
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
    background-color: var(--main-secondary-color);
    transition : 0.2s ease-out;
}

.submit-btn{
   background-color: var(--main-secondary-color);
    color: whitesmoke;
}

.item{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

</style>
