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
  position: relative;
  width: 100%;
  height: 100%;
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

/* 平板适配 */
@media (max-width: 768px) {
  .page-main {
    padding: 24px 16px;
  }
  
  h1 {
    font-size: 28px;
  }
}

/* 手机适配 */
@media (max-width: 480px) {
  .page-main {
    padding: 20px 12px;
  }
  
  h1 {
    font-size: 24px;
  }
  
  p {
    font-size: 15px;
  }
}
</style>
