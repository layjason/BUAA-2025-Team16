<script setup>

</script>

<template>
  <div style="background-color: #ffdaf3; min-height: 100vh; min-width: 100vh">
  <router-view  :key="$route.fullPath"/>
  </div>
</template>

<style scoped>

</style>
