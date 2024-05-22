<template>
    <div class="main-text-container">
        <h1 class="main-text">Stay fit and healthy with our custom exercise routines</h1>
        <div class="text-row">
            <div class="sub-text">Our experienced trainers will work with you to create a personalized workout plan that fits your schedule. fitness level, and objectives.</div>
            <RouterLink :to="{ name: 'signup' }" class="router-link">Start Training</RouterLink>
        </div>
    </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.main-text-container {
    display: flex;
    width: 35em;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    z-index: 99;
}

.main-text {
    text-align: left;
    font-family: Poppins, sans-serif;
    font-size: 3.5em;
}

.text-row {
    display: flex;
    flex-direction: column;
    justify-content: start;
    width: 35em;
    gap: 1em;
}

.sub-text {
    display: flex;
    flex-direction: row;
    justify-content: start;
    text-align: left;
    font-family: Poppins, sans-serif;
}

.router-link {
    color: whitesmoke;
    padding: 0.8em 2em 0.8em 2em;
    background-color: var(--light-blue);
    border-radius: 10px;
    width: 8em;
    font-family: Poppins, sans-serif;
    transition: 0.2s ease-out;
}

.router-link:hover {
    background-color: var(--main-primary-color);
}
</style>
