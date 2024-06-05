<template>
    <div class="input-bar-container">
        <div :class="props.className">{{ props.text }}</div>
        <div class="input-icon-container">
            <input v-model="value" @change="$emit('value', value)" :type="is_visible ? 'password' : ''" class="signup-input-base" :placeholder="placeholder" />
            <font-awesome-icon v-if="isPassword" :icon="faEye" class="visible-icon" @click="updateVisibility"></font-awesome-icon>
        </div>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { ref } from "vue"
import { faEye } from "@fortawesome/free-solid-svg-icons"

const value = ref("")
const props = defineProps<{
    padding: string
    width: string
    height: string
    text: string
    className: string
    isPassword: boolean
    placeholder: string
}>()

const is_visible = ref(props.isPassword)

const updateVisibility = () => {
    is_visible.value = !is_visible.value
}
</script>

<style scoped>
.signup-input-base,
.signup-birth-input-placeholder {
    height: v-bind(height);
    width: v-bind(width);
    border-radius: 5px;
    border: 0;
    padding: v-bind(padding);
    color: var(--main-primary-color);
    background-color: whitesmoke;
}

.input-bar-container {
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: start;
    margin-bottom: 1em;
}

.input-icon-container {
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
}

.visible-icon {
    margin-left: -2em;
    color: var(--main-secondary-color);
}
</style>
