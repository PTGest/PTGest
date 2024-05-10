<template>
    <div class="filters-box">
        <div class="filters">
            <font-awesome-icon class="x-button" :icon="faX"></font-awesome-icon>
           <div class="filter-texts">Filter by:</div>
              <div class="filter-sort">
                  Availability
                  <font-awesome-icon v-if="isSortedDown" @click="handleSort" class="avail-icons active" :icon="faArrowUpWideShort"></font-awesome-icon>
                  <font-awesome-icon v-if="!isSortedDown" @click="handleSort" class="avail-icons active" :icon="faArrowDownWideShort"></font-awesome-icon>
              </div>
              <div class="filter-gender">Gender</div>
        </div>




    </div>
</template>

<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faArrowDownWideShort, faArrowUpWideShort, faX} from "@fortawesome/free-solid-svg-icons";
import {ref} from "vue";


const isSortedDown = ref(false);
const handleSort = ( ) => {
    isSortedDown.value = !isSortedDown.value;
    console.log("Sorting")
}

</script>

<style scoped>

.filters-box{
    position: absolute;
    background-color: var(--primary-color);
    width: 20em;
    height: 20em;
    border-radius: 10px;
    z-index: 99;
}
.filters{
    display: grid;
    grid-template-rows: 1fr 1fr;
    grid-template-columns: 1fr 1fr 1fr 1fr;
    justify-content: center;
    align-items: start;
}

.x-button{
    padding: 0.5em;
    grid-row-start: 0;
    grid-row-end: 1;
    grid-column-start: 4;
    grid-column-end: 5;
}



.filter-texts{
    align-self: center;
    margin-left: 0.5em;
    grid-column-start: 0;
    grid-column-end: 1;
    grid-row-start: 0;
    grid-row-end: 1;
    font-size: 1.2em;
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
    grid-column-end: 3;
    grid-row-start: 1;
    grid-row-end: 2;
    font-size: 1.2em;
    font-weight: bold;
    font-family: Poppins, sans-serif;
    background-color: #c800ff;
}

.avail-icons{
    padding: 0.5em;
    cursor: pointer;
}
.active{
    background-color: var(--light-blue);
    color: whitesmoke;
    border-radius: 10px;
}

</style>
