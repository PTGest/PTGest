<template>
    <div class="forget-password-container">
        <img class="image" src="../../assets/forgotPassword.png" alt="forgetPassword" />
        <div class="text-container">
            <div class="forget-password-text">Forgot</div>
            <div class="forget-password-text">Your Password?</div>
            <input placeholder="Enter your email" height="2.5em" v-model="email"/>
            <DefaultButton class="button" display-text="Send Email" :click-handler="forgetPassword" :is-disabled="isDisable" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue"
import DefaultButton from "../../components/utils/DefaultButton.vue"
import InputBar from "../../components/utils/InputBar.vue"
import { forgetPasswordServices } from "@/services/authServices/authServices.ts"
import router from "../../plugins/router";

const email = ref("")
const isDisable = computed(() => {return (email.value === "")});
const isLoading = ref(false)
const forgetPassword = async() => {
    isLoading.value = true
   try {
       await forgetPasswordServices(email.value)
       router.push({name: "emailSucessPage"})
   }catch (e) {
         console.log(e)
   }
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
    background-image: url(../../assets/waves.svg);
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
input{
    padding: 1em;
    width: 26.5em;
    height: 3em;
    font-family: 'Poppins', sans-serif;
    border-radius: 5px;
    border: 0;
}
</style>
