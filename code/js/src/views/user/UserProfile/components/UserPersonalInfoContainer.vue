<template>
    <div class="user-info">
        <h1>Personal Info</h1>
        <div>
            <p>{{ props.email }}</p>
            <p>Phone: {{ props.phone }}</p>
            <p>Location: {{ props.location }}</p>
            <p>Age: {{ props.age }}</p>
            <p>Height: {{ props.height }}</p>
            <p>Weight: {{ props.weight }}</p>
            <p>BMI: {{ props.BMI }}</p>
        </div>
    </div>
</template>

<script setup lang="ts">
const props = defineProps({
    userName: { type: String, default: "" },
    email: { type: String, default: "" },
    phone: { type: String, default: "" },
    location: { type: String, default: "" },
    age: { type: Number, default: -1 },
    height: { type: Number, default: -1 },
    weight: { type: Number, default: -1 },
    BMI: { type: Number, default: -1 },
})
</script>

<style scoped>
.user-info {
    display: flex;
    flex-direction: column;
    padding: 2em;
    gap: 1em;
}

h1 {
    font-size: 2em;
    font-weight: bold;
    color: whitesmoke;
    text-align: center;
    margin: 0;
}
</style>
