<template>
    <div class="image-container">
        <img class="image" :src="props.src" alt="User Image" />
    </div>
</template>

<script setup lang="ts">
const props = defineProps({
    src: String,
})
</script>

<style scoped>
.image {
    width: 10em;
    height: 10em;
    border-radius: 50%;
}

.image-container {
    display: flex;
    flex-direction: column;
    width: 10em;
    height: 10em;
    align-items: center;
    justify-content: center;
    background-color: var(--primary-color);
    border-radius: 50%;
    padding: 1em;
    margin: 1em;
    font-weight: bold;
    color: whitesmoke;
}
</style>
