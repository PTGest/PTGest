<template>
    <div class="set-feedback-row">
        <div class="header-row">
            {{`Made by ${props.setFeedback.source} on ${dateFormatter(props.setFeedback.date.toString())}`}}
            <font-awesome-icon :icon="faPen" class="icon" v-if="!isEdit && RBAC.getUserRole().includes(props.setFeedback.source)" @click="isEdit = true"/>
            <font-awesome-icon :icon="faCheck" class="icon" v-if="isEdit" @click="editFeedback"/>
        </div>
        <Textarea :readonly="!isEdit" :class="[isEdit ? 'edit-feedback' : 'feedbacks']" v-model="feedback" rows="5" cols="40"></Textarea>
    </div>
</template>

<script setup lang="ts">
import Textarea from "primevue/textarea";
import SetSessionFeedback from "@/views/user/TrainerViews/models/sessions/SetSessionFeedbacks.ts";
import {ref} from "vue";
import dateFormatter from "../../../../../services/utils/dateUtils/dateFormatter.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faCheck, faPen} from "@fortawesome/free-solid-svg-icons";
import RBAC from "@/services/utils/RBAC/RBAC.ts";
import {editTrainerSetSessionsFeedback} from "@/services/TrainerServices/sessions/sessionServices.ts";
import store from "@/store";

const props = defineProps<{
    setFeedback: SetSessionFeedback
}>()
const isEdit = ref(false);
const feedback = ref(props.setFeedback.feedback)

const editFeedback = async() => {
    await editTrainerSetSessionsFeedback(feedback.value, store.getters.sessionDetails.id,props.setFeedback.id,
        props.setFeedback.setId, props.setFeedback.setOrderId);
    isEdit.value = false;
}
</script>

<style scoped>
Textarea, .edit-feedback{
    background-color: var(--main-primary-color);
    color: white;
    outline: none;
    border: none;
    border-radius: 5px;
    width: 99%;
    resize: none;
}
.edit-feedback{
    border: 1px solid whitesmoke;
}
.set-feedback-row{
    width: 100%;
    font-family: 'Poppins', sans-serif;
    background-color: var(--main-primary-color);
    border-radius: 5px;
}
.icon{
    color: whitesmoke;
    font-size: 1rem;
    padding: 0.5em;
    cursor: pointer;
}
.header-row{
    display: flex;
    padding: 0.5em;
    width: 100%;
    justify-content: space-between;
    align-items: center;

}
</style>
