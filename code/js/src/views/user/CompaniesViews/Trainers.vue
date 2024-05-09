<template>
    <div class="Trainers">
        <h1>Trainers</h1>
        <router-link :to="{ name: 'registerTrainer',  params: { isTrainee: false }}" class="add-trainer">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
        </router-link>
        <div v-for="trainer in companyTrainersRef.trainers" class="trainer">
            <TrainerBox :trainer="trainer"></TrainerBox>
        </div>
    </div>
</template>

<script setup lang="ts">
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {Ref, ref} from "vue";
import CompanyTrainers from "../../../views/user/CompaniesViews/models/CompanyTrainers.ts";
import getCompanyTrainers from "../../../services/companyServices/getCompanyTrainers.ts";
import TrainerBox from "../../../views/user/CompaniesViews/Components/TrainerBox.vue";

const companyTrainersRef : Ref<CompanyTrainers> = ref({
    trainers: [],
    total: 0
});

(async () => {
 try {
    companyTrainersRef.value = await getCompanyTrainers();
 } catch (error) {
     console.error("Error getting user info:", error)
 }
})()
</script>

<style scoped>
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
</style>
