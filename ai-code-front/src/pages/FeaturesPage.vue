<script setup lang="ts">
import { healthCheck } from '@/api/healthController.ts'

healthCheck().then((res) => {
  console.log(res)
})

</script>

<template>
  <div class="features-view">
    <h1>功能页面</h1>
    <p>这里展示产品的功能介绍</p>
  </div>
</template>

<style scoped>
.features-view {
  padding: 20px;
}
</style>
