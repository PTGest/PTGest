<template>
    <div class="signup-container">
      <h1>Sign up</h1>
      <div class="signup-inputs-container">

        <div class="signup-input-container">
          <div class="signup-input-text">Name</div>
          <input  placeholder="Enter your name" class="signup-name-input signup-input-base"/>
        </div>

        <div class="signup-input-container">
          <div class="signup-input-text">Email</div>
          <input  placeholder="Enter your email" class="signup-email-input signup-input-base"/>
        </div>

        <div class="signup-input-container">
          <div class="signup-input-text">Password</div>
          <div class="password-container">
            <input :type='is_visible' v-model="password" @change="updatePassword(password)"
                   placeholder="Enter your password" class="signup-password-input signup-input-base"/>

            <font-awesome-icon class="visible-icon" :icon=faEye @click="updateVisibility"></font-awesome-icon>
          </div>
        </div>

        <div class="signup-input-container">
          <div class="signup-input-text">Birthdate</div>
          <input v-model="birthdate" @change="updateBirthdate(birthdate)" type="date"
                 :class="[birthdate.length === 0 ? 'signup-birth-input-placeholder signup-input-base' :
                  'signup-birth-input signup-input-base']"/>
        </div>



        <div class="signup-input-container ">
          <div class="signup-input-text">Gender</div>
          <DropdownMenu></DropdownMenu>
        </div>

        <div class="signup-input-container">
          <div class="phone-text">Phone Number</div>
          <div class="phone-container">
            <font-awesome-icon class="plus-icon" :icon="faPlus"></font-awesome-icon>
            <input :maxlength="3" class="signup-phone-country-input signup-input-base"/>
            <input  placeholder="Phone Number" :maxlength="9" class="signup-phone-input signup-input-base"/>
          </div>
        </div>

      </div>

    </div>
</template>


<script setup>
import {ref} from 'vue'
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faEye, faPlus} from "@fortawesome/free-solid-svg-icons";
import DropdownMenu from "@/components/DropdownMenu.vue";

let password = ref("")
let birthdate = ref('')
let is_visible = ref("password")
console.log(birthdate)
const updateVisibility = () => {
  if (is_visible.value ===  ""){
    is_visible.value = "password"
  }else{
    is_visible.value = ""
  }
}
const updatePassword = (input) => {
  password.value = input
  console.log(password)
}

const updateBirthdate = (newBirthdate) => {
  birthdate.value = newBirthdate
  console.log(birthdate)
}


</script>

<style>

  .signup-container{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 25em;
    border-radius: 20px;
    background-color: var(--primary-color);
  }

  .signup-inputs-container{
    display:flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-bottom: 2em;
  }

  .signup-input-container{
    display:flex;
    flex-direction: column;
    justify-content: start;
    align-items: start;
    margin-bottom: 1em;
  }

  .signup-input-text{
    display: flex;
    flex-direction: row;
    justify-content: start;
  }

  .signup-input-base,.signup-birth-input-placeholder{
    height: 2.5em;
    width: 20em;
    border-radius: 5px;
    border: 0;
    padding: 0 1em 0 1em;
    background-color: var(--secundary-color);
  }

  .password-container{
    position: relative;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }
  .visible-icon{
    position: absolute;
    right: 0.8em;
    cursor: pointer;
  }

  .phone-container{
    position: relative;
    right: -0.8em;
    display: flex;
    flex-direction: row;
    width: 20em;
    justify-content: start;
    align-items: center;
  }
  .signup-phone-country-input{
    max-width: 2em;
    margin-right: 1em;
  }

  .signup-phone-input{
    max-width: 13.6em;
  }

  .plus-icon{
    position: relative;
    margin-right: 0.5em;
  }
  .phone-text{
    position: relative;
    right: -0.8em;
  }

  .signup-birth-input-placeholder{
    color: #757575;
  }


</style>
