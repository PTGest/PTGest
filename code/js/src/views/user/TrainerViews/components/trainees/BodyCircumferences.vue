<template>
    <div class="body-main-container">
        <p>Body Circumferences</p>
        <div class="body-container">
            <FloatLabel>
                <InputText @change="handleInput" id="neck" v-model="bodyCircumferences.neck"/>
                cm
                <label for="neck">Neck</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="chest" v-model="bodyCircumferences.chest" />
                cm
                <label for="chest">Chest</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput"  id="waist" v-model="bodyCircumferences.waist" />
                cm
                <label for="waist">Waist</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="hips" v-model="bodyCircumferences.hips" />
                cm
                <label for="hips">Hips</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="thighs" v-model="bodyCircumferences.thighs" />
                cm
                <label for="thighs">Thighs</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="calves" v-model="bodyCircumferences.calves" />
                cm
                <label for="calves">Calves</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="relaxedRightArm" v-model="bodyCircumferences.relaxedRightArm" />
                cm
                <label for="relaxedRightArm">RelaxedRightArm</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="relaxedLeftArm" v-model="bodyCircumferences.relaxedLeftArm" />
                cm
                <label for="relaxedLeftArm">RelaxedLeftArm</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="flexedRightArm" v-model="bodyCircumferences.flexedRightArm" />
                cm
                <label for="flexedRightArm">FlexedRightArm</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="flexedLeftArm" v-model="bodyCircumferences.flexedLeftArm" />
                cm
                <label for="flexedLeftArm">FlexedLeftArm</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="forearm" v-model="bodyCircumferences.forearm" />
                cm
                <label for="pectoral">Forearm</label>
            </FloatLabel>
        </div>
    </div>
</template>

<script setup lang="ts">
import BodyCircumferences from "@/views/user/TrainerViews/models/traineeData/BodyCircumferences.ts";
import {ref} from "vue";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";

const emits = defineEmits(['bodyCircumferenceData']);
const bodyCircumferences = ref(new BodyCircumferences());
//11

function handleInput() {
    console.log("BODY CIRCUMFERENCES", bodyCircumferences.value);
    emits('bodyCircumferenceData', bodyCircumferences.value);
}



</script>

<style scoped>
.body-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-gap: 1em 0;
    background-color: var(--main-primary-color);
    justify-items: center;
    align-items: center;
}

.body-main-container {
    position : absolute;
    top: -63%;
    right: -105%;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: var(--main-primary-color);
    padding: 1em;
    border-radius: 10px;
    color: whitesmoke;
    z-index:99;
    animation: slideInFromLeft 0.8s;
}

.body-container > FloatLabel:last-child {
    grid-column: span 3;
    text-align: center;
}

::placeholder {
    color: var(--main-primary-color);
    font-size: 1.2em;
    font-family: 'Poppins', sans-serif;
}

input {
    margin: 0.5em;
    width: 8em;
    padding: 0.5em;
    border-radius: 10px;
    border: 1px solid var(--main-primary-color);
    font-size: 1em;
    font-family: 'Poppins', sans-serif;
    background-color: white;
    text-align: center;
}

label {
    align-self: center;
    color: whitesmoke;
    font-size: 0.9em;
    margin-left: 0.3em;
}

.body-container input {
    margin: 10px;
    padding: 10px;
    background-color: var(--main-secondary-color);
    border-radius: 5px;
    text-align: center;
}
p{
    padding: 0em;
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
