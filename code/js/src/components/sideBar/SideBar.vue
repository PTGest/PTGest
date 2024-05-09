<template>
    <div :class="[is_open ? 'side-bar-container-open' : 'side-bar-container']">
        <img src="../../assets/PTGest.png" alt="Logo" class="logo" />
        <div :class="[is_open ? 'side-bar-icon-open' : 'side-bar-icon']">
            <font-awesome-icon :icon="faBars" @click="open"></font-awesome-icon>
        </div>
        <div class="menu">
            <div :class="[is_open ? 'navbar-items-open' : 'navbar-items']">
                <router-link @click="handleMenu" :to="{ name: 'home' }" class="nav-link" link>
                    <font-awesome-icon v-if="is_open" :icon="faHouse"></font-awesome-icon>
                    <div v-if="!is_mobile_view || (is_mobile_view && is_open)" class="navbar-item">Home</div>
                </router-link>

                <template v-if="!userData">
                    <router-link  @click="handleMenu" :to="{ name: 'login' }" class="nav-link" link>
                        <font-awesome-icon v-if="is_open || is_mobile_view" :icon="faUser"></font-awesome-icon>
                        <div v-if="!is_mobile_view || (is_mobile_view && is_open)" class="navbar-item">Login</div>
                    </router-link>

                    <router-link @click="handleMenu" :to="{ name: 'signup' }" class="nav-link" link>
                        <font-awesome-icon v-if="is_open || is_mobile_view" :icon="faUserPlus"></font-awesome-icon>
                        <div v-if="!is_mobile_view || (is_mobile_view && is_open)" class="navbar-item">Signup</div>
                    </router-link>
                </template>
                <router-link @click="handleMenu" v-if="userData && (!is_mobile_view || (is_mobile_view && is_open))" class="nav-link" :to="{ name: 'trainees'}">Trainees</router-link>
                <router-link @click="handleMenu" v-if="userData && (!is_mobile_view || (is_mobile_view && is_open))" class="nav-link" :to="{ name: 'trainers'}">Trainers</router-link>
                <UserIcon @click="handleMenu" class="userIcon" :isOpen="is_open" v-if="userData && (!is_mobile_view || (is_mobile_view && is_open)) " :isMobileView="is_mobile_view" />
                <LogoutButton :isOpen="is_open" v-if="userData && (!is_mobile_view || (is_mobile_view && is_open))" :isMobileView="is_mobile_view" />
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faBars, faHouse, faUserPlus, faUser } from "@fortawesome/free-solid-svg-icons"
import { computed, ref } from "vue"
import store from "../../store"
import LogoutButton from "./components/LogoutButton.vue"
import UserIcon from "../../components/sideBar/components/UserIcon.vue"

// Define a computed property to track changes to userData
const userData = computed(() => {
    return store.state.userData.token !== undefined
})

// Define other reactive variables
const is_mobile_view = store.getters.is_mobile_view === "true" ? ref(true) : ref(false)
const is_open = ref(false)


const open = () => {
    is_open.value = !is_open.value
}

const handleMenu = () => {
    if (is_mobile_view.value) {
        is_open.value = false
    }
}

const handleResize = () => {
    is_mobile_view.value = window.innerWidth <= 659
    store.dispatch("setMobile", is_mobile_view.value)
}

window.addEventListener("resize", handleResize)
</script>

<style scoped>
.logo {
    width: 4em;
    height: 4em;
    margin-top: 1em;
    margin-bottom: 2em;
    border-radius: 30%;
}

.side-bar-container,
.side-bar-container-open {
    background-color: var(--primary-color);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: start;
    position: absolute;
    top: 0;
    left: 0;
    width: 7em;
    height: 100vh;
    gap: 1em;
    transition: 0.2s ease-out;
    z-index: 999;
}

.side-bar-container-open {
    width: 15em;
    transition: 0.2s ease-out;
}

.side-icon {
    padding: 1em;
}

.side-bar-icon,
.side-bar-icon-open {
    cursor: pointer;
    transition: 2s ease-in;
}

.side-bar-icon-open {
    position: relative;
    top: -6em;
    right: -6em;
    transition: 2s ease-out;
}

.navbar-items,
.navbar-items-open {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;
    gap: 1em;
}

.navbar-items-open {
    position: relative;
    top: -4em;
}

.navbar-item {
    display: flex;
    flex-direction: row;
    justify-content: center;
    margin: 0 1em 0 1em;
    align-items: center;
}

.nav-link {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    color: white;
    padding: 1em;
    text-decoration: none;
}

.nav-link:hover {
    background-color: var(--nav-bar-blue);
    border-radius: 10%;
}

@media screen and (max-width: 659px) {
    .logo {
        position: relative;
        top: 0.5em;
        left: 1em;
        width: 2em;
        height: 2em;
        border-radius: 30%;
    }
    .side-bar-icon{
        position: relative;
        right: calc(-100% + 5em);
        transition: 0.1s ease-in;
    }
    .side-bar-icon-open{
        position: relative;
        right: calc(-100% + 15em);
        top: -5em;
        transition: 0.1s ease-out;
    }
    .side-bar-container
     {
        background-color: var(--primary-color);
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: start;
        position: absolute;
        top: 0;
        left: 0;
        width: 100vw;
        height: 4em;
        gap: 1em;
        transition: 0.2s ease-out;
        z-index: 999;
    }

    .side-bar-container-open {
        width: 100vw;
        height: 100vh;
        transition: 0.2s ease-out;
    }
    .navbar-items{
        position: relative;
        right: -6em;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        width: 100%;
    }

    .students {
        color: white;
    }
}
</style>
