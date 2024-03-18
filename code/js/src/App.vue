
<template>

  <div>
    <li><router-link link :to="{ name : 'home' }"> Home </router-link></li>
    <li><router-link link :to="{ name : 'about' }"> About </router-link></li>
    <li><router-link link :to="{ name : 'login' }"> Login </router-link></li>
  </div>

  <router-view/>
</template>


<script setup>
  import CounterButton from '@/components/CounterButton.vue'


</script>