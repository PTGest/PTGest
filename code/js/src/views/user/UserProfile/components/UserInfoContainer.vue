<template>
    <div class="user-info">
        <div class="username-text">{{props.userName}}</div>
        <p>Personal Trainer: </p>

    </div>
</template>



<script setup lang="ts">
const props = defineProps({
    userName: String,
    email: String,
    phone: String,
    location: String,
    age : Number,
    height: Number,
    weight: Number,
    BMI: Number
});

</script>


<style scoped>
.username-text{
    font-size: 1.5em;
    font-weight: bold;
    color: whitesmoke;
    margin: 1em;
    text-align: center;
}

.user-info{
    width: 80%;
    height: 89%;
    padding: 1em;
    color: whitesmoke;
    font-size: 1.3em;
}
</style>
