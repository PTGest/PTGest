<template>
   <SetsContainer :sets="sets.sets"></SetsContainer>
</template>

<script setup lang="ts">
import Sets from './models/sets/Sets.ts';
import SetsContainer from "./components/sets/SetsContainer.vue";
import getSets from "../../../services/TrainerServices/sets/getSets.ts";
import {Ref, ref} from "vue";


const sets : Ref<Sets> = ref({
    sets: [],
    nOfSets: 0
});

(async () => {
    sets.value =  await getSets([]);
    console.log(sets.value);

})();
</script>


<style scoped>
</style>
