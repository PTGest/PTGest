<template>
    <div class="input-bar-container">
        <div :class="props.className">{{ props.text }}</div>
        <div class="input-icon-container">
            <input v-model="value" @change="$emit('value', value)" :type="is_visible ? 'password' : ''" class="signup-input-base" :placeholder="placeholder" />
            <font-awesome-icon v-if="is_Password" :icon="faEye" class="visible-icon" @click="updateVisibility"></font-awesome-icon>
        </div>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { ref } from "vue"
import { faEye } from "@fortawesome/free-solid-svg-icons"

let value = ref("")
const props = defineProps<{
    padding: { type: string; default: "0em 1em 0em 1em"; required: false }
    width: { type: string; default: "20em"; required: false }
    height: { type: string; default: "2.5em"; required: false }
    text: string
    className: string
    is_Password: boolean
    placeholder: string
}>()
const padding = ref(props.padding)
const width = ref(props.width)
console.log(props)
console.log(props)
const height = ref(props.height)

const is_visible = ref(props.is_Password)

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
    color: var(--primary-color);
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
    color: var(--secundary-color);
}
</style>
