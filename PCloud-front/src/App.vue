<script setup lang="ts">
import BasicLayout from '@/layout/BasicLayout.vue'
</script>

<template>
  <div id="app">
    <BasicLayout>
      <router-view />
    </BasicLayout>
  </div>
</template>

<style>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

#app {
  min-height: 100vh;
  width: 100%;
}
</style>
