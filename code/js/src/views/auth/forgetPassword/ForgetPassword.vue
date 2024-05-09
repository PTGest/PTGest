<template>
    <div class="forget-password-container">
        <img class="image" src="../../../assets/forgotPassword.png" alt="forgetPasswordImage" />
        <div class="text-container">
            <div class="forget-password-text">Forgot</div>
            <div class="forget-password-text">Your Password?</div>
            <InputBar padding="0.5em 1em 0.5em 1em" :is_-password="false" width="20em" text="Email" class-name="email-input" placeholder="Enter your email" height="2.5em" @value="updateEmail" />

            <!--      <input v-model="email" class="email-input" placeholder="Enter your email"/>-->
            <DefaultButton class="button" display-text="Send Email" :click-handler="forgetPassword" :is-disabled="email == ''" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import forgetPasswordServices from "../../../services/AuthServices/forgetPassword.ts"
import DefaultButton from "../../../components/utils/DefaultButton.vue"
import InputBar from "../../../components/utils/InputBar.vue"

const email = ref("")

const forgetPassword = () => {
    forgetPasswordServices(email.value)
    console.log(email.value)
}
const updateEmail = (value: string) => {
    console.log(value)
    email.value = value
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
    background-image: url(../../../assets/waves.svg);
    background-size: 100% 80%;
    background-repeat: no-repeat no-repeat;
    background-position-y: 250%;
    box-shadow: whitesmoke 30px 30px 2px 0px;
}
.forget-password-text {
    font-size: 40px;
    font-weight: bold;
    color: whitesmoke;
}

.text-container {
    display: flex;
    flex-direction: column;
    align-items: start;
    margin-bottom: 20px;
    color: whitesmoke;
}

.image {
    position: relative;
    left: -2em;
    width: 40em;
    height: 40em;
    padding: 2em 4em 2em 4em;
}

.button {
    width: 22em;
}
</style>
