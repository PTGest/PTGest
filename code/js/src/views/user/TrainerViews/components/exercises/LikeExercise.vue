<template>
    <div @click="handleLike" class="like-box">
        <font-awesome-icon :class="[isLiked ?'heart-icon-active': 'heart-icon']" :icon="faHeart" />
    </div>
</template>

<script setup lang="ts">
import {faHeart} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {ref} from "vue";
import likeSet from "@/services/TrainerServices/sets/likeSet.js";
import unlikeSet from "@/services/TrainerServices/sets/unlikeSet.ts";
import likeExercise from "@/services/TrainerServices/exercises/likeExercise.ts";
import unlikeExercise from "@/services/TrainerServices/exercises/unlikeExercise.ts";

const props = defineProps<{
    exerciseId: number;
}>();

const isLiked = ref(false);

const handleLike = () => {
console.log('Liked', isLiked.value);
    if(!isLiked.value) {
        likeExercise(props.exerciseId);
        isLiked.value = true;
    }
    else{
        unlikeExercise(props.exerciseId);
        isLiked.value = false;
    }
    console.log('Liked', isLiked.value);
}

</script>


<style scoped>
.like-box{
    display: flex;
    justify-content: center;
    align-items: center;
    width : 2.5em;
    height: 2em;
    margin-left: 1em;
    background-color: var(--main-primary-color);
    border: 1px solid var(--main-secondary-color);
    border-radius: 5px;
    cursor: pointer;
    transition : background-color 0.3s;
}

.like-box:hover{
    background-color: var(--main-secondary-color);
}

.heart-icon, .heart-icon-active{
    width: 1em;
    height: 1em;
    color: rgba(245, 245, 245, 0.5);
}

.heart-icon-active{
    color: red;
}
</style>
