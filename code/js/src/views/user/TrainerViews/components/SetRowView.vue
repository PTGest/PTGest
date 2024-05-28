<template>
    <div class="set-row">
        <div class="set-row-name">{{ props.set.name }}</div>
       <div class="set-row-info-container">
           <Textarea v-model="props.set.notes" rows="3" cols="30" disabled auto-resize/>
           <div class="set-row-info">{{ props.set.type }}</div>
           <LikeSet :set-id="props.set.id"></LikeSet>
       </div>
    </div>
    <Divider />
</template>

<script setup lang="ts">
import Set from "../models/Set.ts";
import Divider from "primevue/divider";
import Textarea from "primevue/textarea";
import LikeSet from "../../../../views/user/TrainerViews/components/LikeSet.vue";

const props = defineProps<{
    set: Set;
}>();

</script>


<style scoped>

.set-row{
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    gap: 1em;

}

.set-row-name{
    display: flex;
    flex-direction: row;
    justify-content: center;
    font-size: 1.5em;
}

.set-row-info-container{
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 20em;
    gap : 1em;
}

:global(.p-inputtextarea){
    background-color: var(--main-primary-color);
    color: whitesmoke;
    border: 1px solid var(--main-secundary-color);
}

</style>
