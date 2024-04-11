<template>
  <button class="default-button" @click="props.clickHandler()" :disabled="props.isDisabled" >{{ props.displayText }}</button>
</template>

<script setup lang="ts">

const props = defineProps<{
  displayText: string;
  isDisabled: boolean;
  clickHandler: () => void;
}>();

</script>



<style scoped>
.default-button {
  margin-top: 1em;
  width: 20em;
  height: 3em;
  border-radius: 5px;
  background-color: whitesmoke;
  color: var(--sign-up-black);
  border: 0;
  cursor: pointer;
}

.default-button:disabled {
  background-color: #d3d3d3;
  color: #a9a9a9;
  cursor: not-allowed;
}
</style>