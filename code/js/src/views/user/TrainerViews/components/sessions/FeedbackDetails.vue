<template>
    <div class="feedback-details">
        <font-awesome-icon class="arrow-icon" @click="$emit('close', false)" :icon="faX" />
        <p>{{ "Feedback " + dateFormatter(props.feedback.date.toString()) }}</p>
        <p>{{ "Added by " + props.feedback.source }}</p>
        <font-awesome-icon @click="openEdit" v-if="RBAC.getUserRole().includes(props.feedback.source) && !isEdit" class="pen-icon" :icon="faPen" />
        <font-awesome-icon @click="editFeedback" v-else class="pen-icon" :icon="faCheck" />
        <textarea v-model="feedback" :readonly="!isEdit"></textarea>
    </div>
</template>

<script setup lang="ts">
import SessionFeedback from "@/views/user/TrainerViews/models/sessions/SessionFeedback.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faCheck, faPen, faX } from "@fortawesome/free-solid-svg-icons"
import dateFormatter from "../../../../../services/utils/dateUtils/dateFormatter.ts"
import RBAC from "@/services/utils/RBAC/RBAC.ts"
import { ref } from "vue"
import { editTrainerSessionsFeedback } from "@/services/TrainerServices/sessions/sessionServices.ts"
import store from "@/store"
const isEdit = ref(false)
const props = defineProps<{
    feedback: SessionFeedback
}>()
const feedback = ref(props.feedback.feedback)

const openEdit = () => {
    isEdit.value = !isEdit.value
}

const editFeedback = async () => {
    await editTrainerSessionsFeedback(feedback.value, store.getters.sessionDetails.id, props.feedback.id)
    isEdit.value = !isEdit.value
}
</script>

<style scoped>
.feedback-details {
    position: absolute;
    top: 0;
    left: -155%;
    width: 30em;
    height: 40em;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    gap: 5em;
    padding: 2em;
    border-radius: 10px;
    background-color: var(--main-primary-color);
}
textarea {
    width: 100%;
    height: 20em;
    background-color: whitesmoke;
    border-radius: 10px;
    padding: 1em;
    font-size: 1.2em;
    font-family: "Poppins", sans-serif;
    color: var(--main-primary-color);
    border: none;
    resize: none;
    outline: none;
}
.arrow-icon {
    position: absolute;
    top: 1em;
    right: 1em;
    cursor: pointer;
    color: whitesmoke;
    font-size: 1.2em;
    transition: 0.2s ease-in;
}
p {
    font-size: 1em;
    color: whitesmoke;
    margin: 0;
}
.pen-icon {
    position: absolute;
    bottom: 26em;
    right: 3em;
    cursor: pointer;
    color: whitesmoke;
    font-size: 1em;
    transition: 0.2s ease-in;
}
</style>
