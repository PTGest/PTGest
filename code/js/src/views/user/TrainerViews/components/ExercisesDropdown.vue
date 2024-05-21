<template>

    <div class="exercises-dropdown">
        <div @click="openDropdown" class="select">
            <span class="selected">Modality</span>
            <font-awesome-icon :class="[isDropdownOpen ? 'dropdown-icon' : 'dropdown-icon-rotate']" :icon="faChevronDown"/>
        </div>
        <div class="dropdown-body">
            <slot></slot>
        </div>

    </div>

    <ul :class="[isDropdownOpen ? 'menu-open' : 'menu']">
        <li class="active">-</li>
        <li>Activation</li>
        <li>Agility</li>
        <li>Cardio</li>
        <li>Conditioning</li>
        <li>Mobility</li>
        <li>Power</li>
        <li>Strength</li>
    </ul>

</template>

<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {ref} from "vue";

const isDropdownOpen = ref(false);

const openDropdown = () => {
    isDropdownOpen.value = !isDropdownOpen.value;
}


</script>



<style scoped>

.exercises-dropdown{
   min-width: 15em;
    position: relative;
    margin: 2em;
}

.exercises-dropdown *{
    box-sizing: border-box;
}

.select{
    width: 10em;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.5em;
    background-color: var(--primary-color);
    border: 1px solid rgba(245, 245, 245, 0.2);
    border-radius: 10px;
    cursor: pointer;
    transition: background 0.3s;
}

.select:hover{
    background-color: var(--secundary-color);
}

.dropdown-icon, .dropdown-icon-rotate{
    margin-right: 0.2em;
}

.dropdown-icon-rotate{
    transform: rotate(180deg);
}

.menu, .menu-open{
    list-style-type: none;
    padding: 0 0 0 0.5em ;
    background-color: var(--secundary-color);
    box-shadow: 0 0.5em 1em rgba(0, 0, 0, 0.2);
    border-radius: 10px;
    position: relative;
    top: -1em;
    left: 1.8em;
    width: 10em;
    height: 0;
    z-index: 10;
    overflow-y: scroll;
    transition: 300ms cubic-bezier(0.77, 0, 0.18, 1);
}

.menu::-webkit-scrollbar, .menu-open::-webkit-scrollbar{
    width: 7px;

}
.menu::-webkit-scrollbar-thumb, .menu-open::-webkit-scrollbar-thumb{
    background-color: var(--primary-color);
    border-radius: 10px;
}

.menu li, .menu-open li{
    padding: 0.7em;
    margin: 0.3em 1em 0.3em 0.2em;
    border-radius: 0.5em;
    cursor: pointer;
}
.menu li:hover, .menu-open li:hover{
    background-color: var(--primary-color);
    color: whitesmoke;
}
.active{
    background-color: var(--primary-color);
    color: whitesmoke;
}

.text-fade-in{
    animation: text-fade-in 0.5s ease-in-out;
}

.menu-open{
    border: 1px solid rgba(245, 245, 245, 0.2);
    height: 15em;
}
@keyframes text-fade-in {
    0%{
       transform: translateX(-1em);
        opacity: 0;
    }
    100%{
        transform: translateX(0);
        opacity: 1;
    }
}

</style>
