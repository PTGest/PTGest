<template>
  <div :class="[is_open ? 'side-bar-container-open' : 'side-bar-container']">
    <div class="side-icon">Icon</div>
    <div :class="[is_open ? 'side-bar-icon-open' : 'side-bar-icon']">
      <font-awesome-icon :icon=faBars/ @click="open">
    </div>
    <div class="menu">
      <div :class="[is_open ? 'navbar-items-open' : 'navbar-items']">
        <div class="navbar-item">
          <font-awesome-icon v-if="is_open" :icon=faHouse></font-awesome-icon>
          <div>
            <router-link :to="{ name : 'home' }" class="nav-link" link> Home</router-link>
          </div>
        </div>

        <div class="navbar-item">
          <router-link :to="{ name : 'about' }" class="nav-link" link> About</router-link>
        </div>
        <div class="navbar-item">
          <router-link :to="{ name : 'login' }" class="nav-link" link> Login</router-link>
        </div>
      </div>
    </div>

  </div>

</template>


<script lang="ts" setup>
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faBars, faHouse} from "@fortawesome/free-solid-svg-icons";
import {ref} from 'vue';


let is_open = ref(false)
const open = () => {
  is_open.value = !is_open.value
}
</script>

<style>

.side-bar-container, .side-bar-container-open {
  background-color: var(--primary-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: start;
  position: absolute;
  top: 0;
  left: 0;
  width: 5em;
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
  padding: 1em;
  transition: 2s ease-in;
}

.side-bar-icon-open {
  position: relative;
  top: -4.5em;
  right: -6em;
  transition: 2s ease-out;
}


.navbar-items, .navbar-items-open {
  margin-top: 1em;
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
  gap: 1em;
  padding: 1em;
}

.nav-link {
  color: white;
}

.nav-link:hover {
  color: white;
}

.navbar-item:hover {
  background-color: #535bf2;
  border-radius: 10%;
}

@media (max-width: 699px) {


}

</style>