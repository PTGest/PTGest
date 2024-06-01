<template>
    <div @click="handleLike" class="like-box">
        <font-awesome-icon :class="[isLiked ?'heart-icon-active': 'heart-icon']" :icon="faHeart" />
    </div>
</template>

<script setup lang="ts">
import {faHeart} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {ref} from "vue";

const props = defineProps<{
    setId: number;
}>();

const isLiked = ref(false);

const handleLike = () => {
    isLiked.value = !isLiked.value;
    console.log('Liked');
}

</script>


<style scoped>
.like-box{
    display: flex;
    justify-content: center;
    align-items: center;
    width : 2.5em;
    height: 2em;
    background-color: var(--main-primary-color);
    border: 1px solid var(--main-secundary-color);
    border-radius: 5px;
    cursor: pointer;
    transition : background-color 0.3s;
}

.like-box:hover{
    background-color: var(--main-secundary-color);
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
