<template>
    <div class="trainees">
        <h1>Trainees</h1>
        <router-link v-if="canEdit" :to="{ name: 'registerTrainee', params: { isTrainee: true } }" class="add-trainee">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
        </router-link>
        <font-awesome-icon v-if="traineesRef.trainees.length != 0" @click="handleFilters(true)" :icon="faFilter" class="filter-icon"></font-awesome-icon>
        <div v-for="trainee in traineesRef.trainees" class="trainee">
            <TraineesBox :trainee="trainee"></TraineesBox>
        </div>
        <h2 v-if="traineesRef.trainees.length == 0">No More Trainees Available</h2>
        <div class="pagination">
            <font-awesome-icon @click="handlePage(false)" :class="[skip == 0 ? 'icons-disable' : 'icons']" :icon="faChevronLeft"></font-awesome-icon>
            <font-awesome-icon @click="handlePage(true)" :class="[traineesRef.trainees.length > 0 ? 'icons' : 'icons-disable']" :icon="faChevronRight"></font-awesome-icon>
        </div>
        <Filters v-if="areFiltersVisible" @visible="handleFilters($event)"></Filters>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faChevronLeft, faChevronRight, faFilter, faPlus } from "@fortawesome/free-solid-svg-icons"
import store from "../../../store"
import router from "../../../plugins/router.ts"
import getCompanyTrainersOrTrainees from "../../../services/companyServices/getCompanyTrainersOrTrainees.ts"
import { ref } from "vue"
import CompanyTrainees from "../companiesViews/models/CompanyTrainees.ts"
import TraineesBox from "./components/trainees/TraineesBox.vue"
import Filters from "@/views/user/companiesViews/components/Filters.vue"
import RBAC from "@/services/utils/RBAC/RBAC.ts"
import TrainerTrainees from ".//models/trainees/TrainerTrainees.ts"
import getTrainerTrainees from "@/services/trainerServices/trainees/traineesServices.ts"

const skip = ref(0)
const areFiltersVisible = ref(false)
const role = store.getters.userData.role
const route = router.currentRoute.value.meta.canEdit as string[]
const canEdit = route.includes(role)
const traineesRef = ref<CompanyTrainees | TrainerTrainees>({
    trainees: [],
    total: 0,
})

;(async () => {
    try {
        if (RBAC.isCompany()) {
            traineesRef.value = <CompanyTrainees>await getCompanyTrainersOrTrainees(skip.value, null, null, null, true, null)
        } else {
            traineesRef.value = <TrainerTrainees>await getTrainerTrainees(skip.value, null, null, null)
        }
    } catch (error) {
        console.error("Error getting user info:", error)
    }
})()

const getTrainees = async (skip: number) => {
    try {
        if (RBAC.isCompany()) traineesRef.value = <CompanyTrainees>await getCompanyTrainersOrTrainees(skip, null, null, null, true, null)
        else traineesRef.value = <TrainerTrainees>await getTrainerTrainees(skip, null, null, null)
    } catch (error) {
        console.error("Error getting user info:", error)
    }
}
const handlePage = (isNext: boolean) => {
    if (isNext) {
        skip.value += 4
        getTrainees(skip.value)
    } else {
        if (skip.value - 4 >= 0) skip.value -= 4
        getTrainees(skip.value)
    }
}
const handleFilters = (isOpen: boolean) => {
    areFiltersVisible.value = isOpen
}
</script>

<style scoped>
.trainees {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 2em;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    min-width: 25em;
}

.add-trainee {
    color: whitesmoke;
    padding: 0.5em 1.5em 0.5em 1.5em;
    background-color: var(--main-secondary-color);
    border: 1px solid var(--main-primary-color);
    border-radius: 10px;
    transition: 0.2s ease-in;
}

.add-trainee:hover {
    color: rgba(245, 245, 245, 0.7);
    background-color: var(--main-primary-color);
    border: 1px solid rgba(96, 96, 250, 0.7);
    border-radius: 10px;
    transition: 0.2s ease-out;
}

.filter-icon {
    position: relative;
    left: 13.3em;
    cursor: pointer;
}

h2 {
    padding: 2em;
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
</style>
