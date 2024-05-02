<template>
    <div class="students">
        <h1>Students</h1>
        <router-link :to="{name: 'registerTrainee'}" class="add-student">
            <font-awesome-icon :icon="faPlus" class="plus-icon"></font-awesome-icon>
        </router-link>

    </div>
</template>


<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
</script>



<style scoped>
    .add-student{
        color: whitesmoke;
        padding: 0.5em 1.5em 0.5em 1.5em;
        background-color: var(--sign-up-blue);
        border-radius: 10px;
    }

    .add-student:hover{
        color: rgba(245, 245, 245, 0.7);
        background-color: var(--light-blue);
        border-radius: 10px;
    }

</style>
