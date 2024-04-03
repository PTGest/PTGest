<template>
<div class="login-container">
  <h1>Login</h1>
  <div class="login-inputs-container">

    <div class="login-input-container">
      <div class="login-input-text">Email</div>
      <input v-model="loginUserData.email" class="login-name-input login-input-base" placeholder="Enter your email"/>
    </div>

    <div class="login-input-container">
      <div class="login-input-text">Password</div>
      <div class="password-container">
        <input v-model="loginUserData.password" :type='is_visible' class="login-password-input login-input-base"
               placeholder="Enter your password" />
        <font-awesome-icon :icon=faEye class="visible-icon" @click="updateVisibility"></font-awesome-icon>
      </div>
    </div>
    <button @click="login" class="login-button">Login up</button>
  </div>

</div>
</template>

<script setup lang="ts">
import {faBuilding, faEye, faPerson, faPlus} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {ref} from 'vue';
import {loginUserServices} from "@/services/authServices/loginServices.ts";
import {Ref} from "vue/dist/vue";
import SignupPTData from "@/models/authModels/SignupPTData.ts";
import LoginUserData from "@/models/authModels/LoginUserData.ts";

let loginUserData : Ref<LoginUserData> = ref({
  email: "",
  password: "",
})
let is_visible = ref("password")
const updateVisibility = () => {
  if (is_visible.value === "") {
    is_visible.value = "password"
  } else {
    is_visible.value = ""
  }
}
const login= () => {
  console.log(loginUserData.value.email)
  loginUserServices(loginUserData.value)
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 25em;
  border-radius: 20px;
  background-color: var(--primary-color);
}

.login-inputs-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 2em;
}


.login-input-container {
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  margin-bottom: 1em;
}

.login-input-text {
  display: flex;
  flex-direction: row;
  justify-content: start;
}

.login-input-base {
  height: 2.5em;
  width: 20em;
  border-radius: 5px;
  border: 0;
  padding: 0 1em 0 1em;
  color: white;
  background-color: var(--secundary-color);
}

.password-container {
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.visible-icon {
  position: absolute;
  right: 0.8em;
  cursor: pointer;
}

.login-button {
  margin-top: 1em;
  width: 20em;
  height: 3em;
  border-radius: 5px;
  background-color: #535bf2;
  color: white;
  border: 0;
  cursor: pointer;
}


</style>