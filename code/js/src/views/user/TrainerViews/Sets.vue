<template>
   <SetsContainer :sets="sets.sets"></SetsContainer>
</template>

<script setup lang="ts">
import Sets from '../TrainerViews/models/Sets.ts';
import SetsContainer from "../../../views/user/TrainerViews/components/SetsContainer.vue";
import getSets from "../../../services/TrainerServices/getSets.ts";
import {Ref, ref} from "vue";


const sets : Ref<Sets> = ref({
    sets: [],
    nOfSets: 0
});

(async () => {
    sets.value =  await getSets([]);

})();
</script>


<style scoped>
</style>
