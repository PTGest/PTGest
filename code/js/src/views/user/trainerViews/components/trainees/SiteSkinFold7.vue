<template>
    <div class="container">
        <FloatLabel>
            <InputText @change="handleInput" id="pectoral" v-model="pectoral" />
            cm
            <label for="pectoral">Pectoral</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="abdominal" v-model="abdominal" />
            cm
            <label for="abdominal">Abdominal</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="thigh" v-model="thigh" />
            cm
            <label for="thigh">Thigh</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="triceps" v-model="triceps" />
            cm
            <label for="triceps">Triceps</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="suprailiac" v-model="suprailiac" />
            cm
            <label for="suprailiac">Suprailiac</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="subscapular" v-model="subscapular" />
            cm
            <label for="subscapular">Subscapular</label>
        </FloatLabel>
        <FloatLabel>
            <InputText @change="handleInput" id="midaxillary" v-model="midaxillary" />
            cm
            <label for="midaxillary">Midaxillary</label>
        </FloatLabel>
    </div>
</template>

<script setup lang="ts">
import FloatLabel from "primevue/floatlabel"
import InputText from "primevue/inputtext"
import { ref } from "vue"
const map = new Map<string, number>()
const pectoral = ref(null)
const abdominal = ref(null)
const thigh = ref(null)
const triceps = ref(null)
const suprailiac = ref(null)
const subscapular = ref(null)
const midaxillary = ref(null)

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
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-gap: 1.5em 0;
    background-color: var(--main-tertiary-color);
    padding: 3em 2em 2em 2em;
    border-radius: 10px;
    justify-items: center;
    align-items: center;
}

.container > FloatLabel:last-child {
    grid-column: span 3;
    text-align: center;
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
