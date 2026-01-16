<template>
  <div class="dashboard-container">
    <AppHeader />
    <main class="dashboard-main">
      <div class="welcome-section">
        <h1>欢迎使用 GCT Reporter</h1>
        <p>程序员报表生成工具 - Sprint 1 开发中</p>
        <div class="stats">
          <div class="stat-card">
            <div class="stat-icon">📊</div>
            <div class="stat-value">0</div>
            <div class="stat-label">报表总数</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-value">3</div>
            <div class="stat-label">系统用户</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🚀</div>
            <div class="stat-value">Sprint 1</div>
            <div class="stat-label">当前版本</div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue'
</script>

<style scoped>
.dashboard-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  background: #f8fafc;
}

.dashboard-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 24px;
}

.welcome-section {
  text-align: center;
  padding: 60px 20px;
}

.welcome-section h1 {
  font-size: 42px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0 0 16px;
}

.welcome-section p {
  font-size: 18px;
  color: #64748b;
  margin: 0 0 48px;
}

/* 平板适配 */
@media (max-width: 768px) {
  .welcome-section h1 {
    font-size: 32px;
  }
  
  .welcome-section p {
    font-size: 16px;
    margin: 0 0 32px;
  }
  
  .dashboard-main {
    padding: 24px 16px;
  }
}

/* 手机适配 */
@media (max-width: 480px) {
  .welcome-section {
    padding: 40px 16px;
  }
  
  .welcome-section h1 {
    font-size: 28px;
  }
  
  .welcome-section p {
    font-size: 15px;
    margin: 0 0 24px;
  }
  
  .dashboard-main {
    padding: 20px 12px;
  }
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
  max-width: 800px;
  margin: 0 auto;
}

/* 手机适配 - 统计卡片 */
@media (max-width: 640px) {
  .stats {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stat-card {
    padding: 24px;
  }
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.15);
}

.stat-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}
</style>
