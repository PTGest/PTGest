<template>
    <div class="Trainees">
        <h1>Trainees</h1>
        <router-link v-if="canEdit" :to="{ name: 'registerTrainee',  params: { isTrainee: true }}" class="add-trainee">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
        </router-link>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faPlus } from "@fortawesome/free-solid-svg-icons"
import store from "../../../store";
import router from "../../../plugins/router.ts";


const role = store.getters.userData.role;
const route = router.currentRoute.value.meta.canEdit as string[];
const canEdit = route.includes(role);
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
