<template>
    <div class="body-data">
        <div class="data-first-col">
            <div class="data-item">
                <strong>Weight:</strong> {{ bodyData.weight }} kg
            </div>
            <div class="data-item">
                <strong>Height:</strong> {{ bodyData.height }} cm
            </div>
            <div class="expandable-section" v-if="bodyData.skinFolds" @click="toggleSection('skinFolds')">
                <h3>
                    Skin Folds
                    <font-awesome-icon :icon="faCaretDown" class="collapsed" />
                </h3>
                <div v-if="sections.skinFolds">
                    <div class="data-item" v-for="(value, key) in bodyData.skinFolds" :key="key">
                        <strong>{{ key }}:</strong> {{ value }} cm
                    </div>
                </div>
            </div>
        </div>
        <div class="data-first-col">
            <div class="expandable-section" @click="toggleSection('circumferences')">
                <h3>
                    Body Circumferences
                    <font-awesome-icon :icon="faCaretDown" class="collapsed" />
                </h3>
                <div v-if="sections.circumferences">
                    <div class="data-item"><strong>Neck:</strong> {{ bodyData.bodyCircumferences.neck }} cm</div>
                    <div class="data-item"><strong>Chest:</strong> {{ bodyData.bodyCircumferences.chest }} cm</div>
                    <div class="data-item"><strong>Waist:</strong> {{ bodyData.bodyCircumferences.waist }} cm</div>
                    <div class="data-item"><strong>Hips:</strong> {{ bodyData.bodyCircumferences.hips }} cm</div>
                    <div class="data-item"><strong>Thighs:</strong> {{ bodyData.bodyCircumferences.thighs }} cm</div>
                    <div class="data-item"><strong>Calves:</strong> {{ bodyData.bodyCircumferences.calves }} cm</div>
                    <div class="data-item"><strong>Relaxed Right Arm:</strong> {{ bodyData.bodyCircumferences.relaxedRightArm }} cm</div>
                    <div class="data-item"><strong>Relaxed Left Arm:</strong> {{ bodyData.bodyCircumferences.relaxedLeftArm }} cm</div>
                    <div class="data-item"><strong>Flexed Right Arm:</strong> {{ bodyData.bodyCircumferences.flexedRightArm }} cm</div>
                    <div class="data-item"><strong>Flexed Left Arm:</strong> {{ bodyData.bodyCircumferences.flexedLeftArm }} cm</div>
                    <div class="data-item"><strong>Forearm:</strong> {{ bodyData.bodyCircumferences.forearm }} cm</div>
                </div>
            </div>
            <div class="expandable-section" @click="toggleSection('composition')">
                <h3>
                    Body Composition
                    <font-awesome-icon :icon="faCaretDown" class="collapsed" />
                </h3>
                <div v-if="sections.composition">
                    <div class="data-item"><strong>BMI:</strong> {{ bodyData.bodyComposition.bmi }}</div>
                    <div class="data-item"><strong>Body Fat Percentage:</strong> {{ bodyData.bodyComposition.bodyFatPercentage !== null ? bodyData.bodyComposition.bodyFatPercentage + '%' : 'N/A' }}</div>
                    <div class="data-item"><strong>Body Fat Mass:</strong> {{ bodyData.bodyComposition.bodyFatMass !== null ? bodyData.bodyComposition.bodyFatMass + ' kg' : 'N/A' }}</div>
                    <div class="data-item"><strong>Fat Free Mass:</strong> {{ bodyData.bodyComposition.fatFreeMass !== null ? bodyData.bodyComposition.fatFreeMass + ' kg' : 'N/A' }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import BodyData from "@/views/user/TrainerViews/models/traineeData/BodyData.ts";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {faCaretDown} from "@fortawesome/free-solid-svg-icons";

const props = defineProps<{
    bodyData: BodyData
}>();

const sections = ref({
    circumferences: false,
    composition: false,
    skinFolds: false
});

const toggleSection = (section: string) => {
    sections.value[section] = !sections.value[section];
};
</script>

<style scoped>
.body-data {
    font-family: Arial, sans-serif;
    padding: 20px;
    background-color: var(--main-primary-color);
    border-radius: 10px;
    margin: auto;
    display: flex;
    flex-direction: row;
    gap: 5em;
}
.body-data h2 {
    text-align: center;
    margin-bottom: 20px;
}
.body-data .data-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
}
.expandable-section {
    width: 100%;
    padding: 0 1em 0 1em;
    background-color: var(--main-secondary-color);
    border-radius: 10px;
}
.expandable-section h3 {
    cursor: pointer;
    color: whitesmoke;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.expandable-section h3:after {
    transition: transform 0.3s ease;
}
.collapsed{
    margin-left: 10px;
}
.expandable-section h3.collapsed:after {
    transform: rotate(-90deg);
}
@media (max-width: 768px) {
    .body-data {
        padding: 10px;
    }
    .body-data .data-item {
        flex-direction: column;
        align-items: flex-start;
    }
}
.data-first-col{
    display: flex;
    flex-direction: column;
    gap: 1em;
}
</style>


