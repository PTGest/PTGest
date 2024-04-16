<template>
    <button v-if="userData" :class="[is_open ? 'logout-button-open' : 'logout-button']" @click="logout">
        <div v-if="!is_mobile_view" class="logout-button-text">Log Out</div>
        <font-awesome-icon :icon="faRightToBracket"></font-awesome-icon>
    </button>
</template>

<script setup lang="ts">
import { faRightToBracket } from "@fortawesome/free-solid-svg-icons"
import { computed } from "vue"
import store from "../../../store"
import logoutServices from "../../../services/authServices/logoutServices.ts"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"

const props = defineProps({
    is_open: Boolean,
    is_mobile_view: Boolean,
})

let userData = computed(() => {
    return store.state.userData.id !== undefined
})

const logout = () => {
    logoutServices()
}
</script>

<style scoped>
@media screen and (max-width: 990px) {
    .logout-button {
        width: 4em;
    }
}
.logout-button,
.logout-button-open {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 4px;
    background-color: var(--sign-up-blue);
    color: white;
    padding: 1em;
    border-radius: 20px;
    cursor: pointer;
    transition: 0.2s ease-out;
    font-size: 0.8em;
}

.logout-button:hover,
.logout-button-open:hover {
    background-color: var(--primary-color);
    border-radius: 20px;
    transition: 0.1s ease-out;
}

.logout-button-open {
    position: relative;
    bottom: -36em;
    width: 13em;
    border-radius: 20px;
    transition: width 0.2s ease-out;
    font-size: 1em;
}
</style>
