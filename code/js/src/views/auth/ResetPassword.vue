<template>
    <div class="reset-password-container">
        <img class="image" src="../../assets/resetPassword.png" alt="ResetPasswordImage" />
        <div class="reset-password-input-container">
            <div class="reset-text-container">
                <div class="reset-text">Reset</div>
                <div class="reset-text">Your Password.</div>
                <input-bar
                    @value="updatePasswordValue"
                    class-name="reset-password-input"
                    is-password="true"
                    placeholder="New Password"
                    is_-password
                    text="Password"
                    padding="0.5em 1em 0.5em 1em"
                    height="2.5em"
                    width="20em"
                />
                <input-bar
                    @value="updateConfirmPasswordValue"
                    @change="verifyPasswords"
                    class-name="reset-password-input"
                    is-password="true"
                    placeholder="New Password"
                    is_-password
                    text="Confirm Password"
                    padding="0.5em 1em 0.5em 1em"
                    height="2.5em"
                    width="20em"
                />
            </div>

            <DefaultButton class="reset-button" display-text="Reset Password" :is-disabled="!equalPasswords" :click-handler="resetPassword" />
        </div>
    </div>
</template>

<script setup lang="ts">
import InputBar from "../../components/utils/InputBar.vue"
import { ref } from "vue"
import DefaultButton from "../../components/utils/DefaultButton.vue"
import { resetPasswordServices } from "../../services/authServices/resetPassword.ts"
import ResetPasswordData from "../../models/authModels/ResetPasswordData.ts"
import { useRoute } from "vue-router"

const params = useRoute().params

const password = ref("")
const confirm_password = ref("")
const equalPasswords = ref(false)

const updatePasswordValue = (value: string) => {
    console.log(value)
    password.value = value
}
const updateConfirmPasswordValue = (value: string) => {
    confirm_password.value = value
}
const verifyPasswords = () => {
    if (password.value !== confirm_password.value) {
        console.log("Passwords do not match")
    } else {
        equalPasswords.value = true
    }
}
const resetPassword = () => {
    resetPasswordServices(new ResetPasswordData(password.value), params.token)
}
</script>

<style scoped>
.image {
    width: 40em;
    height: 40em;
    margin-right: 3em;
}

.reset-password-container {
    display: flex;
    flex-direction: row;
    width: 70em;
    height: 40em;
    justify-content: center;
    align-items: center;
    background-color: var(--sign-up-blue);
    border-radius: 20px;
}

.reset-password-input-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-bottom: 1em;
}

.reset-text-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
    width: 20em;
}

.reset-text {
    font-size: 2.5em;
    font-family: Poppins, sans-serif;
    font-style: normal;
    font-weight: bold;
}

.reset-button {
    position: relative;
    right: 0.5em;
}
</style>
