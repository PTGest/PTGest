<template>
    <div class="trainer">
        <div class="text">
            <h4>{{props.trainer.name}}</h4>
        </div>
        <div class="text">
            <p>Capacity</p>
            <input v-model="capacity" :class="isDisabled ? 'capacity' : 'edit-capacity'"
                   :disabled="isDisabled"
            />
        </div>
        <div class="text">
            <p>Trainees</p>
            <input v-model="assignedTrainees" class="capacity"
                   :placeholder="`${props.trainer.assignedTrainees}`" disabled
            />
        </div>
        <font-awesome-icon v-if="isDisabled" :icon="faPen" @click="editCapacity" class="icon"></font-awesome-icon>
        <font-awesome-icon v-if="!isDisabled" :icon="faCheck" @click="updateCapacity" class="icon"></font-awesome-icon>
        <font-awesome-icon :icon="faX" class="deleteIcon"></font-awesome-icon>

    </div>
</template>

<script setup lang="ts">
import Trainer from "../../../../views/user/CompaniesViews/models/Trainer.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faCheck, faPen, faX} from "@fortawesome/free-solid-svg-icons";
import {ref} from "vue";
import changeTrainerCapacity from "../../../../services/companyServices/changeTrainerCapacity.ts";

const isDisabled = ref(true)
const props = defineProps<{
    trainer: Trainer
}>()
const capacity = ref(props.trainer.capacity)
const assignedTrainees = ref(props.trainer.assignedTrainees)
const editCapacity = () => {
    isDisabled.value = !isDisabled.value
}

const updateCapacity = () => {
    changeTrainerCapacity(props.trainer.id, capacity.value)
    editCapacity()
}

</script>

<style scoped>
.trainer{
    width: 20em;
    height: 2em;
    display: flex;
    justify-content: center;
    align-content: center;
    padding: 1em 0.5em 1em 0.5em;
    border-radius: 10px;
    margin: 0.5em;
    background-color: var(--light-blue);
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: bold;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
.icon{
    position: relative;
    top: 0.8em;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}
p, h6, h4, input{
    padding: 0;
    margin: 0;
}
h4{
    position: relative;
    left : -1em;
    text-shadow: var(--secundary-color) 2px 2px 2px;
}
p{
    font-size: 0.8em;
}
.text{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 0 0.5em 0 0.5em;
}
.capacity, .edit-capacity{
    width: 3em;
    height: 2em;
    font-size: 0.8em;
    border: 0;
    color: whitesmoke;
    background-color: var(--light-blue);
}
.edit-capacity{
    border: 1px solid whitesmoke;
    border-radius: 10px;
}
input{
    text-align: center;
}
::placeholder{
    text-align: center;
    color: whitesmoke;
}

.deleteIcon{
    position: relative;
    top: -0.5em;
    right: -0.7em;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}
</style>
