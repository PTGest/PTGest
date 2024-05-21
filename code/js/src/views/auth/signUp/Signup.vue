<template>
    <div class="signup-container">
        <img v-if="!store.state.is_mobile_view" class="signup-img" src="../../../assets/signup.png" alt="Signup image" />
        <div class="signup-inputs-container" id="signup-container">
            <h1>Sign up</h1>
            <div class="signup-input-container">
                <div class="signup-input-text">Name</div>
                <input v-model="signupUserData.name" class="signup-name-input signup-input-base" placeholder="Enter your name" />
            </div>
            <div class="signup-input-container">
                <div class="signup-input-text">Email</div>
                <input v-model="signupUserData.email" class="signup-email-input signup-input-base" placeholder="Enter your email" />
            </div>

            <div class="signup-input-container">
                <div class="signup-input-text">Password</div>
                <div class="password-container">
                    <input v-model="signupUserData.password" :type="is_visible" class="signup-password-input signup-input-base" placeholder="Enter your password" />
                    <font-awesome-icon :icon="faEye" class="visible-icon" @click="updateVisibility"></font-awesome-icon>
                </div>
            </div>

            <div v-if="toggle" class="signup-input-container">
                <div class="signup-input-text">Gender</div>
                <DropdownMenu @gender="updateGender" arrowRight="-13.6em" size="18.5em" />
            </div>

            <div v-if="toggle" class="signup-input-container">
                <div class="phone-text">Phone Number</div>
                <div class="phone-container">
                    <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
                    <input v-model="countryNumber" :maxlength="3" class="signup-phone-country-input signup-input-base" />
                    <input v-model="phoneNumber" pattern="[0-9]" :maxlength="9" class="signup-phone-input signup-input-base" placeholder="Phone Number" />
                </div>
            </div>

            <div class="signup-switch-button">
                <font-awesome-icon :icon="faPerson" class="switch-icon-pt" @click="toggleSwitch(true)"></font-awesome-icon>
                <div :class="[toggle ? 'switch-toggle-pt' : 'switch-toggle-c']"></div>
                <font-awesome-icon :icon="faBuilding" class="switch-icon-c" @click="toggleSwitch(false)"></font-awesome-icon>
            </div>

            <button class="signup-button" @click="signUp" :disabled="isSignUpDisabled">Sign up</button>
            <router-link class="login-text" :to="{ name: 'login' }">Already have an account?</router-link>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, Ref, ref } from "vue"
import { signupUserServices } from "../../../services/AuthServices/signupServices.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faBuilding, faEye, faPerson, faPlus } from "@fortawesome/free-solid-svg-icons"
import DropdownMenu from "../../../components/utils/DropdownMenu.vue"
import SignupPTData from "../../../models/authModels/SignupPTData.ts"
import store from "../../../store"

const countryNumber = ref("")
const phoneNumber = ref("")
const is_visible = ref("password")
const toggle = ref(true)
const signupUserData: Ref<SignupPTData> = ref({
    name: "",
    email: "",
    gender: "",
    password: "",
    phoneNumber: "",
    user_type: "independent_trainer",
})

function isFullOfData(data: SignupPTData): boolean {
    return toggle && (data.name !== "" && data.email !== "" && data.gender !== "" && data.password !== ""
        && phoneNumber.value != "" && countryNumber.value != "") || !toggle && (data.name !== "" && data.email !== "")
}

const isSignUpDisabled = computed(() => {
    return !isFullOfData(signupUserData.value)
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
.signup-img {
    width: 40em;
    height: 40em;
    margin-right: 3em;
    z-index: 99;
}

.signup-input-base,
.signup-birth-input-placeholder {
    height: 2.5em;
    width: 20em;
    border-radius: 5px;
    border: 0;
    padding: 0 1em 0 1em;
    color: var(--primary-color);
    background-color: whitesmoke;
}

.signup-input-container {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: start;
    margin-bottom: 1em;
}

.signup-container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    width: 70em;
    border-radius: 20px;
    background-color: var(--light-blue);
    z-index: 99;
}

.signup-inputs-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-bottom: 2em;
}

.signup-input-text {
    display: flex;
    flex-direction: row;
    justify-content: start;
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
    color: var(--sign-up-black);
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
    background-color: whitesmoke;
    border-radius: 30px;
}

.switch-toggle-pt,
.switch-toggle-c {
    width: 3em;
    height: 3em;
    border-radius: 50%;
    background-color: var(--sign-up-blue);
    position: relative;
    right: 1.8em;
    transition: 0.2s ease-out;
}

.switch-toggle-c {
    position: relative;
    right: -1.9em;
    transition: 0.2s ease-out;
}

.switch-icon-pt,
.switch-icon-c {
    z-index: 10;
    cursor: pointer;
    color: var(--sign-up-black);
}

.signup-button {
    margin-top: 1em;
    width: 20em;
    height: 3em;
    border-radius: 5px;
    background-color: whitesmoke;
    color: var(--sign-up-black);
    border: 0;
    cursor: pointer;
}

.signup-button:disabled {
    background-color: #d3d3d3;
    color: #a9a9a9;
    cursor: not-allowed;
}

.login-text {
    color: rgba(245, 245, 245, 0.9);
    margin-top: 0.5em;
}

@media screen and (max-width: 990px) {
    .signup-container {
        position: relative;
        left: 1.5em;
        width: 15em;
    }

    .signup-inputs-container {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        margin-bottom: 2em;
    }

    .signup-input-base {
        width: 13em;
    }
    .dropdown-menu-container {
        width: 12.5em;
    }
    .phone-text {
        right: 1.5em;
    }
    .phone-container {
        width: 8em;
        position: relative;
        right: 2.5em;
    }
    .signup-phone-country-input {
        width: 1em;
    }
    .signup-phone-input {
        width: 8em;
    }

    .signup-button {
        width: 10em;
    }
    .switch-icon-pt,
    .switch-icon-c {
        z-index: 10;
        cursor: pointer;
        color: var(--sign-up-black);
    }
}
</style>
