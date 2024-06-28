<template>
    <h2>{{ text }}</h2>
    <div v-if="!isLoading" class="trainee-register">
        <InputBar @value="update('name', $event)" padding="0em 0.5em 0 0.5em" is_password width="18em" class-name="trainee-input" text="Trainee Name" placeholder="Enter Trainee Name" height="3em" />
        <InputBar
            @value="update('email', $event)"
            padding="0em 0.5em 0 0.5em"
            is_password
            width="18em"
            class-name="trainee-input"
            text="Trainee Email"
            height="3em"
            placeholder="Enter Trainee Email"
        />
        <div v-if="isTrainee" class="birth-text">
            Trainee Birthdate
            <input v-model="trainee_Data.birthdate" type="date" placeholder="Enter Trainee BirthDate" class="trainee-birth" />
        </div>
        <div class="birth-text">
            Gender
            <dropdown-menu class="menu" @gender="update('gender', $event)" size="15em" />
        </div>

        <div class="phone-container">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
            <InputBar
                class="trainee-phone-input"
                @value="updatePhone('country', $event)"
                padding="0em 0.5em 0 0.5em"
                is_password
                width="3em"
                class-name="trainee-input"
                text=""
                height="3em"
                placeholder=""
            />
            <InputBar
                class="trainee-phone-input"
                @value="updatePhone('phone', $event)"
                padding="0em 0.5em 0 0.5em"
                is_password
                width="12.5em"
                class-name="trainee-input"
                text=""
                height="3em"
                placeholder="Enter Trainee Phone"
            />
        </div>

        <div v-if="!isTrainee" class="capacity-text">
            Capacity
            <InputBar class="capacity" @value="update('capacity', $event)" padding="0em 0.5em 0 0.5em" is_password width="3em" class-name="trainer-capacity" text="" height="3em" placeholder="" />
        </div>

        <default-button class="register-button" display-text="Register Trainee" :click-handler="authSign" :is-disabled="isDisabled" />
    </div>

    <div v-else class="loading">
        <h1>Loading...</h1>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue"
import authenticatedSignup from "../../../services/AuthServices/AuthenticatedSignup.ts"
import TraineeRegisterData from "../../../views/user/UserRegister/models/TraineeRegisterData.ts"
import HiredTrainerRegisterData from "../../../models/authModels/HiredTrainerRegisterData.ts"
import router from "../../../plugins/router.ts"
import { faPlus } from "@fortawesome/free-solid-svg-icons"
import DropdownMenu from "../../../components/utils/DropdownMenu.vue"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import DefaultButton from "../../../components/utils/DefaultButton.vue"
import InputBar from "../../../components/utils/InputBar.vue"

const isTrainee = ref(router.currentRoute.value.params.isTrainee === "true")
const text = computed(() => {
    return isTrainee.value ? "Register Trainee" : "Register Trainer"
})

const capacity = ref(-1)
const countryNumber = ref("")
const phoneNumber = ref("")
const trainee_Data = ref<TraineeRegisterData>({
    name: "",
    email: "",
    birthdate: "",
    gender: "",
    phoneNumber: "",
    user_type: "trainee",
})

const isLoading = ref(false) // Loading state variable

const isDisabled = computed(() => {
    if (isTrainee.value) {
        return !(trainee_Data.value.name != "" && trainee_Data.value.email != "" && trainee_Data.value.gender != "" && phoneNumber.value != "" && trainee_Data.value.birthdate != "")
    } else {
        return !(trainee_Data.value.name != "" && trainee_Data.value.email != "" && trainee_Data.value.gender != "" && phoneNumber.value != "" && capacity.value != -1)
    }
})

function updatePhone(type: string, value: string) {
    switch (type) {
        case "country":
            countryNumber.value = value
            break
        case "phone":
            phoneNumber.value = value
            break
    }
}

function update(paramName: string, value: any) {
    switch (paramName) {
        case "name":
            trainee_Data.value.name = value
            break
        case "email":
            trainee_Data.value.email = value
            break
        case "gender":
            trainee_Data.value.gender = value.toUpperCase()
            console.log(trainee_Data.value.gender)
            break
        case "capacity":
            capacity.value = value
            break
    }
}

const authSign = async () => {
    isLoading.value = true // Set loading state to true before API request
    console.log("isLoader", isLoading.value)
    trainee_Data.value.phoneNumber = `+${countryNumber.value}${phoneNumber.value}`
    try {
        const userData = isTrainee.value
            ? new TraineeRegisterData(trainee_Data.value.name, trainee_Data.value.email, trainee_Data.value.birthdate, trainee_Data.value.gender, trainee_Data.value.phoneNumber, "trainee")
            : new HiredTrainerRegisterData(trainee_Data.value.name, trainee_Data.value.email, trainee_Data.value.gender, capacity.value, trainee_Data.value.phoneNumber, "hired_trainer")
        await authenticatedSignup(userData)
        console.log("isLoader", isLoading.value)
    } catch (error) {
        console.error("Error:", error)
    } finally {
        isLoading.value = false // Reset loading state after API request is completed
        console.log("isLoader", isLoading.value)
    }
}
</script>

<style scoped>
.trainee-register {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: var(--main-primary-color);
    width: 20em;
    height: 33em;
    gap: 0.5em;
    border-radius: 10px;
    padding: 0.5em;
}

.trainee-birth {
    height: 3em;
    width: 18em;
    border: 0;
    padding: 0 0.5em 0 0.5em;
    margin-bottom: 0.5em;
    color: var(--main-primary-color);
    background-color: whitesmoke;
    border-radius: 5px;
}

.birth-text {
    width: 80%;
    text-align: left;
}
h2 {
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
.phone-container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 0.5em;
    margin-top: 1em;
}

.trainee-phone-input {
    margin-bottom: 0;
}

.capacity-text {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
</style>
