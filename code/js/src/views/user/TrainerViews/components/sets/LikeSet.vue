<template>
    <div @click="handleLike" class="like-box">
        <font-awesome-icon :class="[isLiked ? 'heart-icon-active' : 'heart-icon']" :icon="faHeart" />
    </div>
</template>

<script setup lang="ts">
import { faHeart } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { ref } from "vue"
import {unlikeSet, likeSet} from "@/services/TrainerServices/sets/setServices.js";



const props = defineProps<{
    setId: number
    isFavorite: boolean
}>()

const isLiked = ref(props.isFavorite)

const handleLike = () => {
    if (!isLiked.value) {
        likeSet(props.setId)
        isLiked.value = true
    } else {
        unlikeSet(props.setId)
        isLiked.value = false
    }
    console.log("Liked", isLiked.value)
}
</script>

<style scoped>
.like-box {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 2.5em;
    height: 2em;
    background-color: var(--main-primary-color);
    border: 1px solid var(--main-secondary-color);
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.like-box:hover {
    background-color: var(--main-secondary-color);
}

.heart-icon,
.heart-icon-active {
    width: 1em;
    height: 1em;
    color: rgba(245, 245, 245, 0.5);
}

.heart-icon-active {
    color: red;
}
</style>
