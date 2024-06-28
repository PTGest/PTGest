<template>
    <div class="profile-container">
        <ImageContainer class="image-container" :src="image" />
        <UserPersonalInfoContainer class="user-personal-info" :email="userInfo.email" :phone="userInfo.phone" location="Habibi Land" age="25" height="180cm" weight="80kg" BMI="24.7" />
    </div>
</template>

<script setup lang="ts">


import ImageContainer from "../UserProfile/components/ImageContainer.vue"
import image from "../../../assets/./userIcons/man.png"
import UserPersonalInfoContainer from "../UserProfile/components/UserPersonalInfoContainer.vue"
import { getUserInfo } from "../../../services/UserServices/profileServices.ts"
import store from "../../../store"

const props = defineProps({
    userId: String,
})

;(async () => {
    try {
        await getUserInfo()
    } catch (error) {
        console.error("Error getting user info:", error)
    }
})()

const userInfo = store.getters.userInfo

console.log(userInfo)
</script>

<style scoped>
.profile-container {
    display: grid;
    grid-template-columns: 1.5fr;
    grid-gap: 1.5em;
    grid-template-rows: 1.5fr 1fr;
    background-color: var(--sign-up-blue);
    border-radius: 10px;
}

.user-info {
    margin: 1em;
    justify-self: center;
    grid-column-start: 2;
    grid-row-start: 1;
    grid-row-end: 3;
    font-weight: bold;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.bio-card {
    margin: 1em 1em 1em 0;
    grid-column-start: 2;
    grid-row-start: 1;
}

.image-container {
    grid-column-start: 1;
    grid-row-start: 1;
    grid-row-end: 4;
    justify-self: center;
}

.user-personal-info {
    grid-column-start: 1;
    grid-row-start: 2;
    grid-row-end: 3;
    justify-self: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    font-weight: bold;
    padding: 0 4em 0 4em;
}
</style>
