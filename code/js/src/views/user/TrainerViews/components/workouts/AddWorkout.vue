<template>
    <div>
        <div class="add-workout-container">
            <div class="title-row">
                <input v-model="workoutName" class="name-input" placeholder="Enter Workout Name" />
                <font-awesome-icon @click="closeAddWorkout" class="icon" :icon="faX"></font-awesome-icon>
            </div>

            <div class="dropdown-container">
                <div class="dropdowns">
                    <div class="label">Workout options</div>
                    <MultiSelect
                        v-model="selectedMuscleGroups"
                        :options="muscleGroupOptions"
                        filter
                        option-Label="name"
                        placeholder="Select Muscle groups"
                        :max-Selected-Labels="3"
                        class="w-full md:w-20rem"
                    />

                    <MultiSelect v-model="selectedSets" :options="availableSets.sets" option-Label="name" filter placeholder="Select sets" :max-Selected-Labels="3" class="w-full md:w-20rem" />
                    <router-link :to="{ name: 'addSet' }" class="link">
                        <Button @click="handleAddSetVisibility" class="add-set">
                            <font-awesome-icon :icon="faPlus" />
                            Add Set
                        </Button>
                    </router-link>
                </div>
                <div class="text-area-container">
                    <div class="label">Details</div>
                    <textarea v-model="description" class="text-area" placeholder="" />
                </div>
                <Button @click="addWorkout" class="submit" :disabled="isDisable"> Add Workout </Button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import Button from "primevue/button"
import { faPlus, faX } from "@fortawesome/free-solid-svg-icons"
import MultiSelect from "primevue/multiselect"
import { computed, Ref, ref } from "vue"
import Sets from "../../../../../views/user/TrainerViews/models/sets/Sets.ts"
import getSets from "../../../../../services/TrainerServices/sets/getSets.ts"
import createCustomWorkout from "@/services/TrainerServices/workouts/createCustomWorkout.ts"
import CreateCustomWorkoutRequest from "@/views/user/TrainerViews/models/workouts/CreateCustomWorkoutRequest.ts"
import AddSet from "@/views/user/TrainerViews/components/sets/AddSet.vue"
import router from "@/plugins/router.ts"

const workoutName = ref(null)
const description = ref(null)
const selectedSets = ref([])
const selectedMuscleGroups = ref([])
const availableSets: Ref<Sets> = ref({
    sets: [],
    nOfSets: 0,
})

const handleAddSetVisibility = () => {
    addSetOpen.value = !addSetOpen.value
}

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

const isDisable = computed(() => {
    return !(selectedMuscleGroups.value.length != 0 && selectedSets.value.length != 0)
})

;(async () => {
    availableSets.value = await getSets([])
})()

const addWorkout = async () => {
    console.log(workoutName.value)
    console.log(description.value)
    console.log(selectedSets.value)
    console.log(selectedMuscleGroups.value)

    await createCustomWorkout(
        new CreateCustomWorkoutRequest(
            workoutName.value,
            description.value,
            selectedMuscleGroups.value.map((muscleGroup) => muscleGroup.name),
            selectedSets.value.map((set) => set.id)
        )
    )
    console.log("workout added")
}

const closeAddWorkout = () => {
    router.go(-1)
}
</script>

<style scoped>
.add-workout-container {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.icon {
    cursor: pointer;
}

.title-row {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 1em 2em 0 1.5em;
}

.name-input {
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
    border: 1px solid rgba(245, 245, 245, 0.2);
}

.name-input:hover {
    background-color: var(--main-secondary-color);
    transition: background-color 0.2s ease-out;
}
.name-input:focus {
    border: 1px solid var(--main-secondary-color);
}

::placeholder {
    color: var(--main-tertiary-color);
}

:deep(.p-multiselect-label-container) {
    display: flex;
    flex-direction: row;
    justify-content: start;
}

:deep(.p-multiselect) {
    margin: 0.5em;
    width: 21.5em;
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

.text-area-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.text-area {
    padding: 1em;
    border-radius: 5px;
    border: 1px solid var(--main-secondary-color);
    margin: 0 0.5em 1.5em 0.5em;
    font-size: 1.3rem;
    font-family: "Poppins", sans-serif;
    color: whitesmoke;
    width: 84%;
    background-color: var(--main-primary-color);
}

textarea {
    resize: none;
}

.dropdown-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 1em;
    width: 100%;
    padding: 1em;
}
.dropdowns {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.label {
    font-size: 0.6rem;
    width: 84%;
    text-align: left;
}

.submit {
    background-color: #1a1a1a;
    color: whitesmoke;
    font-size: 1rem;
}

.add-set {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 0.5em;
    border: 1px solid var(--main-secondary-color);
    transition: 0.3s ease-out;
}

.add-set:hover {
    cursor: pointer;
    border: 1px solid var(--button-border-color);
    background-color: var(--main-secondary-color);
}

.link {
    width: 84%;
}
</style>
