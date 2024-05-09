<template>
    <div class="Trainees">
        <h1>Trainees</h1>
        <router-link :to="{ name: 'registerTrainee',  params: { isTrainee: true }}" class="add-trainee">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
        </router-link>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faPlus } from "@fortawesome/free-solid-svg-icons"
import {ref, Ref} from "vue";
import getAllTrainees from "../../../services/companyServices/getCompanyTrainers.ts";



// const traineesRef : Ref<Trainees> = ref({
//     trainers: [],
//     total: 0
// });

// (async () => {
//     try {
//         companyTrainersRef.value = await getCompanyTrainers();
//     } catch (error) {
//         console.error("Error getting user info:", error)
//     }
// })()


</script>

<style scoped>
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
</style>
