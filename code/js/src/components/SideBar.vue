<template>
  <div :class="[is_open ? 'side-bar-container-open' : 'side-bar-container']">
    <img  src="../assets/PTGest.png" alt="Logo" class="logo"/>
    <div :class="[is_open ? 'side-bar-icon-open' : 'side-bar-icon']">
      <font-awesome-icon :icon=faBars @click="open"></font-awesome-icon>
    </div>
    <div class="menu">
      <div :class="[is_open ? 'navbar-items-open' : 'navbar-items']">

        <router-link :to="{ name : 'home' }" class="nav-link" link>
          <font-awesome-icon v-if="is_open || is_mobile_view" :icon=faHouse></font-awesome-icon>
          <div v-if="!is_mobile_view || is_mobile_view && is_open" class="navbar-item">Home</div>
        </router-link>


        <router-link :to="{ name : 'about' }" class="nav-link" link>
          <font-awesome-icon v-if="is_open || is_mobile_view" :icon=faAddressCard></font-awesome-icon>
          <div v-if="!is_mobile_view || is_mobile_view && is_open" class="navbar-item">About</div>
        </router-link>


        <router-link v-if="is_signed_in" :to="{ name : 'login' }" class="nav-link" link>
          <font-awesome-icon v-if="is_open || is_mobile_view" :icon=faRightToBracket></font-awesome-icon>
          <div v-if="!is_mobile_view || is_mobile_view && is_open" class="navbar-item">Login</div>
        </router-link>

        <router-link :to="{ name : 'signup' }" class="nav-link" link>
          <font-awesome-icon v-if="is_open || is_mobile_view" :icon=faUserPlus></font-awesome-icon>
          <div v-if="!is_mobile_view || is_mobile_view && is_open" class="navbar-item">Signup</div>
        </router-link>

      </div>
    </div>

  </div>

</template>


<script lang="ts" setup>
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faBars, faHouse, faRightToBracket, faAddressCard, faUserPlus} from "@fortawesome/free-solid-svg-icons";
import {ref} from 'vue';

let is_mobile_view = ref(false)
let is_open = ref(false)
let is_signed_in = ref(false)
const open = () => {
  is_open.value = !is_open.value
}

const handleResize = () => {
  is_mobile_view.value = window.innerWidth <= 990;
  console.log("This is in Mobile View", is_mobile_view.value)
}



window.addEventListener('resize', handleResize)

</script>

<style scoped>

.logo{
  width: 4em;
  height: 4em;
  margin-top: 1em;
  margin-bottom: 2em;
  border-radius: 30%;
}

.side-bar-container, .side-bar-container-open {
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

.side-bar-icon, .side-bar-icon-open {
  cursor: pointer;
  transition: 2s ease-in;
}

.side-bar-icon-open {
  position: relative;
  top: -6em;
  right: -6em;
  transition: 2s ease-out;
}


.navbar-items, .navbar-items-open {
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
  align-items: center;
}

.nav-link {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  color: white;
  gap: 1em;
  padding: 1em;
  text-decoration: none;
}


.nav-link:hover {
  background-color: #535bf2;
  border-radius: 10%;
}
</style>