<template>
    <div class="bio-container">
        <div class="bio-card">
            <h2>Bio</h2>
            <div class="bio-text-container">
                <input v-model="newBio" :class="[is_editable ? 'bio-text-edit' : 'bio-text']"
                       :disabled="!is_editable"
                       placeholder="Enter your Bio"
                />
                <font-awesome-icon :icon="faPenToSquare" @click="updateBio"/>
            </div>
        </div>
    </div>
</template>


<script setup lang="ts">

import {computed, ref} from "vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faPenToSquare} from "@fortawesome/free-solid-svg-icons";
import store from "../../../../store";

const bio = computed(() => store.getters.userBio);
const newBio = ref(bio.value);
const is_editable = ref(false);

const updateBio = () => {
    if(is_editable.value){
        // Save the bio
        store.dispatch("userBio", newBio.value)
        is_editable.value = !is_editable.value;
    }else{
        is_editable.value = !is_editable.value;
    }
}
</script>



<style scoped>
.bio-card{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 10px;
    background-color: var(--primary-color);
    border-radius: 10px;
}

.bio-text-container{
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 10px;
}

.bio-text,.bio-text-edit {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    margin: 10px;
    border: none;
    background-color: var(--primary-color);
}
.bio-text-edit{
    color: whitesmoke;
    border: 1px solid whitesmoke;
    background-color: var(--secundary-color);
}

</style>
