<template>
    <Suspense>
        <template #default>
            <div class="trainers">
                <h1>Trainers</h1>
                <router-link :to="{ name: 'registerTrainer',  params: { isTrainee: false }}" class="add-trainer">
                    <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
                </router-link>
                <font-awesome-icon @click="handleFilters" :icon="faFilter" class="filter-icon"></font-awesome-icon>
                <Filters v-if="areFiltersVisible"></Filters>
                <div v-for="trainer in companyTrainersRef.trainers" class="trainer">
                    <TrainerBox :trainer="trainer"></TrainerBox>
                </div>
                <h2 v-if="companyTrainersRef.trainers.length == 0"> No More Trainers Available</h2>
                <div class="pagination">
                    <font-awesome-icon @click="handlePage(false)"
                                       :class="[skip == 0 ? 'icons-disable': 'icons']"
                                       :icon="faChevronLeft"></font-awesome-icon>
                    <font-awesome-icon @click="handlePage(true)"
                                       :class="[companyTrainersRef.trainers.length > 0 ?'icons' :'icons-disable' ]"
                                       :icon="faChevronRight"></font-awesome-icon>
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
import {faChevronLeft, faChevronRight, faFilter, faPlus} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {Ref, ref} from "vue";
import CompanyTrainers from "../../../views/user/CompaniesViews/models/CompanyTrainers.ts";
import getCompanyTrainers from "../../../services/companyServices/getCompanyTrainers.ts";
import TrainerBox from "../../../views/user/CompaniesViews/Components/TrainerBox.vue";
import Filters from "@/views/user/CompaniesViews/Components/Filters.vue";


const companyTrainersRef : Ref<CompanyTrainers> = ref({
    trainers: [],
    total: 0
});

const skip = ref(0) ;
const areFiltersVisible = ref(false);
(async () => {
 try {
    companyTrainersRef.value = await getCompanyTrainers(skip.value);
 } catch (error) {
     console.error("Error getting user info:", error)
 }
})()

const handlePage = (isNext: boolean) => {
   if (isNext){
       skip.value += 4;
       getTrainers(skip.value)
       console.log(skip.value)
       console.log(companyTrainersRef.value)
   } else {
       console.log(skip.value)
       if (skip.value - 4 >= 0)
       skip.value -= 4;
       getTrainers(skip.value)
   }
}
const getTrainers = async (skip: number) => {
    try {
        companyTrainersRef.value = await getCompanyTrainers(skip);
    } catch (error) {
        console.error("Error getting user info:", error)
    }
}
const handleFilters = () => {
    areFiltersVisible.value = !areFiltersVisible.value;
    console.log("Filters")
}

</script>

<style scoped>
.trainers {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 2em;
    background-color: var(--sign-up-blue);
    border-radius: 10px;
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
}

.icons-disable{
    padding: 1em;
    color: var(--light-blue);
    pointer-events: none;
}

.add-trainer {
    color: whitesmoke;
    padding: 0.5em 1.5em 0.5em 1.5em;
    background-color: var(--sign-up-blue);
    border-radius: 10px;
}

.add-trainer:hover {
    color: rgba(245, 245, 245, 0.7);
    background-color: var(--light-blue);
    border-radius: 10px;
}

h2{
    padding: 2em;
}

.filter-icon{
    position: relative;
    left: 14em;
}

</style>
