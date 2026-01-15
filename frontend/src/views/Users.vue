<template>
  <div class="page-container">
    <AppHeader />
    <main class="page-main">
      <h1>用户管理</h1>
      <p>功能开发中...</p>
    </main>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue'
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f8fafc;
}

.page-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 24px;
}

h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px;
}

p {
  font-size: 16px;
  color: #64748b;
}
</style>
