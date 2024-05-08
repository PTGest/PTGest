<template>
    <div class="Trainers">
        <h1>Trainers</h1>
        <router-link :to="{ name: 'registerTrainer' }" class="add-trainer">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
        </router-link>
        <div v-for="trainer in trainers.trainers" class="trainer">
            <TrainerBox :trainer="trainer" ></TrainerBox>
        </div>
    </div>
</template>

<script setup lang="ts">
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {Ref, ref} from "vue";
import CompanyTrainers from "../../../views/user/CompaniesViews/models/CompanyTrainers.ts";
//import getCompanyTrainers from "../../../services/companyServices/getCompanyTrainers.ts";
import Trainer from "../../../views/user/CompaniesViews/models/Trainer.ts";
import TrainerBox from "@/views/user/CompaniesViews/Components/TrainerBox.vue";

const trainers : Ref<CompanyTrainers> = ref({
    trainers: [new Trainer( 1, "John", "MALE", 1, 3 )],
    total: 0
});

// (async () => {
//     try {
//          await getCompanyTrainers();
//     } catch (error) {
//         console.error("Error getting user info:", error)
//     }
// })()
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
