<template>
    <div class="add-exercise-container">
        <div class="add-exercise-header">
            <input v-model="exerciseName" class="exercise-name-input" placeholder="Name your exercise" />
            <font-awesome-icon @click="$router.go(-1)" class="close-button" :icon="faTimes" />
        </div>
        <div class="dropdowns-container">
            <div class="label-text">Primary Focus</div>
            <ExercisesDropdown :options="modalityOptions" placeholder="Modality" @dropdownOption="modalityDropdownOption($event)" />
            <MultiSelect
                v-model="selectedMuscleGroups"
                display="chip"
                :options="muscleGroupOptions"
                option-Label="name"
                placeholder="Select Muscle Groups"
                :max-Selected-Labels="3"
                class="w-full md:w-20rem"
            />
            <div class="label-text">Ref</div>
            <input v-model="reference" class="ref" />
        </div>

        <div class="exercise-instructions">
            <div class="exercise-instructions-input">
                <label class="label">Description</label>
                <Textarea placeholder="Enter Exercise Description" v-model="exerciseInstructions" rows="9" cols="30" auto-resize />
            </div>
            <Button v-if="isAllFieldsFilled" class="submit-btn" label="Submit" @click="submitExercise" />
            <Button v-else class="submit-btn" label="Submit" disabled />
        </div>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faTimes } from "@fortawesome/free-solid-svg-icons"
import ExercisesDropdown from "./ExercisesDropdown.vue"
import { computed, ref } from "vue"
import MultiSelect from "primevue/multiselect"
import Textarea from "primevue/textarea"
import Button from "primevue/button"
import { createExercise } from "../../../../../services/trainerServices/exercises/exerciseServices.ts"
import CreateCustomExerciseRequest from "../../models/exercises/CreateCustomExerciseRequest.ts"

const modalityOptions = [{ name: "BODYWEIGHT" }, { name: "WEIGHTLIFT" }, { name: "RUNNING" }, { name: "CYCLING" }, { name: "OTHER" }]

const exerciseName = ref("")
const modalityOption = ref("")
const exerciseInstructions = ref(null)
const reference = ref(null)
const selectedMuscleGroups = ref<string[]>([])
const muscleGroupOptions = ref([
    { name: "BICEPS" },
    { name: "CHEST" },
    { name: "CORE" },
    { name: "FOREARMS" },
    { name: "FULL_BODY" },
    { name: "GLUTES" },
    { name: "HAMSTRINGS" },
    { name: "HIP_GROIN" },
    { name: "LOWER_BACK" },
    { name: "LOWER_BODY" },
    { name: "LOWER_LEG" },
    { name: "MID_BACK" },
    { name: "QUADS" },
    { name: "SHOULDERS" },
    { name: "TRICEPS" },
    { name: "UPPER_BACK_NECK" },
    { name: "UPPER_BODY" },
])

const isAllFieldsFilled = computed(() => {
    console.log()
    return modalityOption.value != "" && selectedMuscleGroups.value != null && exerciseName.value != ""
})
const modalityDropdownOption = (option: any) => {
    modalityOption.value = option.name
    console.log(modalityOption.value)
}

const submitExercise = async () => {
    console.log("submitting exercise")
    const exercise = await createExercise(
        new CreateCustomExerciseRequest(
            exerciseName.value,
            modalityOption.value,
            selectedMuscleGroups.value.map((muscleGroup) => muscleGroup.name),
            exerciseInstructions.value,
            reference.value
        )
    )
    console.log("SUBMITTING EXERCISE", exercise)
}
</script>

<style scoped>
.add-exercise-container {
    position: relative;
    display: grid;
    grid-template-rows: 1fr 2fr;
    grid-template-columns: 1.3fr 1fr;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    min-width: 40vw;
    height: 30em;
    z-index: 99;
}

.add-exercise-header {
    display: flex;
    width: 26em;
    justify-content: space-between;
    align-items: center;
    padding: 1em;
    grid-column-start: 1;
    grid-column-end: 2;
}

.close-button {
    position: relative;
    top: -1em;
    left: 1em;
    color: whitesmoke;
    font-size: 1.5em;
    margin-right: 1em;
    cursor: pointer;
}

.exercise-name-input {
    width: 100%;
    height: 2em;
    padding: 0.5em;
    font-family: Poppins, sans-serif;
    font-size: 1.5em;
    border-radius: 5px;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    margin: 1em;
    outline: none;
    transition: 0.3s;
}

.exercise-name-input:focus {
    border: 1px solid rgba(245, 245, 245, 0.2);
}

.exercise-name-input:hover {
    background-color: var(--main-secondary-color);
}

input {
    border: none;
    outline: none;
}

.dropdowns-container {
    position: relative;
    bottom: 2em;
    display: flex;
    flex-direction: column;
    justify-items: center;
    align-items: center;
    width: 20em;
    gap: 0.5em;
    margin-left: 2.5em;
    grid-column-start: 1;
    grid-column-end: 2;
}

.w-full {
    width: 20em;
    color: whitesmoke;
    background-color: var(--main-primary-color);
    outline-color: whitesmoke;
}

:deep(.p-multiselect-label-container) {
    display: flex;
    flex-direction: row;
    justify-content: start;
}

:deep(.p-multiselect) {
    border: 1px solid rgba(245, 245, 245, 0.2);
}

:deep(.p-icon) {
    color: whitesmoke;
}

:global(.p-multiselect-items-wrapper) {
    background: var(--main-primary-color);
    color: whitesmoke;
}

:global(.p-multiselect-item) {
    background: var(--main-primary-color);
    color: whitesmoke;
}
:global(.p-multiselect-item:hover) {
    background: var(--main-secondary-color);
    color: whitesmoke;
}

:global(.p-multiselect-panel) {
    border: 1px solid rgba(245, 245, 245, 0.2);
    outline: none;
}

:global(.p-multiselect-header) {
    background: var(--main-primary-color);
    padding: 0.5em 0.5em 0.5em 1em;
    color: whitesmoke;
}

:deep(.p-multiselect .p-multiselect-label.p-placeholder) {
    color: whitesmoke;
}

:global(.p-multiselect-close) {
    background: var(--main-secondary-color);
    color: whitesmoke;
    padding: 0;
}

:global(.p-multiselect-trigger-icon) {
    color: whitesmoke;
}
:global(::-webkit-scrollbar),
:global(.menu-open::-webkit-scrollbar) {
    width: 7px;
}
:global(::-webkit-scrollbar-thumb),
:global(::-webkit-scrollbar-thumb) {
    background-color: var(--main-secondary-color);
    border-radius: 10px;
}
.label-text {
    position: relative;
    top: 1em;
    display: flex;
    flex-direction: row;
    justify-content: start;
    width: 100%;
    font-size: 0.6em;
    color: whitesmoke;
}

.exercise-instructions {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    grid-column-start: 2;
    grid-column-end: 2;
    grid-row-start: 1;
    grid-row-end: 3;
}
.exercise-instructions-input {
    position: relative;
    top: 3em;
}

:deep(.p-inputtext) {
    background: var(--main-primary-color);
    color: whitesmoke;
    border: none;
}
:deep(.p-inputtext:focus) {
    outline: whitesmoke;
}

label {
    display: flex;
    flex-direction: row;
    font-size: 1em;
    color: whitesmoke;
    width: 100%;
    padding: 0.5em;
}

.submit-btn {
    margin-top: 5em;
    background-color: var(--main-primary-color);
}

::placeholder {
    color: rgba(245, 245, 245, 0.4);
}

.ref {
    width: 100%;
    height: 2em;
    padding: 0.7em;
    font-family: Poppins, sans-serif;
    font-size: 1em;
    border-radius: 5px;
    color: whitesmoke;
    background-color: var(--main-primary-color);
    border: 1px solid rgba(245, 245, 245, 0.2);
    margin-top: 0.5em;
    outline: none;
    transition: 0.3s;
}
</style>
