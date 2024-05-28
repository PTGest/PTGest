<template>
    <div v-if="!isAddSetOpen" class="sets-container">

        <div class="title-row">
            Sets
            <button @click="openAddSet" class="add-btn">
                <FontAwesomeIcon :icon="faPlus"/>
                Add Set
            </button>
        </div>
        <div class="label-row">
            <div>Name</div>
            <div>Notes</div>
            <div>Type</div>
        </div>
        <Divider/>
        <SetRowView v-for="set in props.sets" :key="set.id" :set="set"/>
    </div>
    <AddSet v-if="isAddSetOpen" @close="openAddSet"/>
</template>

<script setup lang="ts">
import Divider from "primevue/divider";
import Set from "../models/Set.ts";
import SetRowView from "../../../../views/user/TrainerViews/components/SetRowView.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {ref} from "vue";
import AddSet from "@/views/user/TrainerViews/components/AddSet.vue";
const props = defineProps<{
   sets: Set[];
}>();
const isAddSetOpen = ref(false);

const openAddSet = () => {
    isAddSetOpen.value = !isAddSetOpen.value;
    console.log("Add Set");
}
</script>


<style scoped>
.title-row{
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    font-size: 2rem;
    font-weight: bold;
    width: 100%;
}

.sets-container{
    position : relative;
    display: flex;
    flex-direction: column;
    padding: 1rem;
    background-color: var(--main-primary-color);
    color : whitesmoke;
    border-radius: 10px;
    width: 100%;
}

.label-row{
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    margin-top: 1em;
    gap: 8em;
    font-size: 1em;
}

.add-btn{
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 0.5em;
    border-radius: 5px;
    background-color: var(--sign-up-blue);
    color: whitesmoke;
    cursor: pointer;
    font-size: 15px;
}
</style>
