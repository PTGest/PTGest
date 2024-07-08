<template>
    <div class="feedback-details">
        <font-awesome-icon class="arrow-icon" @click="$emit('close', false)" :icon="faX" />
        <p>{{'Feedback ' + dateFormatter(props.feedback.date.toString())}}</p>
        <p>{{'Added by ' + props.feedback.source}}</p>
        <textarea v-model="props.feedback.feedback" readonly></textarea>
    </div>
</template>

<script setup lang="ts">
import SessionFeedback from "@/views/user/TrainerViews/models/sessions/SessionFeedback.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faX} from "@fortawesome/free-solid-svg-icons";
import dateFormatter from "../../../../../services/utils/dateUtils/dateFormatter.ts";

const props = defineProps<{
    feedback: SessionFeedback
}>()
</script>

<style scoped>
.feedback-details{
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
textarea{
    width: 100%;
    height: 20em;
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
.arrow-icon{
    position: absolute;
    top : 1em;
    right: 1em;
    cursor: pointer;
    color: whitesmoke;
    font-size: 1.2em;
    transition: 0.2s ease-in;
}
p{
    font-size: 1em;
    color: whitesmoke;
    margin: 0;
}
</style>
