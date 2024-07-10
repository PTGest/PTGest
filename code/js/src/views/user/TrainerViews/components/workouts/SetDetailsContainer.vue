<template>
    <div class="details-container">
        <div class="header-row" @click="openDetails">
            {{ props.set.name }}
            <font-awesome-icon :class="isOpen ? 'icon-open' : 'icon'" :icon="faCaretLeft" />
        </div>
        <div v-if="isOpen" class="setDetails">
            <div class="type">{{ props.set.type }}</div>

            <div class="exercise-details" v-if="isSessionDetails">
                <div class="feedbacks-label">
                    {{ `Feedbacks (${setFeedbacks.length})` }}
                    <font-awesome-icon @click="addSetFeedbackOpen = true" class="plus-icon" :icon="faPlus" />
                </div>
                <AddSetFeedback @close="addSetFeedbackOpen = false" class="set-feedback-container" v-if="addSetFeedbackOpen" :set-order-id="props.set.orderId" :set-id="props.set.id"> </AddSetFeedback>
            </div>

            <div class="exercise-details">
                <div class="label">Exercises</div>
                <WorkoutExerciseDetails v-bind="exercise" v-for="exercise in props.set.setExerciseDetails" :exercise="exercise"></WorkoutExerciseDetails>
            </div>

            <div v-if="notes != null" class="details">
                <div class="label">Notes</div>
                <Textarea class="notes" v-model="notes" rows="5" cols="40" disabled></Textarea>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import SetDetails from "@/views/user/TrainerViews/models/sets/SetDetails.ts"
import Textarea from "primevue/textarea"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faCaretLeft, faPlus } from "@fortawesome/free-solid-svg-icons"
import { Ref, ref } from "vue"
import WorkoutExerciseDetails from "@/views/user/TrainerViews/components/workouts/WorkoutExerciseDetails.vue"
import store from "../../../../../store"
import { getTrainerSetFeedback } from "@/services/TrainerServices/sessions/sessionServices.ts"
import SetSessionFeedback from "@/views/user/TrainerViews/models/sessions/SetSessionFeedbacks.ts"
import AddSetFeedback from "@/views/user/TrainerViews/components/sessions/AddSetFeedback.vue"

const props = defineProps<{
    set: SetDetails
    isSessionDetails: boolean
}>()
const addSetFeedbackOpen = ref(false)
const notes: Ref<string | null> = ref(props.set.notes)
const isOpen = ref(false)
const setFeedbacks: Ref<SetSessionFeedback[]> = ref(new Array<SetSessionFeedback>())
;(async () => {
    if (props.isSessionDetails) {
        setFeedbacks.value = await getTrainerSetFeedback(store.getters.sessionDetails.workoutId)
        console.log("SET FEEDBACKS", setFeedbacks.value)
    }
})()

const openDetails = () => {
    isOpen.value = !isOpen.value
}
</script>

<style scoped>
.details-container {
    background-color: var(--main-secondary-color);
    width: 90%;
    border-radius: 10px;
    max-height: 20em;
    margin: 0.5em;
    border: 3px solid var(--main-secondary-color);
    overflow-y: scroll;
}

.setDetails {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.2em;
    color: white;
    font-size: 1.5rem;
    cursor: pointer;
}

.icon,
.icon-open {
    color: white;
    font-size: 1.5rem;
    cursor: pointer;
    margin-right: 0.5em;
}

.icon-open {
    transform: rotate(270deg);
}

::-webkit-scrollbar-thumb {
    background-color: white !important;
}

.notes {
    resize: none;
    width: 100%;
}

.label,
.feedbacks-label {
    font-weight: bold;
    font-size: 0.7rem;
    color: white;
    padding: 0.5em;
}

.feedbacks-label {
    display: flex;
    width: 100%;
    justify-content: space-between;
    align-items: center;
}

.details,
.exercise-details {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
    padding: 0.5em;
    width: 100%;
}

.type {
    font-weight: bold;
}
.plus-icon {
    cursor: pointer;
    color: white;
    font-size: 1rem;
}

.set-feedback-container {
    position: absolute;
    width: 20em;
    height: 20em;
    top: 25%;
    left: 25%;
    border-radius: 10px;
}
</style>
