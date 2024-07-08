<template>
    <div class="add-feedback-container">
        <h2>Add Feedback</h2>
        <font-awesome-icon class="arrow-icon" @click="$emit('close', false)" :icon="faArrowLeft" />
        <textarea v-model="feedback" placeholder="Enter Feedback"/>
        <Button @click="addFeedback" class="add-btn">Add Feedback</Button>
    </div>
</template>

<script setup lang="ts">
import Button from "primevue/button";
import {ref} from "vue";
import {faArrowLeft} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {addTrainerSessionsFeedback} from "@/services/TrainerServices/sessions/sessionServices.ts";
import router from "@/plugins/router.ts";
import store from "@/store";
import RBAC from "@/services/utils/RBAC/RBAC.ts";
import {addTraineeSessionsFeedback} from "@/services/TraineeServices/TraineeServices.ts";

const feedback = ref("")

const addFeedback = async() => {
    if(RBAC.isTrainer() || RBAC.isHiredTrainer()) {
        await addTrainerSessionsFeedback(feedback.value, store.getters.sessionDetails.id)
        router.go(0);
    }else{
        await addTraineeSessionsFeedback(feedback.value, store.getters.sessionDetails.id)
        router.go(0);
    }
}

</script>



<style scoped>
.add-feedback-container{
    display: flex;
    width: 20em;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 1em;
    padding: 1em;
    border-radius: 5px;
    background-color: var(--main-primary-color);

}
textarea{
    width: 100%;
    height: 10em;
    background-color: whitesmoke;
    border-radius: 10px;
    padding: 1em;
    font-size: 1.2em;
    font-family: 'Poppins', sans-serif;
    color: var(--main-primary-color);
    border: none;
    resize: none;
    outline: none;
}
::placeholder{
    justify-self: start;
    font-family: 'Poppins', sans-serif;
    color: var(--main-primary-color);
}
::-webkit-scrollbar-thumb {
    background-color: var(--main-tertiary-color);
    border-radius: 10px;
}

.arrow-icon{
    position: absolute;
    top: 1em;
    left: 1em;
    cursor: pointer;
}

.add-btn{
    background-color: var(--main-secondary-color);
    color: whitesmoke;
    padding: 0.5em;
    border-radius: 5px;
    font-size: 1.2em;
    cursor: pointer;
    transition: 0.2s ease-in;
    border: 1px solid var(--main-secondary-color);
}

.add-btn:hover{
    border: 1px solid var(--button-border-color);
    background-color: var(--main-tertiary-color);
    transition: 0.2s ease-out;
}
</style>
