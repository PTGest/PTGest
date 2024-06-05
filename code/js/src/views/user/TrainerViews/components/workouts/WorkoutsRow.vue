<template>
        <div class="workouts-container">
           <table class="table">
                <thead>
                    <tr>
                        <th>
                            <font-awesome-icon :icon="faDumbbell"/>
                            Workout
                        </th>
                        <th>Description</th>
                        <th>Muscle Group</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Workout 1</td>
                        <td>Strength</td>

                    </tr>
                </tbody>
            </table>
        </div>
</template>

<script setup lang="ts">
import Workout from "@/views/user/TrainerViews/models/workouts/Workout.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faDumbbell} from "@fortawesome/free-solid-svg-icons";

const props = defineProps<{
  workouts: Workout[];
}>();
</script>



<style scoped>

.workouts-container{
    display: flex;
    flex-direction: column;
    justify-content: center;
    width: 30em;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

table{
    width: 100%;
    border-collapse: collapse;
    margin: 1rem;
}

th{
    padding: 1rem;
    border-bottom: 1px solid var(--main-secondary-color);
}

td{
    padding: 1rem;
    text-align: center;

}

tr{
    margin-top: 1rem;
    border-bottom: 1px solid var(--main-secondary-color);
}

</style>
