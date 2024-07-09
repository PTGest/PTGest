<template>
   <div class="setFeedback">
       <font-awesome-icon class="x-icon" :icon="faX" @click="$emit('close')"/>
       Set Feedback
       <textarea v-model="feedback"></textarea>
       <Button @click="addSetFeedback" class="btn">Add Feedback</Button>
   </div>
</template>

<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faX} from "@fortawesome/free-solid-svg-icons";
import {addTrainerSessionsSetFeedback} from "@/services/TrainerServices/sessions/sessionServices.ts";
import {ref} from "vue";
import router from "@/plugins/router.ts";
import store from "@/store";

const feedback = ref("")
const props = defineProps<{
    setId: number
    setOrderId: number
}>()
const addSetFeedback = async () => {
    await addTrainerSessionsSetFeedback(feedback.value, store.getters.sessionDetails.id, props.setId ,props.setOrderId)
    console.log("Adding set feedback")
}

</script>

<style scoped>
.setFeedback{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 1em;
    background-color: var(--main-tertiary-color);
    font-family: 'Poppins', sans-serif;
    padding: 1em;
}
textarea{
    resize: none;
    width: 90%;
    height: 70%;
    border: none;
    background-color: var(--main-primary-color);
    font-family: 'Poppins', sans-serif;
    border-radius: 10px;
    color: whitesmoke;
    font-size: 1.3rem;
    outline: none;
    padding: 1em;
}
.btn{
    margin-top: 1em;
    background-color: var(--main-primary-color);
    color: white;
    border-radius: 5px;
    padding: 0.5em;
    cursor: pointer;
    transition: 0.2s ease-in;
    border: none;
}
.btn:hover{
    transition: 0.2s ease-out;
    background-color: var(--main-secondary-color);
}

.x-icon{
    position: relative;
    top: 0em;
    left: 50%;
    color: white;
    font-size: 1rem;
    cursor: pointer;
    margin-right: 0.5em;
}
</style>
