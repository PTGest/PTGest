<template>
    <div class="user-info-container">
        <h1>Personal Info</h1>
        <div class="user-info">
            <h2 class="info">{{ userInfo.name }}</h2>
            <div class="info">{{ age }} years old</div>
            <div class="info">{{ userInfo.gender }}</div>
            <div class="info">{{ userInfo.phoneNumber }}</div>
            <div class="info">{{ userInfo.email }}</div>
        </div>
    </div>
</template>

<script setup lang="ts">
import UserInfo from "@/views/user/UserProfile/Models/UserInfo.ts"
import { Ref, ref } from "vue"
import { getUserInfo } from "@/services/UserServices/profileServices.ts"
import { getYearFromDate } from "@/services/utils/dateUtils/getFromDateUtils.js"

const props = defineProps<{
    userId: string
}>()

const userInfo: Ref<UserInfo> = ref(new UserInfo())
const age: Ref<number | null> = ref(null)

;(async () => {
    userInfo.value = await getUserInfo(props.userId)
    age.value = new Date().getFullYear() - getYearFromDate(userInfo.value.birthdate)
})()
</script>

<style scoped>
.user-info-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 1em;
    padding: 1em;
}

.user-info {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 1em;
    border-radius: 10px;
    background-color: var(--main-primary-color);
    border: 1px solid var(--main-secondary-color);
    color: whitesmoke;
}

h1 {
    font-size: 2em;
    font-weight: bold;
    color: whitesmoke;
    text-align: center;
    margin: 0;
}
h2 {
    font-size: 1.5em;
    font-weight: bold;
    color: whitesmoke;
    text-align: center;
    margin: 0;
}
.info {
    padding: 0 2em 0 2em;
}
</style>
