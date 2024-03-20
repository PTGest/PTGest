<template>
    <button @click="counter++">{{ counter }}</button>
</template>


<script setup>
    import {ref} from 'vue'
    const counter = ref(0)
</script>
