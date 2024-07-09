<template>
    <div v-if="loading">
        <ProgressSpinner />
    </div>
    <div v-else>
        <div class="profile-container">
            <img class="image" :src="icon" alt="user-icon" width="150" height="150">
            <div class="profile-info">
                <div class="info-row">
                    <font-awesome-icon :icon="faUser" class="icon"/>
                    {{userInfo.name}}
                </div>
                <div class="info-row">
                    <font-awesome-icon :icon="faEnvelope" class="icon"/>
                    {{userInfo.email}}
                </div>
                <div class="info-row">
                    <font-awesome-icon :icon="faPhone" class="icon"/>
                    {{formatPhoneNumber(userInfo.phoneNumber)}}
                </div>
                <div class="change-password-btn">
                   Change Password
                </div>
            </div>
        </div>
        <div class="new-password-container">
            <p>New Password</p>
            <input v-model="newPassword" placeholder="Enter new password"/>
            <Button @click="changePassword" class="submit-btn">Submit</Button>
        </div>
    </div>
</template>

<script setup lang="ts">
import Button from "primevue/button";
import ProgressSpinner from "primevue/progressspinner";
import store from "@/store";
import {getUserInfo} from "@/services/UserServices/profileServices.ts";
import {Ref, ref} from "vue";
import UserInfo from "@/views/user/UserProfile/Models/UserInfo.ts";
import icon from "@/assets/userIcons/man.png";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faEnvelope, faPhone, faUser} from "@fortawesome/free-solid-svg-icons";
import formatPhoneNumber from "../../../services/utils/formatPhoneNumber.ts";
import {changeUserPassword} from "@/services/AuthServices/changePassword.ts";

const newPassword = ref("");
const loading = ref(true);
const userInfo : Ref<UserInfo> = ref(new UserInfo());
( async () => {
    userInfo.value = await getUserInfo(store.getters.userData.id)
    loading.value = false
    console.log("USER INFO", userInfo)
})()

const changePassword = async () => {
    await changeUserPassword('', newPassword.value);
    //TODO() MUDAR A CURRENT PASSWORD PARA A CURRENT PASSWORD DO USER
}
</script>



<style scoped>
.profile-container{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: start;
    width: 25em;
    margin-top: 10%;
    background-color: var(--main-primary-color);
    padding: 2em;
    border-radius: 20px;
    gap: 1em;
}
.image{
    border: 5px solid whitesmoke;
    border-radius: 50%;
}

.profile-info{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 1em;
    border-radius: 20px;
    gap: 1em;
}

.info-row{
    display: flex;
    align-items: center;
    justify-content: start;
    gap: 1em;
    width: 20em;
    padding: 1em;
    background-color: whitesmoke;
    font-size: 1em;
    font-family: 'Poppins', sans-serif;
    color: var(--main-primary-color);
    border-radius: 10px;
}
.icon{
    font-size: 1.5em;
    color: var(--main-primary-color);
}

.change-password-btn{

    padding: 0.3em;
    cursor: pointer;
}
.change-password-btn:hover{
    color: var(--main-tertiary-color);
    border-radius: 10px;
    transition: color 0.2s ease-in-out;
}

.new-password-container{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: var(--main-primary-color);
    border-radius: 20px;
    padding: 1.2em;
    gap: 1em;
}

.new-password-container p {
    padding: 0.2em;
    margin: 0
}
.new-password-container input{
    padding: 0.5em;
    border-radius: 10px;
    border: 1px solid var(--main-tertiary-color);
    font-family: 'Poppins', sans-serif;
    width: 15em;
}
.submit-btn{
    padding: 0.5em 1em;
    border-radius: 5px;
    color: whitesmoke;
    font-family: 'Poppins', sans-serif;
    font-size: 1em;
    font-weight: bold;
    background-color: var(--button-border-color);
    border: 1px solid var(--button-border-color);
    cursor: pointer;
}
.submit-btn:hover{
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    transition: background-color 0.2s ease-in-out;
}
</style>
