<template>
        <div class="container" v-if="props.gender == 'MALE'">
            <FloatLabel>
                <InputText @change="handleInput" id="pectoral" v-model="pectoral" />
                <label for="pectoral">Pectoral</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="pectoral" v-model="abdominal" />
                <label for="pectoral">Abdominal</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="pectoral" v-model="thigh" />
                <label for="pectoral">Thigh</label>
            </FloatLabel>
        </div>
        <div class="container" v-else>
            <FloatLabel>
                <InputText @change="handleInput" id="pectoral" v-model="tricep" />
                <label for="pectoral">Tricep</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="pectoral" v-model="suprailiac" />
                <label for="pectoral">Suprailiac</label>
            </FloatLabel>
            <FloatLabel>
                <InputText @change="handleInput" id="pectoral" v-model="thigh" />
                <label for="pectoral">Thigh</label>
            </FloatLabel>
        </div>

</template>

<script setup lang="ts">
import FloatLabel from "primevue/floatlabel";
import InputText from "primevue/inputtext";
import {ref} from "vue";
const props = defineProps<{
    gender: string
}>();

console.log(props.gender);

const map = new Map<string, number>();

const pectoral = ref(null);
const abdominal = ref(null);
const thigh = ref(null);
const tricep = ref(null);
const suprailiac = ref(null);

const emits = defineEmits(['skinFoldData']);
const handleInput = (e: any) => {
    map.set(e.target.id, e.target.value);
    emits('skinFoldData', map);
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
