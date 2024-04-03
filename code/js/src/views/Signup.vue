<template>
  <div class="signup-container">
    <h1>Sign up</h1>
    <div class="signup-inputs-container">

      <div class="signup-input-container">
        <div class="signup-input-text">Name</div>
        <input v-model="signupUserData.name" class="signup-name-input signup-input-base" placeholder="Enter your name"/>
      </div>

      <div class="signup-input-container">
        <div class="signup-input-text">Email</div>
        <input v-model="signupUserData.email" class="signup-email-input signup-input-base" placeholder="Enter your email"/>
      </div>

      <div class="signup-input-container">
        <div class="signup-input-text">Password</div>
        <div class="password-container">
          <input v-model="signupUserData.password" :type='is_visible' class="signup-password-input signup-input-base"
                 placeholder="Enter your password"/>
          <font-awesome-icon :icon=faEye class="visible-icon" @click="updateVisibility"></font-awesome-icon>
        </div>
      </div>


      <div v-if="toggle" class="signup-input-container">
        <div class="signup-input-text">Gender</div>
        <DropdownMenu @gender="updateGender"/>
      </div>

      <div v-if="toggle" class="signup-input-container">
        <div class="phone-text">Phone Number</div>
        <div class="phone-container">
          <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
          <input v-model="countryNumber" :maxlength="3" class="signup-phone-country-input signup-input-base"/>
          <input v-model="phoneNumber" :maxlength="9" class="signup-phone-input signup-input-base"
                 placeholder="Phone Number"/>
        </div>
      </div>

      <div class="signup-switch-button">
        <font-awesome-icon :icon="faPerson" class="switch-icon-pt" @click="toggleSwitch(true)"></font-awesome-icon>
        <div :class="[toggle ?'switch-toggle-pt': 'switch-toggle-c' ]"></div>
        <font-awesome-icon :icon="faBuilding" class="switch-icon-c" @click="toggleSwitch(false)"></font-awesome-icon>
      </div>

      <button class="signup-button" @click="signUp">Sign up</button>

    </div>

  </div>
</template>


<script setup lang="ts">
import {Ref, ref} from 'vue'
import {signupUserServices} from "../services/authServices/signupServices.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faBuilding, faEye, faPerson, faPlus} from "@fortawesome/free-solid-svg-icons";
import DropdownMenu from "../components/DropdownMenu.vue";
import SignupPTData from "../models/authModels/SignupPTData.ts";



let countryNumber = ref("")
let phoneNumber = ref("")
let is_visible = ref("password")
let toggle = ref(true)
let signupUserData: Ref<SignupPTData> = ref({
  name: "",
  email: "",
  gender: "",
  password: "",
  phoneNumber: "",
  user_type: "independent_trainer"
})


const updateVisibility = () => {
  if (is_visible.value === "") {
    is_visible.value = "password"
  } else {
    is_visible.value = ""
  }
}
const toggleSwitch = (value: boolean) => {
  toggle.value = value
  signupUserData.value.user_type = value ? "independent_trainer" : "company"
}

const updateGender = (value: string) => {
  signupUserData.value.gender = value.toUpperCase()
}
const signUp = () => {
  signupUserData.value.phoneNumber = `+${countryNumber.value}${phoneNumber.value}`
  signupUserServices(signupUserData.value)
  console.log(signupUserData.value)
}

</script>

<style scoped>

.signup-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 25em;
  border-radius: 20px;
  background-color: var(--primary-color);
}

.signup-inputs-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 2em;
}

.signup-input-container {
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  margin-bottom: 1em;
}

.signup-input-text {
  display: flex;
  flex-direction: row;
  justify-content: start;
}

.signup-input-base, .signup-birth-input-placeholder {
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

.phone-container {
  position: relative;
  right: -0.8em;
  display: flex;
  flex-direction: row;
  width: 20em;
  justify-content: start;
  align-items: center;
}

.signup-phone-country-input {
  max-width: 2em;
  margin-right: 1em;
}

.signup-phone-input {
  max-width: 13.6em;
}

.plus-icon {
  position: relative;
  margin-right: 0.5em;
}

.phone-text {
  position: relative;
  right: -0.8em;
}

.signup-birth-input-placeholder {
  color: #757575;
}

.signup-switch-button {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 7em;
  height: 3.5em;
  background-color: var(--secundary-color);
  border-radius: 30px;
}

.switch-toggle-pt, .switch-toggle-c {
  width: 3em;
  height: 3em;
  border-radius: 50%;
  background-color: #535bf2;
  position: relative;
  right: 1.8em;
  transition: 0.2s ease-out;
}

.switch-toggle-c {
  position: relative;
  right: -1.9em;
  transition: 0.2s ease-out;
}

.switch-icon-pt, .switch-icon-c {
  z-index: 10;
  cursor: pointer;
}

.signup-button {
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
