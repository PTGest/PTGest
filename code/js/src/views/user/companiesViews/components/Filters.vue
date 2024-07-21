<template>
    <div class="filters-box">
        <div class="filters">
            <div class="header-row">
                <div class="filter-texts">Filter by:</div>
                <font-awesome-icon
                    @click="
                        () => {
                            $emit('visible', false)
                        }
                    "
                    class="x-button"
                    :icon="faX"
                />
            </div>

            <div class="filters-row">
                <div class="name-input">
                    <input type="text" placeholder="Name" class="input-text" />
                </div>
                <div v-if="props.isTraineesFilter" class="filter-sort">
                    Availability
                    <font-awesome-icon v-if="isSortedDown" @click="handleSort" class="avail-icons active" :icon="faArrowUpWideShort"></font-awesome-icon>
                    <font-awesome-icon v-if="!isSortedDown" @click="handleSort" class="avail-icons active" :icon="faArrowDownWideShort"></font-awesome-icon>
                </div>
                <div class="filter-gender">
                    Gender:
                    <font-awesome-icon @click="handleGender('1')" :class="[gender == '1' ? 'gender-icon active' : 'gender-icon']" :icon="faPerson"></font-awesome-icon>
                    <font-awesome-icon @click="handleGender('2')" :class="[gender == '2' ? 'gender-icon active' : 'gender-icon']" :icon="faPersonDress"></font-awesome-icon>
                    <font-awesome-icon @click="handleGender('3')" :class="[gender == '3' ? 'gender-icon active' : 'gender-icon']" :icon="faPersonHalfDress"></font-awesome-icon>
                </div>
            </div>

            <button class="button" @click="handleApplyFilters">Apply Filters</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faArrowDownWideShort, faArrowUpWideShort, faPerson, faPersonDress, faPersonHalfDress, faX } from "@fortawesome/free-solid-svg-icons"
import { ref } from "vue"
import getCompanyTrainersOrTrainees from "@/services/companyServices/getCompanyTrainersOrTrainees.ts"
import RBAC from "@/services/utils/RBAC/RBAC.ts"
import getTrainerTrainees from "@/services/trainerServices/trainees/traineesServices.ts"
const skip = ref(4)
const gender = ref("")
const isSortedDown = ref(false)
const emit = defineEmits(["visible"])
const props = defineProps({
    isTraineesFilter: Boolean,
})
const handleGender = (newGender: string) => {
    gender.value = newGender
}
const handleSort = () => {
    isSortedDown.value = !isSortedDown.value
}

const handleApplyFilters = async () => {
    const sorted = isSortedDown.value ? "ASC" : "DESC"
    let genderValue = null
    switch (gender.value) {
        case "1":
            genderValue = "MALE"
            break
        case "2":
            genderValue = "FEMALE"
            break
        case "3":
            genderValue = "OTHER"
            break
    }
    if (RBAC.isCompany()) {
        await getCompanyTrainersOrTrainees(skip.value, null, null, null, true)
    } else {
        await getTrainerTrainees(skip.value, null, sorted, genderValue)
    }
    emit("visible", false)
}
</script>

<style scoped>
.filters-box {
    position: absolute;
    background-color: var(--main-primary-color);
    width: 25em;
    height: 20em;
    border: 1px solid var(--main-secondary-color);
    border-radius: 10px;
    z-index: 99;
}
.filters {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.x-button {
    margin-left: 2em;
    cursor: pointer;
}

.header-row {
    display: flex;
    width: 100%;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0 1.5em 0.5em 0.5em;
    margin-top: 1em;
}

.filter-texts {
    margin-left: 0.5em;
    font-size: 1em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
}

.filter-sort {
    grid-column-start: 1;
    grid-column-end: 2;
    grid-row-start: 1;
    grid-row-end: 2;
    font-size: 1.2em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
}

.filter-gender {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 0.5em;
    font-size: 1.2em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
}

.avail-icons {
    padding: 0.5em;
    cursor: pointer;
}
.active {
    background-color: var(--button-border-color);
    color: whitesmoke;
    border-radius: 10px;
}
.gender-icon {
    padding: 0.5em;
    margin: 0.5em;
    cursor: pointer;
}

.button {
    grid-column-start: 1;
    grid-column-end: 3;
    grid-row-start: 2;
    grid-row-end: 3;
    margin-top: 1em;
    width: 15em;
    height: 3em;
    border-radius: 5px;
    background-color: var(--main-secondary-color);
    color: whitesmoke;
    border: 1px solid var(--main-primary-color);
    cursor: pointer;
    transition: 0.2s ease-in;
}

.button:hover {
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    transition: 0.2s ease-out;
}

.input-text {
    width: 15em;
    height: 2em;
    border-radius: 5px;
    border: 1px solid var(--main-secondary-color);
    margin: 0.5em;
    padding: 0.5em;
    font-size: 1em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
}
</style>
