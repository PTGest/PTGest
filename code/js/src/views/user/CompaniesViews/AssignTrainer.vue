<template>
    <div v-for="trainer in companyTrainersRef.trainers" class="trainer">
        <TrainerBox v-if="$router.currentRoute.value.params.assignTrainer == 'assignTrainer'" :trainer="trainer" is-assign-trainer></TrainerBox>
        <TrainerBox v-else :trainer="trainer" is-reassign-trainer></TrainerBox>
    </div>
</template>

<script setup lang="ts">
import TrainerBox from "../../../views/user/CompaniesViews/Components/TrainerBox.vue"
import { Ref, ref } from "vue"
import getCompanyTrainersOrTrainees from "../../../services/companyServices/getCompanyTrainersOrTrainees.ts"
import CompanyTrainers from "../../../views/user/CompaniesViews/models/CompanyTrainers.ts"

const skip = ref(0)

const companyTrainersRef: Ref<CompanyTrainers> = ref({
    trainers: [],
    total: 0,
});

(async () => {
    try {
        companyTrainersRef.value = <CompanyTrainers>await getCompanyTrainersOrTrainees(skip.value, null, null, null, false, null)
    } catch (error) {
        console.error("Error getting user info:", error)
    }
})()
</script>

<style scoped></style>
