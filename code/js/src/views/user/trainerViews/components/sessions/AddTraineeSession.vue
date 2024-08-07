<template>
    <h2>{{ props.isEdit ? "Edit Session" : "Add new Session" }}</h2>
    <div class="add-session-container">
        <font-awesome-icon class="x-icon" @click="$router.go(-1)" :icon="faX" />
        <div class="item">
            <label for="workout">Workout</label>
            <ExercisesDropdown
                @dropdownOption="updateWorkout($event)"
                :options="
                    workouts.workouts.map((workout: Workout) => {
                        return {
                            id: workout.id,
                            name: workout.name,
                        }
                    })
                "
                option-Label="name"
                filter
                placeholder="Select Workout"
            />
        </div>
        <div class="item">
            <label for="workout">Begin Date</label>
            <Calendar id="calendar-24h" v-model="beginDate" show-Time hour-Format="24" />
        </div>
        <div v-if="workoutType == 'TRAINER_GUIDED'" class="item">
            <label for="workout">End Date</label>
            <Calendar id="calendar-timeonly" v-model="endTime" time-Only />
        </div>
        <div class="radio-buttons">
            <div class="radio-btn">
                <RadioButton class="btn" v-model="workoutType" input-Id="ingredient1" name="PlanBased" value="PLAN_BASED" />
                <label for="plan-based" class="ml-1">Plan Based</label>
            </div>
            <div class="radio-btn">
                <RadioButton v-model="workoutType" input-Id="workoutType" name="TrainerGuided" value="TRAINER_GUIDED" />
                <label for="trainer-guided" class="ml-2">Trainer Guided</label>
            </div>
        </div>
        <label for="location">Location</label>
        <input v-model="location" placeholder="Enter Location"/>
        <textarea class="text-area" v-model="setNotes" />
        <Button @click="addSession" label="submit" class="submit" :disabled="isDisable">{{ props.isEdit ? "Edit Session" : "Add Session" }}</Button>
    </div>
</template>

<script setup lang="ts">
import { computed, Ref, ref } from "vue"

import Workouts from "@/views/user/trainerViews/models/workouts/Workouts.ts"
import CreateSessionRequestGuided from "@/views/user/trainerViews/models/sessions/CreateSessionRequestGuided.js"
import Calendar from "primevue/calendar"
import RadioButton from "primevue/radiobutton"
import ExercisesDropdown from "@/views/user/trainerViews/components/exercises/ExercisesDropdown.vue"
import { createSession } from "@/services/trainerServices/sessions/sessionServices.ts"
import router from "@/plugins/router.ts"
import Button from "primevue/button"
import Workout from "@/views/user/trainerViews/models/workouts/Workout.ts"

import TrainerSessionDetails from "@/views/user/trainerViews/models/sessions/TrainerSessionDetails.ts"
import store from "@/store"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faX } from "@fortawesome/free-solid-svg-icons"
import { getWorkouts } from "@/services/trainerServices/workouts/workoutServices.ts"
import { editSession } from "@/services/trainerServices/sessions/sessionServices.ts"
import CreateSessionRequestPlanBased from "@/views/user/trainerViews/models/sessions/CreateSessionRequestPlanBased.ts";

const props = defineProps<{
    isEdit: boolean
    sessionData: TrainerSessionDetails
}>()


const workoutType = ref(props.isEdit ? props.sessionData.workoutType : "")
const workouts = ref(props.isEdit ? props.sessionData.workouts : new Workouts())
const selectedWorkout = ref(props.isEdit ? props.sessionData.selectedWorkout : "")
const setNotes = ref<string | null>(props.isEdit ? props.sessionData.setNotes : null)
const beginDate: Ref<Date> = ref(props.isEdit ? props.sessionData.beginDate : new Date())
const endTime: Ref<Date> = ref(props.isEdit ? props.sessionData.endTime : new Date())
const location = ref("")

;(async () => {
    workouts.value = await getWorkouts()
})()

const updateWorkout = (workout: any) => {
    selectedWorkout.value = workout
}

const isDisable = computed(() => {
    return !(selectedWorkout.value && beginDate.value && workoutType.value && location.value)
})

const addSession = async () => {
    try {
        if (workoutType.value == "TRAINER_GUIDED") {
            const endDate = new Date(beginDate.value)
            const sessionRequestData: Ref<CreateSessionRequestGuided> = ref(new CreateSessionRequestGuided())
            endDate.setHours(endTime.value.getHours(), endTime.value.getMinutes(), endTime.value.getSeconds())
            sessionRequestData.value = {
                traineeId: store.getters.traineeInfo.id,
                workoutId: selectedWorkout.value.id,
                beginDate: beginDate,
                endDate: endDate,
                location: location.value,
                notes: setNotes.value,
                type: "trainer_guided",
            }
            if (!props.isEdit) {
                await createSession(sessionRequestData.value)
            } else {
                await editSession(props.sessionData.id, sessionRequestData.value)
            }
            router.go(-1)
        } else {
            const sessionRequestData: Ref<CreateSessionRequestPlanBased> = ref(new CreateSessionRequestPlanBased())
            sessionRequestData.value = {
                traineeId: store.getters.traineeInfo.id,
                workoutId: selectedWorkout.value.id,
                beginDate: beginDate.value,
                notes: setNotes.value,
                type: "plan_based",
            }
            if (!props.isEdit) {
                await createSession(sessionRequestData.value)
            } else {
                await editSession(props.sessionData.id, sessionRequestData.value)
            }
            router.go(-1)
        }
    } catch (e) {
        console.log(e)
    }
}
</script>

<style scoped>
.add-session-container {
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

:deep(.p-placeholder) {
    color: whitesmoke;
}

:deep(.p-inputtext) {
    text-align: center;
    width: 15em;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    outline: whitesmoke;
}

:global(.p-datepicker-header) {
    background-color: var(--main-primary-color);
    color: whitesmoke;
    outline: whitesmoke;
}

:global(.p-datepicker) {
    left: -2em;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    outline: whitesmoke;
}

:global(.p-link) {
    color: whitesmoke;
}
:global(.p-datepicker-next) {
    color: whitesmoke;
}

.end-time {
    width: 2em;
}

.radio-buttons {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
.radio-btn {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    margin: 0.5em;
    gap: 1em;
}

.btn {
    position: relative;
    right: 0.85em;
}

textarea {
    padding: 0.5em;
    resize: none;
}
.text-area {
    background-color: var(--main-primary-color);
    width: 20em;
    height: 10em;
    margin: 0.5em;
    color: whitesmoke;
    font-family: Poppins, sans-serif;
    font-size: 1em;
    border-radius: 5px;
    border: 1px solid rgba(245, 245, 245, 0.2);
    outline: none;
    transition: 0.2s ease-in;
}
.text-area:focus {
    outline: 1px solid rgba(245, 245, 245, 0.2);
}

.text-area:hover {
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-out;
}

.submit {
    background-color: var(--main-secondary-color);
    color: whitesmoke;
}

.item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.x-icon {
    align-self: flex-end;
    cursor: pointer;
}
input{
    width: 24em;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    padding: 1em;
    border-radius: 5px;
    border: 1px solid rgba(245, 245, 245, 0.2);
    outline: none;
    transition: 0.2s ease-in;
}

::placeholder{
    color: whitesmoke;
}
</style>
