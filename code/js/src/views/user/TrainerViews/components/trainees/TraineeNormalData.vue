<template>
    <div>
        <h2>Trainee Info</h2>
        <div class="container">
            <SelectButton @click="updateGender" id="gender" class="gender-button" v-model="gender" :options="options" aria-labelledby="basic" />
            <FloatLabel class="inpt2">
                <InputText @change="handleInput" id="height" v-model="normalData.height" />
                cm
                <label for="Height">Height</label>
            </FloatLabel>
            <FloatLabel class="inpt2">
                <InputText @change="handleInput" id="weight" v-model="normalData.weight" />
                kg
                <label for="Weight">Weight</label>
            </FloatLabel>

            <div class="body-circfrs" @click="openBodyCircfrs">Body Circumferences</div>
            <BodyCircumferences @body-circumference-data="handleBodyData" v-if="isOpen" />
        </div>
    </div>
</template>

<script setup lang="ts">
import FloatLabel from "primevue/floatlabel"
import InputText from "primevue/inputtext"
import { ref } from "vue"
import TraineeNormalData from "@/views/user/TrainerViews/models/traineeData/TraineeNormalData.ts"
import SelectButton from "primevue/selectbutton"
import BodyCircumferences from "@/views/user/TrainerViews/components/trainees/BodyCircumferences.vue"

const props = defineProps<{
    data: TraineeNormalData
}>()
const emits = defineEmits(["normalData"])
const gender = ref(props.data.gender)
const options = ["MALE", "FEMALE"]
const isOpen = ref(false)
const normalData = ref(new TraineeNormalData())

const updateGender = () => {
    console.log(gender.value)
    normalData.value.gender = gender.value
    emits("normalData", normalData.value)
    console.log("Normal Data", normalData.value.gender)
}
const handleInput = (e: any) => {
    const { id, value } = e.target
    // Allow only alphanumeric characters and ';'
    normalData.value[id] = value.replace(/[^a-zA-Z0-9;]/g, "")
    normalData.value.name = props.data.name
    emits("normalData", normalData.value)
    console.log(normalData.value)
}

const handleBodyData = (bodyCircumferenceData: any) => {
    console.log("Body Circumference Data", bodyCircumferenceData)
    normalData.value.bodyCircumferences = bodyCircumferenceData
    emits("normalData", normalData.value)
}

const openBodyCircfrs = () => {
    isOpen.value = !isOpen.value
}
</script>

<style scoped>
.container {
    position: relative;
    margin-top: 1.5em;
    margin-bottom: 1.5em;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    background-color: var(--main-tertiary-color);
    padding: 2em;
    border-radius: 10px;
    justify-items: center;
    align-items: center;
}

input {
    margin: 0.5em;
    width: 8em;
    border-radius: 10px;
    border: 1px solid var(--main-primary-color);
    font-size: 1em;
    font-family: "Poppins", sans-serif;
    background-color: white;
    text-align: center;
}
label {
    align-self: center;
    color: whitesmoke;
    font-size: 1em;
    margin-left: 0.5em;
}

.container input {
    margin: 10px;
    padding: 10px;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    text-align: center;
}

.body-circfrs {
    position: relative;
    margin-top: 1em;
    grid-column: 1/4;
    background-color: var(--main-secondary-color);
    padding: 0.5em;
    border-radius: 5px;
    text-align: center;
    cursor: pointer;
    font-size: 1em;
    color: whitesmoke;
}
</style>
