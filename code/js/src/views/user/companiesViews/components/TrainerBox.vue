<template>
    <div class="trainer">
        <div class="text">
            <h4>{{ props.trainer.name }}</h4>
        </div>
        <div class="text">
            <p>Capacity</p>
            <input v-model="capacity" :class="isDisabled ? 'input' : 'edit-input'" :disabled="isDisabled" />
        </div>
        <div class="text">
            <p>Trainees</p>
            <input v-model="assignedTrainees" class="input" :placeholder="`${props.trainer.assignedTrainees}`" disabled />
        </div>
        <div v-if="!(isAssignTrainer || isReassignTrainer)">
            <font-awesome-icon v-if="isDisabled" :icon="faPen" @click="editCapacity" class="icon"></font-awesome-icon>
            <font-awesome-icon v-if="!isDisabled" :icon="faCheck" @click="updateCapacity" class="icon"></font-awesome-icon>
        </div>

        <AssignTrainerButton v-if="isAssignTrainer" :trainer-id="trainer.id" :trainee-id="$route.params.traineeId"></AssignTrainerButton>
        <AssignTrainerButton v-if="isReassignTrainer" :trainer-id="trainer.id" :trainee-id="$route.params.traineeId" is-reassign-trainer></AssignTrainerButton>
    </div>
</template>

<script setup lang="ts">
import Trainer from "../models/Trainer.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faCheck, faPen} from "@fortawesome/free-solid-svg-icons"
import { ref } from "vue"

import {changeTrainerCapacity} from "@/services/companyServices/companyServices.ts";
import AssignTrainerButton from "@/views/user/companiesViews/components/AssignTrainerButton.vue";

const isDisabled = ref(true)
const props = defineProps<{
    trainer: Trainer
    isAssignTrainer: boolean
    isReassignTrainer: boolean
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
.trainer {
    position: relative;
    width: 20em;
    height: 3em;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 1em 0.5em 1em 1em;
    border-radius: 10px;
    margin: 0.5em;
    background-color: var(--main-primary-color);
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: bold;
    border: 1px solid var(--main-secondary-color);
}
.icon {
    position: relative;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}
p,
h6,
h4,
input {
    padding: 0;
    margin: 0;
}
h4 {
    position: relative;
    left: -1em;
    text-shadow: var(--main-secondary-color) 2px 2px 2px;
}
p {
    font-size: 0.8em;
}
.text {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 0 0.5em 0 0.5em;
}
.input,
.edit-input {
    width: 3em;
    height: 2em;
    font-size: 0.8em;
    border: 0;
    color: whitesmoke;
    background-color: transparent;
}
.edit-input {
    border: 1px solid whitesmoke;
    border-radius: 10px;
}
input {
    text-align: center;
}
::placeholder {
    text-align: center;
    color: whitesmoke;
}

.delete-icon {
    position: absolute;
    top: 0.3em;
    right: -0.5em;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}

@media screen and (max-width: 659px) {
    .trainer {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 1em 0.5em 1em 1em;
        border-radius: 10px;
        margin: 0.5em;
        color: whitesmoke;
        font-size: 1em;
        font-weight: bold;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
    .icon {
        position: relative;
        top: -0.1em;
        color: whitesmoke;
        font-size: 0.8em;
        margin: 0 1em 0 1em;
        cursor: pointer;
    }
    p,
    h6,
    h4,
    input {
        padding: 0;
        margin: 0;
    }
    h4 {
        position: relative;
        left: -1em;
        text-shadow: var(--main-secondary-color) 2px 2px 2px;
    }
    p {
        font-size: 0.8em;
    }
    .text {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }
    .capacity,
    .edit-capacity {
        width: 3em;
        height: 2em;
        font-size: 0.8em;
        border: 0;
        color: whitesmoke;
        background-color: var(--light-blue);
    }
    .edit-capacity {
        border: 1px solid whitesmoke;
        border-radius: 10px;
    }
    input {
        text-align: center;
    }
    ::placeholder {
        text-align: center;
        color: whitesmoke;
    }

    .delete-icon {
        position: relative;
        top: -0.5em;
        right: -1em;
        color: whitesmoke;
        font-size: 0.8em;
        margin: 0 1em 0 1em;
        cursor: pointer;
    }
}
</style>
