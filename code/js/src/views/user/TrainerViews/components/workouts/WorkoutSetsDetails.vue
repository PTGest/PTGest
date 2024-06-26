<template>
    <div class="details">
        <SetDetailsContainer v-bind="set" v-for="set in props.sets" :set="set" />
    </div>
</template>

<script setup lang="ts">
import SetDetails from "@/views/user/TrainerViews/models/sets/SetDetails.ts"
import SetDetailsContainer from "@/views/user/TrainerViews/components/workouts/SetDetailsContainer.vue"

const props = defineProps<{
    sets: SetDetails[]
}>()
</script>

<style scoped>
.details {
    display: flex;
    flex-direction: column;
    justify-content: center;
    width: 100%;
    max-height: 90%;
    align-items: center;
    overflow-y: scroll;
}
</style>
