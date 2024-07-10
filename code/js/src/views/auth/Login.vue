<template>
    <div class="login-container">
        <img class="image" src="../../assets/login.png" alt="Login Image" />
        <div class="container">
            <h1>Login</h1>
            <div class="login-inputs-container">
                <div class="login-input-container">
                    <div class="login-input-text">Email</div>
                    <input v-model="loginUserData.email" class="login-name-input login-input-base" placeholder="Enter your email" />
                </div>

                <div class="login-input-container" id="login-inputs-containers">
                    <div class="login-input-text">Password</div>
                    <div class="password-container">
                        <input v-model="loginUserData.password" :type="is_visible" class="login-password-input login-input-base" placeholder="Enter your password" />
                        <font-awesome-icon :icon="faEye" class="visible-icon" @click="updateVisibility"></font-awesome-icon>
                    </div>
                    <a class="forget-text" href="/forgetPassword">Forgot you Password?</a>
                </div>
                <DefaultButton display-text="Login" :is-disabled="isLoginDisabled" :click-handler="login" />
                <router-link class="sign-text" :to="{ name: 'signup' }"> Don't have an account?</router-link>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { faEye } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { computed, ref } from "vue"
import { loginUserServices } from "../../services/authServices/loginServices.ts"
import { Ref } from "vue/dist/vue"
import LoginUserData from "../../models/authModels/LoginUserData.ts"
import DefaultButton from "../../components/utils/DefaultButton.vue"

const loginUserData: Ref<LoginUserData> = ref({
    email: "",
    password: "",
})
const is_visible = ref("password")
const updateVisibility = () => {
    if (is_visible.value === "") {
        is_visible.value = "password"
    } else {
        is_visible.value = ""
    }
}
const login = () => {
    console.log(loginUserData.value.email)
    loginUserServices(loginUserData.value)
}

function isFullOfData(data: LoginUserData): boolean {
    return data.email !== "" && data.password !== ""
}

const isLoginDisabled = computed(() => {
    return !isFullOfData(loginUserData.value)
})
</script>

<style scoped>
.image {
    width: 40em;
    height: 40em;
    border-radius: 20px;
    margin-right: 2em;
}

.login-container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    width: 70em;
    border-radius: 20px;
    background-color: var(--light-blue);
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
    color: var(--sign-up-black);
    background-color: whitesmoke;
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

.forget-text {
    position: relative;
    right: -0.2em;
    margin-top: 0.3em;
    font-size: 0.8em;
    font-family: Poppins, sans-serif;
    font-weight: bold;
    color: rgba(245, 245, 245, 0.6);
}
.forget-text:hover {
    color: whitesmoke;
}
.sign-text {
    color: whitesmoke;
    margin-top: 0.5em;
}
</style>
