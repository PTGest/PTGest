<template>
    <div>
        <div class="add-set-container">
            <div class="set-header-row">
                <input v-model="setName" class="exercise-name-input" placeholder="Name your set" />
                <button @click="$router.go(-1)" class="close-button">
                    <FontAwesomeIcon :icon="faTimes" />
                </button>
            </div>

            <ExercisesDetails class="exerciseDetails-container" @exerciseDetails="handleExerciseDetails" v-if="selectedExercises.length > 0" :exercises="selectedExercises"></ExercisesDetails>

            <div class="dropdown-wrapper">
                <label class="label">Type</label>
                <ExercisesDropdown :options="setTypes" placeholder="Set Type" @dropdownOption="updateSetType($event)" />

                <label class="label">Exercise</label>
                <MultiSelect
                    v-if="typeOption.name === 'SUPERSET'"
                    v-model="selectedExercises"
                    filter
                    display="chip"
                    :options="
                        exercises.exercises.map((exercise) => {
                            return {
                                id: exercise.id,
                                name: exercise.name,
                            }
                        })
                    "
                    option-Label="name"
                    placeholder="Select Exercise"
                    :max-Selected-Labels="3"
                    class="w-full md:w-20rem"
                />
                <exercises-dropdown
                    v-else
                    @dropdownOption="handleExercises($event)"
                    :options="
                        exercises.exercises.map((exercise) => {
                            return {
                                id: exercise.id,
                                name: exercise.name,
                            }
                        })
                    "
                    placeholder="Enter Exercise"
                >
                </exercises-dropdown>
                <label class="label">Notes</label>
                <textarea class="text-area" v-model="setNotes" />
            </div>
            <div class="submit-row">
                <Button @click="submitSet" label="submit" :class="isDisable ? 'submit-btn-disable' : 'submit-btn'" :disabled="isDisable">Create Set</Button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, Ref, ref } from "vue"
import { faTimes } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import ExercisesDropdown from "../exercises/ExercisesDropdown.vue"

import Exercises from "../../models/exercises/Exercises.ts"
import MultiSelect from "primevue/multiselect"
import ExercisesDetails from "../exercises/ExercisesDetails.vue"
import SetExercise from "../../models/sets/SetExercise.ts"
import { createSet } from "../../../../../services/trainerServices/sets/setServices.ts"
import CreateCustomSetRequest from "../../models/sets/CreateCustomSetRequest.ts"
import router from "@/plugins/router.ts"
import { getExercises } from "@/services/TrainerServices/exercises/exerciseServices.js"

const selectedExercises: Ref<{ id: number; name: string }[]> = ref([])
const exercises: Ref<Exercises> = ref({
    exercises: [],
    nOfExercises: 0,
})
const exerciseDetailsList: Ref<SetExercise[]> = ref([])
const setName = ref(null)
const setNotes = ref(null)
const typeOption = ref("")
const setTypes = [{ name: "SIMPLESET" }, { name: "DROPSET" }, { name: "SUPERSET" }]

const isDisable = computed(() => {
    return typeOption.value === "" || selectedExercises.value.length === 0 || exerciseDetailsList.value.length === 0
})

;(async () => {
    const allExercises = await getExercises([])
    exercises.value.exercises = allExercises.exercises
    exercises.value.nOfExercises = allExercises.nOfExercises
})()

const handleExercises = (exercise: { id: number; name: string }) => {
    selectedExercises.value.pop()
    selectedExercises.value.push(exercise)
}

const handleExerciseDetails = (details: SetExercise[]) => {
    exerciseDetailsList.value = details
    console.log(exerciseDetailsList.value)
}
const updateSetType = (type: string) => {
    typeOption.value = type
    console.log(typeOption.value)
}

const submitSet = async () => {
    console.log(setNotes.value)
    const set = await createSet(
        new CreateCustomSetRequest(setName.value === null ? null : setName.value, setNotes.value === null ? null : setNotes.value, typeOption.value.name, exerciseDetailsList.value)
    )
    router.go(-1)
    console.log("SUBMITTING SET", set)
}
</script>

<style scoped>
.add-set-container {
    position: absolute;
    top: 20%;
    left: 38%;
    display: flex;
    flex-direction: column;
    padding: 1rem;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    border-radius: 10px;
    width: 30em;
    z-index: 100;
}

.exercise-name-input {
    width: 13.5em;
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

.set-header-row {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    width: 100%;
    padding: 1em;
    gap: 1em;
}

.dropdown-wrapper {
    position: relative;
    bottom: 2em;
    display: flex;
    flex-direction: column;
    justify-items: start;
    align-items: start;
    gap: 0.5em;
    padding: 2em 2em 0 2em;
}

.label {
    position: relative;
    left: 1em;
    top: 1em;
    display: flex;
    flex-direction: row;
    justify-content: start;
    width: 100%;
    font-size: 0.6em;
    color: whitesmoke;
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

.close-button {
    position: absolute;
    right: 2em;
    top: 3.3em;
    color: whitesmoke;
    font-size: 1.1em;
    background-color: var(--main-primary-color);
    transition: 0.2s ease-in;
}

.close-button:hover {
    background-color: var(--main-secondary-color);
    border-color: var(--sign-up-blue);
    transition: 0.2s ease-out;
}

.submit-btn,
.submit-btn-disable {
    position: relative;
    left: -1em;
    padding: 1em;
    width: 15em;
    color: whitesmoke;
    font-size: 13px;
    transition: 0.2s ease-in;
}

.submit-btn-disable {
    cursor: not-allowed;
    color: var(--main-secondary-color);
}

.submit-btn:hover {
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-out;
    border-color: var(--sign-up-blue);
}

.submit-row {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 1em;
    width: 100%;
}

::placeholder {
    color: rgba(245, 245, 245, 0.4);
}

:deep(.p-multiselect-label-container) {
    display: flex;
    flex-direction: row;
    justify-content: start;
}

:deep(.p-multiselect) {
    margin: 0.5em;
    width: 20em;
    border: 1px solid rgba(245, 245, 245, 0.2);
    background-color: var(--main-primary-color);
    outline: none;
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
</style>
