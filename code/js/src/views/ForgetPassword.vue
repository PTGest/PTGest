<template>

  <div class="forget-password-container">
    <img class="image" src=".././assets/forgotPassword.png" alt="forgetPasswordImage"/>
    <div class="text-container">
      <div class="forget-password-text">Forgot</div>
      <div class="forget-password-text">Your Password ?</div>
      <input v-model="email" class="email-input" placeholder="Enter your email"/>
      <button class="email-button" @onclick="forgetPassword">Send Email</button>
    </div>

  </div>

</template>

<script setup lang="ts">
import {ref} from 'vue'
import forgetPasswordServices from "../services/authServices/forgetPassword.ts";

let email = ref('')

const forgetPassword = () => {
  forgetPasswordServices(email.value)
  console.log(email.value)
}

</script>

<style scoped>
.forget-password-container {
  display: flex;
  width: 80em;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background-color: var(--light-blue);
  background-image: url(.././assets/waves.svg);
  background-size: 100% 80%;
  background-repeat: no-repeat no-repeat;
  background-position-y: 250%;
  box-shadow: whitesmoke 30px 30px 2px 0px;
}

.text-container {
  display: flex;
  flex-direction: column;
  align-items: start;
  margin-bottom: 20px;
  font-family: Inter, sans-serif;
  font-size: 45px;
  color: whitesmoke;
}

.image{
  position: relative;
  left: -2em;
  width: 40em;
  height: 40em;
  padding: 2em 4em 2em 4em;
}

.email-input{
  width: 25em;
  height: 2em;
  border-radius: 5px;
  border: 1px solid var(--primary-color);
  margin-top: 1em;
  margin-right: 1em;
  padding: 0.5em;
  font-family: Inter, sans-serif;
  font-size: 15px;
  color: var(--primary-color);
  background-color: whitesmoke;
}

.email-input:focus{
  outline: 1px solid whitesmoke;
}

.email-button{
  width: 26em;
  border-radius: 5px;
  margin-top: 1em;
  padding: 1em;
  font-family: Inter, sans-serif;
  font-size: 15px;
  color: whitesmoke;
  background-color: var(--primary-color);
}


</style>