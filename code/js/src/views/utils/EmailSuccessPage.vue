<template>
    <div class="email-success">
        <h1>Email Sent</h1>
        <div class="email-success-container">
            <div class="email-success-text">An email has been sent to your email address</div>
            <div class="email-success-text">Please check your email to reset your password</div>
        </div>
    </div>
</template>

<script setup lang="ts">

</script>

<style scoped>
.email-success {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: whitesmoke;
    border-radius: 10px;
    background-color: var( --main-primary-color);
}
.email-success-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    font-family: 'Poppins', sans-serif;
    font-size: 1.5em;
    padding: 2em;
    color: whitesmoke;
}
</style>
