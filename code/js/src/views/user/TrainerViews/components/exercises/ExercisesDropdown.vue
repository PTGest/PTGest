<template>
    <div class="exercises-dropdown">
        <div @click="openDropdown" class="select">
            <span class="selected">{{ selected }}</span>
            <font-awesome-icon :class="[isDropdownOpen ? 'dropdown-icon-rotate' : 'dropdown-icon']" :icon="faChevronDown" />
        </div>

        <ul :class="[isDropdownOpen ? 'menu-open' : 'menu']">
            <li :class="{ active: selected === placeholder }" @click="selectOption(`${props.placeholder}`)">-</li>
            <li :class="{ active: selected === option.name }" v-for="option in props.options" :key="option.name" @click="selectOption(option)">{{ option.name }}</li>
        </ul>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faChevronDown } from "@fortawesome/free-solid-svg-icons"
import { ref } from "vue"
const isDropdownOpen = ref(false)

const emit = defineEmits(["dropdownOption"])
const props = defineProps<{
    options: any
    placeholder: string
}>()
const selected = ref(props.placeholder)

const selectOption = (option: any) => {
    if (option === props.placeholder) {
        selected.value = props.placeholder
        isDropdownOpen.value = false
        emit("dropdownOption", "")
    } else {
        selected.value = option.name
        isDropdownOpen.value = false
        emit("dropdownOption", option)
    }
}

const handleClick = (e: Event) => {
    const target = e.target as HTMLElement
    if (!target.closest(".exercises-dropdown")) {
        isDropdownOpen.value = false
    }
}

window.addEventListener("click", handleClick)

const openDropdown = () => {
    isDropdownOpen.value = !isDropdownOpen.value
}
</script>

<style scoped>
.exercises-dropdown {
    padding: 0.5em;
    position: relative;
}

.exercises-dropdown * {
    box-sizing: border-box;
}

.select {
    width: 20em;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.5em;
    background-color: var(--main-primary-color);
    border: 1px solid rgba(245, 245, 245, 0.2);
    border-radius: 5px;
    cursor: pointer;
    transition: background 0.3s;
}

.select:hover {
    background-color: var(--main-secondary-color);
}

.dropdown-icon,
.dropdown-icon-rotate {
    margin-right: 0.2em;
}

.dropdown-icon-rotate {
    transform: rotate(180deg);
}

.menu,
.menu-open {
    list-style-type: none;
    padding: 0 0 0 0.5em;
    background-color: var(--main-secondary-color);
    box-shadow: 0 0.5em 1em rgba(0, 0, 0, 0.2);
    border-radius: 5px;
    position: absolute;
    top: 2.4em;
    width: 20em;
    height: 0;
    z-index: 10;
    overflow-y: scroll;
    transition: 300ms cubic-bezier(0.77, 0, 0.18, 1);
}
.menu::-webkit-scrollbar,
.menu-open::-webkit-scrollbar {
    width: 7px;
}
.menu::-webkit-scrollbar-thumb,
.menu-open::-webkit-scrollbar-thumb {
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.menu li,
.menu-open li {
    padding: 0.7em;
    margin: 0.3em 1em 0.3em 0.2em;
    border-radius: 0.5em;
    cursor: pointer;
}
.menu li:hover,
.menu-open li:hover {
    background-color: var(--main-primary-color);
    color: whitesmoke;
}
.active {
    background-color: var(--main-primary-color);
    color: whitesmoke;
}

.text-fade-in {
    animation: text-fade-in 0.5s ease-in-out;
}

.menu-open {
    position: absolute;
    left: 0.5em;
    top: 2.4em;
    border: 1px solid rgba(245, 245, 245, 0.2);
    height: 15em;
    transition: 300ms cubic-bezier(0.77, 0, 0.18, 1);
}

@keyframes text-fade-in {
    0% {
        transform: translateX(-1em);
        opacity: 0;
    }
    100% {
        transform: translateX(0);
        opacity: 1;
    }
}
</style>
