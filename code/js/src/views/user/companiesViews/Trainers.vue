<template>
    <Suspense>
        <template #default>
            <div class="trainers">
                <h1>Trainers</h1>
                <router-link :to="{ name: 'registerTrainer', params: { isTrainee: false } }" class="add-trainer">
                    <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
                </router-link>
                <font-awesome-icon v-if="companyTrainersRef.trainers.length != 0" @click="handleFilters(true)" :icon="faFilter" class="filter-icon"></font-awesome-icon>
                <Filters @visible="handleFilters($event)" v-if="areFiltersVisible"></Filters>
                <div v-for="trainer in companyTrainersRef.trainers" class="trainer">
                    <TrainerBox :trainer="trainer"></TrainerBox>
                </div>
                <h2 v-if="companyTrainersRef.trainers.length == 0">No More Trainers Available</h2>
                <div class="pagination">
                    <font-awesome-icon @click="handlePage(false)" :class="[skip == 0 ? 'icons-disable' : 'icons']" :icon="faChevronLeft"></font-awesome-icon>
                    <font-awesome-icon @click="handlePage(true)" :class="[companyTrainersRef.trainers.length > 0 ? 'icons' : 'icons-disable']" :icon="faChevronRight"></font-awesome-icon>
                </div>
            </div>
        </template>
        <template #fallback>
            <div class="loading">
                <h1>Loading...</h1>
            </div>
        </template>
    </Suspense>
</template>

<script setup lang="ts">
import { faChevronLeft, faChevronRight, faFilter, faPlus } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { Ref, ref } from "vue"
import CompanyTrainers from ".//models/CompanyTrainers.ts"
import getCompanyTrainersOrTrainees from "../../../services/companyServices/getCompanyTrainersOrTrainees.ts"
import TrainerBox from "./components/TrainerBox.vue"
import Filters from "./components/Filters.vue"

const companyTrainersRef: Ref<CompanyTrainers> = ref({
    trainers: [],
    total: 0,
})

const skip = ref(0)
const areFiltersVisible = ref(false)
;(async () => {
    try {
        companyTrainersRef.value = <CompanyTrainers>await getCompanyTrainersOrTrainees(skip.value, null, null, null, false, null)
    } catch (error) {
        console.error("Error getting user info:", error)
    }
})()

const handlePage = (isNext: boolean) => {
    if (isNext) {
        skip.value += 4
        getTrainers(skip.value)
    } else {
        if (skip.value - 4 >= 0) skip.value -= 4
        getTrainers(skip.value)
    }
}
const getTrainers = async (skip: number) => {
    try {
        companyTrainersRef.value = <CompanyTrainers>await getCompanyTrainersOrTrainees(skip, null, null, null, false, null)
    } catch (error) {
        console.error("Error getting user info:", error)
    }
}
const handleFilters = (isOpen: boolean) => {
    areFiltersVisible.value = isOpen
}
</script>

<style scoped>
.trainers {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 2em;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    min-width: 25em;
}

.pagination {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 1em;
}

.icons {
    padding: 1em;
    transition: 1ms ease-in;
    cursor: pointer;
}

.icons-disable {
    padding: 1em;
    color: var(--main-secondary-color);
    pointer-events: none;
}

.add-trainer {
    color: whitesmoke;
    padding: 0.5em 1.5em 0.5em 1.5em;
    background-color: var(--main-secondary-color);
    border: 1px solid var(--main-primary-color);
    border-radius: 10px;
    transition: 0.2s ease-in;
}

.add-trainer:hover {
    color: rgba(245, 245, 245, 0.7);
    background-color: var(--main-primary-color);
    border: 1px solid var(--button-border-color);
    border-radius: 10px;
    transition: 0.2s ease-out;
}

h2 {
    padding: 2em;
}

.filter-icon {
    position: relative;
    left: 13.8em;
    cursor: pointer;
}

@media screen and (max-width: 659px) {
    .trainers {
        margin: 0;
        width: 50%;
    }

    h1 {
        font-size: 1.5em;
    }
    .filter-icon {
        position: relative;
        left: 9.2em;
    }
}
</style>
