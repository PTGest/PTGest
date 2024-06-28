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
    size: string
    arrowRight: string
}>()

const is_open = ref(false)
const toggleOpen = () => {
    is_open.value = !is_open.value
}

const gender = ref("Gender")
const optionChange = (option: string) => {
    gender.value = option
    toggleOpen()
}
</script>

<style scoped>
.dropdown-menu-container {
    position: relative;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    background-color: whitesmoke;
    width: v-bind(size);
    height: 2em;
    border-radius: 5px;
}

.dropdown {
    position: absolute;
    top: 2.5em;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border-radius: 10px;
    background-color: whitesmoke;
    z-index: 998;
}

.drop-option {
    display: flex;
    justify-content: center;
    width: v-bind(size);
    list-style-type: none;
    padding: 0.5em 0 0.5em 0;
    transition: 0.1s ease-in;
    color: var(--main-primary-color);
    background-color: transparent;
}

.drop-option:hover {
    cursor: pointer;
    background-color: var(--main-primary-color);
    border-radius: 5px;
    color: whitesmoke;
    transition: 0.1s ease-out;
}

.drop-icon,
.drop-icon-open {
    position: relative;
    padding: 1em;
    color: var(--sign-up-black);
    cursor: pointer;
}

.drop-icon-open {
    right: v-bind(arrowRight);
    transform: rotateZ(-180deg);
}

.option {
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
