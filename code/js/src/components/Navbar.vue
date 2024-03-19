
<template>
  <div class="navbar-container">
    <div class="navbar-icon">Icon</div>
    <div class="menu">
      <font-awesome-icon class="side-bar" @click="open" :icon= faBars />
      <div v-if="is_open" class="navbar-items">
        <div class="navbar-item"><router-link class="nav-link" link :to="{ name : 'home' }"> Home </router-link></div>
        <div class="navbar-item"><router-link class="nav-link" link :to="{ name : 'about' }"> About </router-link></div>
        <div class="navbar-item"><router-link class="nav-link" link :to="{ name : 'login' }"> Login </router-link></div>
      </div>
    </div>

  </div>

</template>


<script setup lang="ts">
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faBars} from "@fortawesome/free-solid-svg-icons";
import {ref} from 'vue';

let is_open = ref(false)
let showItems = ref(true)
const open = () => {
  is_open.value = !is_open.value
}
</script>

<style>

.navbar-container{
  background-color: var(--primary-color);
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  margin-bottom: 2em;
}
.navbar-icon{
  margin-left: 1em;
  padding: 1em;
}

@media (min-width: 700px){

  .navbar-items{
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    margin-right: 1em;
    gap: 1em;
  }
  .navbar-item{
    padding: 1em;
  }
  .nav-link{
    color: var(--tertiary-color);
  }
  .nav-link:hover{
    color: white;
  }
  .navbar-item:hover{
    background-color: #535bf2;
    border-radius: 10%;
  }
  .side-bar{
    visibility: hidden;
  }
}
@media (max-width: 699px) {
  .menu{
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    height: 100%;
    width: 5em;
    position: fixed;
    top: 0;
    right: 0;
    gap: 1em;
    background-color: var(--secundary-color);

  }
  .side-bar{
    visibility: visible;
    margin-top: 1em;
  }
  .navbar-items{
    display: flex;
    flex-direction: column;
    justify-content: center;

  }

}

</style>