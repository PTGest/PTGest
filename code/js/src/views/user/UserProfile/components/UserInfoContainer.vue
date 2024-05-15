<template>
    <div class="user-info">
        <div class="username-text">{{ props.userName }}</div>
        <p>Personal Trainer:</p>
    </div>
</template>

<script setup lang="ts">
const props = defineProps({
    userName: { type: String, default: "" },
    email: { type: String, default: "" },
    phone: { type: String, default: "" },
    age: { type: Number, default: -1 },
    height: { type: Number, default: -1 },
    weight: { type: Number, default: -1 },
    BMI: { type: Number, default: -1 },
})
</script>

<style scoped>
.username-text {
    font-size: 1.5em;
    font-weight: bold;
    color: whitesmoke;
    margin: 1em;
    text-align: center;
}

.user-info {
    width: 80%;
    height: 89%;
    padding: 1em;
    color: whitesmoke;
    font-size: 1.3em;
}
</style>
