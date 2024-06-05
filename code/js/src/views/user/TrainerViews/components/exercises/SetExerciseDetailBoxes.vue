<template>
    <div class="boxes-container">
        <div>
            <label> Weight </label>
            <label class="kilos-text"> kilos </label>
        </div>
        <input v-model="weight" @change="handleDetails('weight', weight)" class="box" />
        <label> Reps </label>
        <input v-model="reps" @change="handleDetails('reps', reps)" class="box" />
    </div>
</template>

<script setup lang="ts">
import {ref} from "vue";

const reps = ref(0);
const weight = ref(0);
const details = ref(new Map());
const emit = defineEmits(['details']);

const handleDetails = (detailKey:string, value: number) => {
    switch (detailKey) {
        case 'weight':
            details.value.set('weight', value);
           emit('details', details.value);
            break;
        case 'reps':
            details.value.set('reps', value);
            emit('details', details.value);
            break;
        default:
            break;
    }
};


</script>

<style scoped>
.boxes-container{
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    margin-top: 1rem;
    gap: 0.5rem;
}

.box{
    width:3em;
    height: 3em;
    border-radius: 10px;
    border: 1px solid var(--main-secondary-color);
    color: whitesmoke;
    padding: 0.5em;
    background-color: var(--main-primary-color);
}

.kilos-text{
    font-size: 13px;

}

input{
    text-align:center;
}
</style>
