<template>
    <RouterLink class="user-icon-container" :to="{name: 'userProfile', params:{userId:userId}}">
        <img class="user-icon" src="../../../assets/userIcons/man.png" alt="User Icon">
        <div v-if="is_open"> Profile </div>
    </RouterLink>
</template>

<script setup lang="ts">
import { defineProps,  } from 'vue';
import store from "../../../store";

const props = defineProps({
    is_open: Boolean,
    is_mobile_view: Boolean,
})
let userId = store.getters.userData.token;
</script>


<style scoped>
.user-icon-container{
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    gap : 1em;
    padding: 1em;
    color: whitesmoke;
}
.user-icon-container:hover{
    background-color: var(--light-blue);
    border-radius: 10px;
}

.user-icon{
    width: 60px;
}

</style>
