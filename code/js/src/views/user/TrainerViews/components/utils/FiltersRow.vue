<template>
    <div class="input-row">
        <font-awesome-icon class="search-icon" :icon="faMagnifyingGlass" />
        <input @change="handleInput" v-model="searchBar" class="bar" :placeholder="props.placeholder" />
        <button class="filters" @click="handleFiltersView">
            <font-awesome-icon :icon="faFilter" />
            Filters
        </button>
        <font-awesome-icon @click="resetFilters" class="reset-filters-icon" :icon="faArrowsRotate" />
    </div>
</template>

<script setup lang="ts">
import {faArrowsRotate, faFilter, faMagnifyingGlass} from "@fortawesome/free-solid-svg-icons";
import {ref} from "vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
const searchBar = ref("")
const emits = defineEmits(["input", "open","reset"])

const props = defineProps<{
    placeholder: string
}>()
const handleInput = () => {
    emits("input", searchBar.value)
    console.log(searchBar.value)
}

const handleFiltersView = () => {
    emits("open")
}

const resetFilters = () => {
    searchBar.value = ""
    emits("input", searchBar.value)
    emits("reset")
}

</script>



<style scoped>
.input-row {
    position: relative;
    left: -0.5em;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 1em;
    margin-bottom: 1em;
    gap: 1em;
}

.search-icon {
    position: relative;
    right: -1.5em;
}

.bar {
    margin-left: 1em;
    margin-bottom: 0;
    height: 2.5em;
    width: 100%;
    padding: 0.5em;
    border-radius: 5px;
    font-family: Poppins, sans-serif;
    font-size: 0.9rem;
    border: none;
}

.filters {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    background-color: var(--main-secondary-color);
    padding: 0.5em;
    border-radius: 5px;
    font-family: Poppins, sans-serif;
    font-size: 1.05rem;
    color: whitesmoke;
    transition: 0.2s ease-in;
}

.filters:hover {
    background-color: var(--light-blue);
    transition: 0.2s ease-out;
}

.reset-filters-icon{
    color: whitesmoke;
    font-size: 1.1rem;
    cursor: pointer;
    transition: 0.2s ease-in;
}
</style>
