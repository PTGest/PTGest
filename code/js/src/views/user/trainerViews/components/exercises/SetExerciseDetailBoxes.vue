<template>
    <div class="boxes-container">

        <MultiSelect v-model="chosenDetails" :options="options" optionLabel="name" placeholder="Select details"
                     :maxSelectedLabels="3" class="w-full md:w-20rem" />

        <div v-if="chosenDetails.filter(obj => obj.name == 'Weight').length != 0">
            <label> Weight </label>
            <input v-model="weight" @change="handleDetails('weight', weight)" class="box" />
            <label class="kilos-text"> kilos </label>
        </div>

        <div v-if="chosenDetails.filter(obj => obj.name == 'Reps').length != 0">
            <label> Reps </label>
            <input v-model="reps" @change="handleDetails('reps', reps)" class="box" />
        </div>

        <div v-if="chosenDetails.filter(obj => obj.name == 'Duration').length != 0">
            <label> Duration </label>
            <input v-model="duration" @change="handleDetails('duration', duration)" class="box" />
            <label class="kilos-text"> seconds </label>
        </div>

        <div v-if="chosenDetails.filter(obj => obj.name == 'Distance').length != 0">
            <label> Distance </label>
            <input v-model="distance" @change="handleDetails('distance', distance)" class="box" />
            <label class="kilos-text"> meters </label>
        </div>
        <div v-if="chosenDetails.filter(obj => obj.name == 'Speed').length != 0">
            <label> Speed </label>
            <input v-model="speed" @change="handleDetails('speed', speed)" class="box" />
        </div>

        <div v-if="chosenDetails.filter(obj => obj.name == 'RestTime').length != 0">
            <label> RestTime </label>
            <input v-model="restTime" @change="handleDetails('restTime', restTime)" class="box" />
            <label class="kilos-text"> seconds </label>
        </div>

        <div v-if="chosenDetails.filter(obj => obj.name == 'Resistance').length != 0">
            <label> Resistance </label>
            <input v-model="resistance" @change="handleDetails('resistance', resistance)" class="box" />
        </div>
    </div>
</template>

<script setup lang="ts">
import {ref} from "vue"
import MultiSelect from "primevue/multiselect";

const reps = ref(0)
const weight = ref(0)
const duration = ref(0)
const distance = ref(0)
const speed = ref(0)
const restTime = ref(0)
const resistance = ref(0)
const details = ref(new Map())
const chosenDetails = ref([])
const options = [{name: "Weight"}, {name: "Reps"}, {name: "Duration"}, {name: "Distance"}, {name: "Speed"}, {name: "RestTime"}, {name: "Resistance"}]
const emit = defineEmits(["details"])


const handleDetails = (detailKey: string, value: number) => {
    switch (detailKey) {
        case "weight":
            details.value.set("WEIGHT", value)
            emit("details", details.value)
            break
        case "reps":
            details.value.set("REPS", value)
            emit("details", details.value)
            break
        case "duration":
                details.value.set("DURATION", value)
            emit("details", details.value)
            break
        case "distance":
            details.value.set("DISTANCE", value)
            emit("details", details.value)
            break
        case "speed":
            details.value.set("SPEED", value)
            emit("details", details.value)
            break
        case "restTime":
            details.value.set("REST_TIME", value)
            emit("details", details.value)
            break
        case "resistance":
            details.value.set("RESISTANCE", value)
            emit("details", details.value)
            break
        default:
            break
    }
}
</script>

<style scoped>
.boxes-container {
    display: flex;
    width: 100%;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-top: 1rem;
    gap: 0.5rem;
}

.box {
    width: 3em;
    height: 3em;
    border-radius: 10px;
    border: 1px solid var(--main-secondary-color);
    color: whitesmoke;
    padding: 0.5em;
    background-color: var(--main-primary-color);
}

.kilos-text {
    font-size: 13px;
}

input {
    text-align: center;
}
</style>
