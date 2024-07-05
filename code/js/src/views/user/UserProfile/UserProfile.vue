<template>
    <Suspense>
        <template #default>
            <div class="profile-container">
                <ImageContainer class="image-container" :src="image" />
                <UserPersonalInfoContainer class="user-personal-info" :userId="props.userId"/>
            </div>
        </template>
        <template #fallback>
            <div>Loading...</div>
        </template>
    </Suspense>
</template>

<script setup lang="ts">


import ImageContainer from "../UserProfile/components/ImageContainer.vue"
import image from "../../../assets/./userIcons/man.png"
import UserPersonalInfoContainer from "../UserProfile/components/UserPersonalInfoContainer.vue"

const props = defineProps<{
    userId: string
}>()

</script>

<style scoped>
.profile-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.user-info {
    margin: 1em;
    justify-self: center;
    font-weight: bold;
    background-color: var(--main-primary-color);
    border-radius: 10px;
}

.bio-card {
    margin: 1em 1em 1em 0;
}

.image-container {
    justify-self: center;
}

.user-personal-info {
    background-color: var(--main-primary-color);
    border-radius: 10px;
    font-weight: bold;
    padding: 1em 4em 1em 4em;
}
</style>
