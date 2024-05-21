<template>
    <div class="trainer">
        <div class="text">
            <h4>{{props.trainee.name}}</h4>
        </div>
        <div class="text">
            <p>Gender</p>
            <div class="gender-text">{{props.trainee.gender}}</div>
        </div>

        <router-link :to="{ name: 'assignTrainer', params: {traineeId: trainee.id, assignTrainer : 'reassignTrainer'},}">
            <button class="reassign-btn">Reassign Trainer</button>
        </router-link>



        <font-awesome-icon :icon="faX" class="delete-icon"></font-awesome-icon>
    </div>
</template>

<script setup lang="ts">

import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faX} from "@fortawesome/free-solid-svg-icons";
import Trainee from "../../../../views/user/CompaniesViews/models/Trainee.ts";
import DefaultButton from "@/components/utils/DefaultButton.vue";

const props = defineProps<{
    trainee: Trainee
}>()

</script>

<style scoped>
.trainer{
    width: 13em;
    height: 2em;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 1em 1em 1em 1em;
    border-radius: 10px;
    margin: 0.5em;
    background-color: var(--light-blue);
    color: whitesmoke;
    font-size: 1.5em;
    font-weight: bold;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
.icon{
    position: relative;
    top: 0.8em;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}
p, h4{
    padding: 0;
    margin: 0;
}

h4{
    text-shadow: var(--secundary-color) 2px 2px 2px;
}
p{
    font-size: 0.8em;
}
.text{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 0 0.5em 0 0.5em;
}


.delete-icon{
    position: relative;
    top: -1.5em;
    right: -0.5em;
    color: whitesmoke;
    font-size: 0.8em;
    margin: 0 1em 0 1em;
    cursor: pointer;
}
.gender-text{
    font-size: 0.6em;
}

.reassign-btn{
    font-size: 0.6em;
    border-radius: 10px;
    color: var(--primary-color);
    background-color: whitesmoke;
    cursor: pointer;
    margin: 0;

}

</style>

