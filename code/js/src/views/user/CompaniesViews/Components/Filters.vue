<template>
    <div class="filters-box">
        <div class="filters">
            <font-awesome-icon @click="() => {
                        $emit('visible', false)
                    }
            " class="x-button" :icon="faX"/>
           <div class="filter-texts">Filter by:</div>
              <div class="filter-sort">
                  Availability
                  <font-awesome-icon v-if="isSortedDown" @click="handleSort" class="avail-icons active" :icon="faArrowUpWideShort"></font-awesome-icon>
                  <font-awesome-icon v-if="!isSortedDown" @click="handleSort" class="avail-icons active" :icon="faArrowDownWideShort"></font-awesome-icon>
              </div>
              <div class="filter-gender">
                  Gender
                  <font-awesome-icon @click="handleGender('1')" :class="[gender == '1' ?'gender-icon active' : 'gender-icon']" :icon="faPerson"></font-awesome-icon>
                  <font-awesome-icon @click="handleGender('2')" :class="[gender == '2' ?'gender-icon active' : 'gender-icon']" :icon="faPersonDress"></font-awesome-icon>
                  <font-awesome-icon @click="handleGender('3')" :class="[gender == '3' ?'gender-icon active' : 'gender-icon']" :icon="faPersonHalfDress"></font-awesome-icon>
              </div>
            <default-button class="button" display-text="Apply Filters" :click-handler="handleApplyFilters" ></default-button>
        </div>
    </div>
</template>

<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {
    faArrowDownWideShort,
    faArrowUpWideShort,
    faPerson,
    faPersonDress, faPersonHalfDress,
    faX
} from "@fortawesome/free-solid-svg-icons";
import {ref} from "vue";
import DefaultButton from "../../../../components/utils/DefaultButton.vue";
import getCompanyTrainersOrTrainees from "@/services/companyServices/getCompanyTrainersOrTrainees.js";

const gender = ref("");
const isSortedDown = ref(false);

const handleGender = (newGender: string) => {
    gender.value = newGender
    console.log(gender.value)
}
const handleSort = ( ) => {
    isSortedDown.value = !isSortedDown.value;
    console.log("Sorting")
}


const handleApplyFilters = () => {
    const sorted = isSortedDown.value ? "ASC" : "DESC";
    let genderValue = null;
        switch(gender.value){
            case "1":
                genderValue = 'MALE'
                break;
            case "2":
                genderValue = 'FEMALE'
                break;
            case "3":
                genderValue = 'OTHER'
                break;

        }
        console.log(genderValue)
    console.log(sorted)
    getCompanyTrainersOrTrainees(4, null, sorted, genderValue)
}
</script>

<style scoped>

.filters-box{
    position: absolute;
    background-color: var(--main-primary-color);
    width: 25em;
    height: 20em;
    border : 1px solid var(--main-secundary-color); ;
    border-radius: 10px;
    z-index: 99;
}
.filters{
    display: grid;
    grid-template-rows: 1fr 1fr;
    grid-template-columns: 1fr 1fr 1fr;
    grid-column-gap: 0.5em;
    justify-content: center;
    align-items: start;
}

.x-button{
    padding: 0.5em;
    grid-row-start: 0;
    grid-row-end: 1;
    grid-column-start: 4;
    grid-column-end: 5;
    cursor: pointer ;
}



.filter-texts{
    align-self: center;
    margin-left: 0.5em;
    grid-column-start: 0;
    grid-column-end: 1;
    grid-row-start: 0;
    grid-row-end: 1;
    font-size: 1em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
}


.filter-sort{
    grid-column-start: 1;
    grid-column-end: 2;
    grid-row-start: 1;
    grid-row-end: 2;
    font-size: 1.2em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
}

.filter-gender{
    grid-column-start: 2;
    grid-row-start: 1;
    grid-row-end: 2;
    font-size: 1.2em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
}

.avail-icons{
    padding: 0.5em;
    cursor: pointer;
}
.active{
    background-color: var(--button-border-color);
    color: whitesmoke;
    border-radius: 10px;
}
.gender-icon{
    padding: 0.5em;
    margin: 0.5em;
    cursor: pointer;
}

.button{
    grid-column-start: 1;
    grid-column-end: 3;
    grid-row-start: 2;
    grid-row-end: 3;
    margin-top: 1em;
    width: 15em;
    height: 3em;
    border-radius: 5px;
    background-color: var(--main-secundary-color);
    color: whitesmoke;
    border : 1px solid var(--main-primary-color);
    cursor: pointer;
    transition: 0.2s ease-in;
}

.button:hover{
    background-color: var(--main-primary-color);
    border : 1px solid var(--button-border-color);
    transition: 0.2s ease-out;
}

</style>
