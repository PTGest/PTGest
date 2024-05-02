<template>
    <h2>Register Trainee</h2>
  <div class="trainee-register">
      <input-bar @value='update("name",$event )' padding="0em 0.5em 0 0.5em" is_password width="18em" class-name="trainee-input" text="Trainee Name"
                 placeholder="Enter Trainee Name" height="3em"/>
      <input-bar @value='update("email",$event )' padding="0em 0.5em 0 0.5em" is_password width="18em" class-name="trainee-input"
                 text="Trainee Email" height="3em" placeholder="Enter Trainee Email"/>
      <div class="birth-text">
          Trainee Birthdate
          <input v-model="trainee_Data.birthdate" type="date" placeholder="Enter Trainee BirthDate" class="trainee-birth"/>
      </div>
      <div class="birth-text">
          Gender
      <dropdown-menu class="menu" @gender='update("gender", $event)' size="16em" arrow_right="-11em"/>
      </div>

      <div class="phone-container">
          <font-awesome-icon  :icon="faPlus" class="plus-icon"></font-awesome-icon>
          <input-bar class="trainee-phone-input"  @value='updatePhone("country", $event)' padding="0em 0.5em 0 0.5em"
                     is_password width="2em" class-name="trainee-input"
                     text="" height="3em" placeholder=""/>
          <input-bar class="trainee-phone-input" @value='updatePhone("phone", $event)' padding="0em 0.5em 0 0.5em"
                     is_password width="13em" class-name="trainee-input"
                     text="" height="3em" placeholder="Enter Trainee Phone"/>
      </div>


      <default-button class="register-button" display-text="Register Trainee" :click-handler="authSign" :is-disabled="!is_disabled"/>
  </div>

</template>


<script setup lang="ts">

import InputBar from "../../../components/InputBar.vue";
import DefaultButton from "../../../components/DefaultButton.vue";
import {computed, Ref, ref} from "vue";
import TraineeRegisterData from "../../../views/user/UserRegister/models/TraineeRegisterData.ts";
import DropdownMenu from "../../../components/DropdownMenu.vue";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import authenticatedSignup from "../../../services/AuthServices/AuthenticatedSignup.ts";

let countryNumber = ref("");
let phoneNumber = ref("");
let trainee_Data: Ref<TraineeRegisterData> = ref({
    name: ref(""),
    email: ref(""),
    birthdate: ref(""),
    gender: ref(""),
    phoneNumber: ref(""),
    user_type: ref("trainee"),
});

const is_disabled = computed(() => {
    console.log("is_disabled", trainee_Data.value);
    console.log("countryNumber", countryNumber.value);
    console.log("phoneNumber", phoneNumber.value);

    return !(trainee_Data.value.name == "" ||
        trainee_Data.value.email == "" ||
        trainee_Data.value.gender == "" ||
        trainee_Data.value.birthdate == "" ||
        countryNumber.value == "" ||
        phoneNumber.value == ""
    );
});

function updatePhone (type: string, value: string)  {
   switch (type) {
         case "country":
              countryNumber.value = value;
              console.log("countryNumber", countryNumber.value);
              break;
         case "phone":
             phoneNumber.value = value;
             console.log("phoneNumber", phoneNumber.value);
             break;
   }
}


function update(paramName: string, value: string) {
    console.log(paramName, value);
    switch (paramName) {
        case "name":
            trainee_Data.value.name = value;
            break;
        case "email":
            trainee_Data.value.email = value;
            break;
        case "gender" :
            trainee_Data.value.gender = value.toUpperCase();
            break;

    }
}

const authSign = () => {
    trainee_Data.value.phoneNumber = `+${countryNumber.value}${phoneNumber.value}`;
    authenticatedSignup(new TraineeRegisterData(
        trainee_Data.value.name,
        trainee_Data.value.email,
        trainee_Data.value.birthdate,
        trainee_Data.value.gender,
        trainee_Data.value.phoneNumber,
        "trainee"
    ));
}

</script>


<style scoped>
.trainee-register {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: var(--sign-up-blue);
    width: 20em;
    height: 33em;
    gap: 0.5em;
    border-radius: 10px;
}

.trainee-birth{
    height: 3em;
    width: 18em;
    border: 0;
    padding: 0 0.5em 0 0.5em;
    margin-bottom: 0.5em;
    color: var(--primary-color);
    background-color: whitesmoke;
    border-radius: 5px;
}

.birth-text{
    width: 80%;
    text-align: left;
}
h2{
    font-size: 2em;
}

.register-button {
    margin-top: 1em;
    width: 18em;
    height: 3em;
    border-radius: 5px;
    background-color: whitesmoke;
    color: var(--sign-up-black);
    border: 0;
    cursor: pointer;
}
.phone-container{
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 0.5em;
}

.trainee-phone-input{
   margin-bottom: 0;
}
</style>
