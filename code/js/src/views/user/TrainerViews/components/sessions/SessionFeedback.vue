<template>
    <div v-if="!isAddFeedbackOpen" class="feedback-container">
        <h2>Session Feedback</h2>
        <FeedbackDetailsContainer @detailsOpen="isDetailsOpen = $event" v-for="feedback in sessionFeedback" :feedback="feedback"></FeedbackDetailsContainer>
        <Button v-if="!isDetailsOpen" class="add-btn" @click="addFeedback"> Add Feedback </Button>
    </div>
    <AddFeedbackView @close="isAddFeedbackOpen = $event" v-else></AddFeedbackView>
</template>

<script setup lang="ts">
import Button from "primevue/button";
import SessionFeedback from "@/views/user/TrainerViews/models/sessions/SessionFeedback.ts";
import FeedbackDetailsContainer from "@/views/user/TrainerViews/components/sessions/FeedbackDetailsContainer.vue";
import {ref} from "vue";
import AddFeedbackView from "@/views/user/TrainerViews/components/sessions/AddFeedback.vue";
import store from "@/store";

const isAddFeedbackOpen = ref(false);
const isDetailsOpen = ref(false);
const sessionFeedback : SessionFeedback= store.getters.sessionDetails.feedbacks;

console.log("Session Feedback", sessionFeedback)
const addFeedback = () => {
  isAddFeedbackOpen.value = true;
}

</script>

<style scoped>
.feedback-container{
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
.add-btn{
    background-color: var(--main-tertiary-color);
    color: whitesmoke;
    padding: 0.5em;
    border-radius: 5px;
    font-size: 1.2em;
    cursor: pointer;
    transition: 0.2s ease-in;
    border: 1px solid var(--main-tertiary-color);
}

.add-btn:hover{
    border: 1px solid var(--button-border-color);
    background-color: var(--main-secondary-color);
    transition: 0.2s ease-out;
}
</style>
