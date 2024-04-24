<template>
    <div class="dropdown-menu-container">
        <div :class="[gender === '' ? 'placeholder' : 'option']">{{ gender }}</div>
        <font-awesome-icon :class="[is_open ? 'drop-icon-open' : 'drop-icon']" :icon="faChevronDown" @click="toggleOpen"></font-awesome-icon>
        <div v-if="is_open" class="dropdown">
            <div
                class="drop-option"
                @click="
                    () => {
                        $emit('gender', 'Male')
                        optionChange('Male')
                    }
                "
            >
                Male
            </div>

            <div
                class="drop-option"
                @click="
                    () => {
                        $emit('gender', 'Female')
                        optionChange('Female')
                    }
                "
            >
                Female
            </div>

            <div
                class="drop-option"
                @click="
                    () => {
                        $emit('gender', 'Undefined')
                        optionChange('Undefined')
                    }
                "
            >
                Undefined
            </div>

            <div
                class="drop-option"
                @click="
                    () => {
                        $emit('gender', 'Other')
                        optionChange('Other')
                    }
                "
            >
                Other
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faChevronDown } from "@fortawesome/free-solid-svg-icons"
import { defineProps, ref } from "vue"

const props = defineProps<{
    size: string,
    arrow_right: string,
}>()

let is_open = ref(false)
const toggleOpen = () => {
    is_open.value = !is_open.value
}

let gender = ref("Gender")
let optionChange = (option: string) => {
    gender.value = option
    toggleOpen()
}
</script>

<style scoped>
.dropdown-menu-container {
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
    background-color: whitesmoke;
    width: v-bind(size);
    height: 2em;
    border-radius: 5px;
}

.dropdown {
    position: relative;
    top: 6.5em;
    left: -5em;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border-radius: 5px;
    background-color: whitesmoke;
    z-index: 998;
}

.drop-option {
    width: 18.5em;
    list-style-type: none;
    padding: 0.5em 0 0.5em 0;
    transition: 0.1s ease-in;
    color: whitesmoke;
    background-color: #263238;
}

.drop-option:hover {
    cursor: pointer;
    background-color: whitesmoke;
    color: var(--black1a);
    transition: 0.1s ease-out;
}

.drop-icon,
.drop-icon-open {
    position: relative;
    right: v-bind(arrow_right);
    padding: 1em;
    color: var(--sign-up-black);
    cursor: pointer;
}

.drop-icon-open {
    transform: rotateZ(-180deg);
}

.option,
.placeholder {
    max-width: 1em;
    padding: 0.5em;
    color: var(--black1a);
}

.placeholder {
    margin-left: 0.5em;
    font-size: 0.8em;
    color: #757575;
}
</style>
