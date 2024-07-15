<template>
    <div v-if="isLoading">
        <ProgressSpinner />
    </div>
    <div v-else>
        <SetsContainer :sets="sets.sets"></SetsContainer>
    </div>
</template>

<script setup lang="ts">
import ProgressSpinner from "primevue/progressspinner"
import SetsContainer from "./components/sets/SetsContainer.vue"
import { getSets } from "@/services/TrainerServices/sets/setServices.ts"
import { Ref, ref, onMounted } from "vue"
import Sets from "@/views/user/TrainerViews/models/sets/Sets.ts"

const sets: Ref<Sets> = ref(new Sets())
const isLoading = ref(true)

onMounted(async () => {
    sets.value = await getSets(null)
    isLoading.value = false
    console.log(sets.value)
})
</script>

<style scoped></style>
