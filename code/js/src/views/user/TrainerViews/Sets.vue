<template>
   <SetsContainer :sets="sets.sets"></SetsContainer>

</template>

<script setup lang="ts">
import Sets from '../TrainerViews/models/Sets.ts';
import Set from "../../../views/user/TrainerViews/models/Set.ts";
import Exercise from "../../../views/user/TrainerViews/models/Exercise.ts";
import SetsContainer from "../../../views/user/TrainerViews/components/SetsContainer.vue";

const sets = new Sets(
    [
        new Set(1, 'PushUps Set', 'Fazer agachamentos com pesos em ambos os lados', '3x10', '3x10',new Exercise(1, 'Exercise 1', 'This is exercise 1')),
        new Set(2, 'Nigga Set', 'Fazer agachamentos a triplicar', '3x10', '3x10',new Exercise(1, 'Exercise 1', 'This is exercise 1')),
        new Set(3, 'Habibi Set', 'Nada', '3x10', '3x10',new Exercise(1, 'Exercise 1', 'This is exercise 1')),
    ],
    3
)

// (async () => {
//     const response = await fetch('http://localhost:3000/api/sets');
//     sets.value = await response.json();
// })();
</script>


<style scoped>
</style>
