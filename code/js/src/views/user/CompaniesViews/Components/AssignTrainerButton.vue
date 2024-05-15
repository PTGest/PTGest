<template>
    <default-button class="assign-button" display-text="Assign Trainer" :click-handler="handleAssignTrainer"/>
</template>

<script setup lang="ts">

import DefaultButton from "../../../../components/utils/DefaultButton.vue";
import assignTrainer from "../../../../services/companyServices/assignTrainer.ts";

const props = defineProps<{
    trainerId : string
    traineeId : string
}>()

const handleAssignTrainer = async () => {
    console.log("Assign Trainer")
    console.log(props.trainerId)
    console.log(props.traineeId)
    await assignTrainer(props.traineeId,props.trainerId)
}

</script>


<style scoped>

.assign-button{
    width: 6em;
    height: 4em;
    font-size: 0.5em;
    margin: 0;
}


</style>
