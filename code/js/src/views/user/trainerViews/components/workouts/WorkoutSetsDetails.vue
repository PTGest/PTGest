<template>
    <div class="details">
        <SetDetailsContainer v-bind="set" v-for="set in props.sets" :set="set" :is-session-details="props.isSessionDetails" />
    </div>
</template>

<script setup lang="ts">
import SetDetails from "@/views/user/trainerViews/models/sets/SetDetails.ts"
import SetDetailsContainer from "@/views/user/trainerViews/components/workouts/SetDetailsContainer.vue"

const props = defineProps<{
    sets: SetDetails[]
    isSessionDetails: boolean
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
