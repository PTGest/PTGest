<template>
    <div class="container">
        <FloatLabel>
            <InputText @change="handleInput" id="tricep" v-model="tricep" />
            cm
            <label for="tricep">Tricep</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="thigh" v-model="thigh" />
            cm
            <label for="thigh">Thigh</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="abdominal" v-model="abdominal" />
            cm
            <label for="abdominal">Abdominal</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="suprailiac" v-model="suprailiac" />
            cm
            <label for="suprailiac">Suprailiac</label>
        </FloatLabel>
    </div>
</template>

<script setup lang="ts">
import FloatLabel from "primevue/floatlabel"
import InputText from "primevue/inputtext"
import { ref } from "vue"

const map = new Map<string, number>()

const tricep = ref(null)
const abdominal = ref(null)
const thigh = ref(null)
const suprailiac = ref(null)

const emits = defineEmits(["skinFoldData"])
const handleInput = (e: any) => {
    map.set(e.target.id.toUpperCase(), e.target.value)
    emits("skinFoldData", map)
}
</script>

<style scoped>
.container {
    margin-top: 1.5em;
    margin-bottom: 1.5em;
    display: flex;
    flex-direction: row;
    background-color: var(--main-tertiary-color);
    padding: 3em 2em 2em 2em;
    border-radius: 10px;
    justify-content: center;
    align-items: center;
}

::placeholder {
    color: var(--main-primary-color);
    font-size: 1.2em;
    font-family: "Poppins", sans-serif;
}

input {
    margin: 0.5em;
    width: 8em;
    padding: 0.5em;
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
</style>
