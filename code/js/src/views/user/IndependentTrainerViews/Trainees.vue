<template>
    <div class="trainees">
        <h1>Trainees</h1>
        <router-link v-if="canEdit" :to="{ name: 'registerTrainee',  params: { isTrainee: true }}" class="add-trainee">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
        </router-link>
        <font-awesome-icon v-if="companyTraineesRef.trainees.length != 0" @click="handleFilters(true)" :icon="faFilter" class="filter-icon"></font-awesome-icon>
        <div v-for="trainee in companyTraineesRef.trainees" class="trainee">
            <TraineesBox :trainee="trainee"></TraineesBox>
        </div>
        <h2 v-if="companyTraineesRef.trainees.length == 0"> No More Trainees Available</h2>
        <div class="pagination">
            <font-awesome-icon @click="handlePage(false)"
                               :class="[skip == 0 ? 'icons-disable': 'icons']"
                               :icon="faChevronLeft"></font-awesome-icon>
            <font-awesome-icon @click="handlePage(true)"
                               :class="[companyTraineesRef.trainees.length > 0 ?'icons' :'icons-disable' ]"
                               :icon="faChevronRight"></font-awesome-icon>
        </div>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import {faChevronLeft, faChevronRight, faFilter, faPlus} from "@fortawesome/free-solid-svg-icons"
import store from "../../../store";
import router from "../../../plugins/router.ts";
import CompanyTrainers from "@/views/user/CompaniesViews/models/CompanyTrainers.ts";
import getCompanyTrainersOrTrainees from "@/services/companyServices/getCompanyTrainersOrTrainees.ts";
import {ref} from "vue";
import CompanyTrainees from "@/views/user/CompaniesViews/models/CompanyTrainees.ts";
import TraineesBox from "@/views/user/IndependentTrainerViews/components/TraineesBox.vue";

const skip = ref(0);
const areFiltersVisible = ref(false);
const role = store.getters.userData.role;
const route = router.currentRoute.value.meta.canEdit as string[];
const canEdit = route.includes(role);
const companyTraineesRef = ref<CompanyTrainees>({
    trainees: [],
    total: 0
});

(async () => {
    try {
        companyTraineesRef.value =
            <CompanyTrainees>await getCompanyTrainersOrTrainees(skip.value, null, null, null, true);
        console.log(companyTraineesRef.value)
    } catch (error) {
        console.error("Error getting user info:", error)
    }
})()

const getTrainees = async (skip: number) => {
    try {
        companyTraineesRef.value = <CompanyTrainees>await getCompanyTrainersOrTrainees(skip, null, null, null, true);
    } catch (error) {
        console.error("Error getting user info:", error)
    }
}
const handlePage = (isNext: boolean) => {
    if (isNext){
        skip.value += 4;
        getTrainees(skip.value)
        console.log(skip.value)
        console.log(companyTraineesRef.value)
    } else {
        console.log(skip.value)
        if (skip.value - 4 >= 0)
            skip.value -= 4;
        getTrainees(skip.value)
    }
}
const handleFilters = (isOpen : boolean) => {
    console.log(isOpen)
    areFiltersVisible.value = isOpen;
    console.log("Filters")
}

</script>

<style scoped>
.trainees {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 2em;
    background-color: var(--sign-up-blue);
    border-radius: 10px;
    min-width: 25em;
}

.add-trainee {
    color: whitesmoke;
    padding: 0.5em 1.5em 0.5em 1.5em;
    background-color: var(--sign-up-blue);
    border-radius: 10px;
}

.add-trainee:hover {
    color: rgba(245, 245, 245, 0.7);
    background-color: var(--light-blue);
    border-radius: 10px;
}

.filter-icon{
    position: relative;
    left: 9em;
    cursor: pointer;
}

h2{
    padding: 2em;
}

.pagination{
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap : 1em;
}

.icons{
    padding: 1em;
    transition: 1ms ease-in ;
    cursor: pointer;
}

.icons-disable{
    padding: 1em;
    color: var(--light-blue);
    pointer-events: none;
}
</style>
