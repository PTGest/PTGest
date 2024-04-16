<template>
    <div>
        <h1>Error</h1>
        <div class="error-container">
            <div class="error-message">{{ store.state.errorType.message }}</div>
            <div class="error-type">
                More Info
                <a :href="type">here</a>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useStore } from "vuex"

let store = useStore()
let type = store.state.errorType.type

console.log("Error Page")
console.log(store.state.errorType.type)
</script>

<style scoped></style>
