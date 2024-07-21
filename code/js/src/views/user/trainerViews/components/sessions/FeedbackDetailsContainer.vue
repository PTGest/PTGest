<template>
    <div class="feedback-details-container" v-if="!isDetailsOpen">
        <p @click="openDetails">
            {{ "Feedback " + dateFormatter(props.feedback.date.toString()) }}
        </p>
    </div>
    <FeedbackDetails @close="closeDetails($event)" v-else :feedback="props.feedback"></FeedbackDetails>
</template>

<script setup lang="ts">
import SessionFeedback from "@/views/user/trainerViews/models/sessions/SessionFeedback.ts"
import dateFormatter from "../../../../../services/utils/dateUtils/dateFormatter.ts"
import { ref } from "vue"
import FeedbackDetails from "@/views/user/trainerViews/components/sessions/FeedbackDetails.vue"

const props = defineProps<{
    feedback: SessionFeedback
}>()
const emits = defineEmits(["detailsOpen"])
const isDetailsOpen = ref(false)

const openDetails = () => {
    isDetailsOpen.value = true
    emits("detailsOpen", true)
}
const closeDetails = (value: boolean) => {
    isDetailsOpen.value = value
    emits("detailsOpen", false)
}
</script>

<style scoped>
input {
    width: 100%;
    height: 100px;
    border: 1px solid black;
    border-radius: 5px;
    padding: 1em;
    margin: 1em;
}
.feedback-details-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 1em;
    border-radius: 5px;
    background-color: var(--main-primary-color);
}
.feedback-details-container p {
    cursor: pointer;
    font-size: 1em;
    color: whitesmoke;
    transition: 0.2s ease-in;
    margin: 0;
}
.feedback-details-container p:hover {
    color: var(--main-secondary-color);
    transition: 0.2s ease-out;
}
</style>
