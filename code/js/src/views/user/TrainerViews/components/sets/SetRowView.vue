<template>
    <div class="set-container">
        <div @click="openSetDetails" class="set-row">
            <div class="set-row-name">{{ props.set.name }}</div>
            <div class="set-row-info-container">
                <Textarea class="text-area" v-model="props.set.notes" rows="3" cols="30" disabled auto-resize/>
                <div class="set-row-info">{{ props.set.type }}</div>
            </div>
        </div>
        <LikeSet :set-id="props.set.id"></LikeSet>
    </div>
    <Divider />
</template>

<script setup lang="ts">
import Set from "../../models/sets/Set.ts";
import Divider from "primevue/divider";
import Textarea from "primevue/textarea";
import LikeSet from "./LikeSet.vue";
import SetDetails from "@/views/user/TrainerViews/components/sets/SetDetails.vue";
import {ref} from "vue";
import router from "@/plugins/router.ts";

const props = defineProps<{
    set: Set;
}>();

const openSetDetails = () => {
   router.push({name: 'setDetails', params: {setId: props.set.id}});
}


</script>


<style scoped>

.set-row{
    position : relative;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border-radius: 10px;
    gap: 1em;
}

.set-row:hover{
    cursor: pointer;
    border: 1px solid var(--button-border-color);
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
    border: 1px solid var(--main-secondary-color);
}

.text-area{
    max-height: 5em;
    overflow-y: auto!important;
}

.set-container{
    display: flex;
    flex-direction: row;
    gap: 1em;
    justify-content: space-between;
    align-items: center;
}
</style>
