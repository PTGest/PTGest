<template>
    <default-button class="assign-button" :display-text="text" :click-handler="handleAssignTrainer" />
</template>

<script setup lang="ts">
import DefaultButton from "../../../../components/utils/DefaultButton.vue"

import router from "@/plugins/router.ts";
import {assignOrReassignTrainer} from "@/services/companyServices/companyServices.ts";

const props = defineProps<{
    trainerId: string
    traineeId: string
    isReassignTrainer: boolean
}>()

const text = props.isReassignTrainer ? "Reassign Trainer" : "Assign Trainer"
const handleAssignTrainer = async () => {
    if (props.isReassignTrainer) {
        console.log("reassigning trainer")
        console.log(props.trainerId)
        console.log(props.traineeId)
        await assignOrReassignTrainer(props.traineeId, props.trainerId, true)
    } else {
        await assignOrReassignTrainer(props.traineeId, props.trainerId, false)
    }
    router.back()
}
</script>

<style scoped>
.assign-button {
    width: 6em;
    height: 4em;
    font-size: 0.5em;
    margin: 0;
}
</style>
