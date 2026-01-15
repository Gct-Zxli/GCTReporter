<template>
  <header class="app-header">
    <div class="header-container">
      <!-- Logo 区域 -->
      <div class="header-logo">
        <div class="logo-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="8" y="8" width="32" height="32" rx="4" fill="url(#header-logo-gradient)" />
            <path d="M16 20h16M16 24h12M16 28h16" stroke="white" stroke-width="2" stroke-linecap="round" />
            <defs>
              <linearGradient id="header-logo-gradient" x1="8" y1="8" x2="40" y2="40">
                <stop offset="0%" stop-color="#6366f1" />
                <stop offset="100%" stop-color="#8b5cf6" />
              </linearGradient>
            </defs>
          </svg>
        </div>
        <span class="logo-text">GCT Reporter</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="header-nav">
        <router-link to="/dashboard" class="nav-item" active-class="active">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
          </svg>
          <span>首页</span>
        </router-link>

        <router-link to="/reports" class="nav-item" active-class="active">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
            <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd" />
          </svg>
          <span>报表管理</span>
        </router-link>

        <router-link v-if="isAdmin" to="/users" class="nav-item" active-class="active">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
          </svg>
          <span>用户管理</span>
        </router-link>
      </nav>

      <!-- 用户菜单 -->
      <div class="header-user">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-trigger">
            <div class="user-avatar">
              <svg viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
              </svg>
            </div>
            <div class="user-info">
              <span class="user-name">{{ username }}</span>
              <span class="user-role">{{ roleLabel }}</span>
            </div>
            <svg class="dropdown-icon" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </div>

          <template #dropdown>
            <el-dropdown-menu class="user-dropdown">
              <el-dropdown-item command="profile">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                </svg>
                <span>个人资料</span>
              </el-dropdown-item>

              <el-dropdown-item command="settings">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z" clip-rule="evenodd" />
                </svg>
                <span>系统设置</span>
              </el-dropdown-item>

              <el-dropdown-item divided command="logout" class="logout-item">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 102 0V4a1 1 0 00-1-1zm10.293 9.293a1 1 0 001.414 1.414l3-3a1 1 0 000-1.414l-3-3a1 1 0 10-1.414 1.414L14.586 9H7a1 1 0 100 2h7.586l-1.293 1.293z" clip-rule="evenodd" />
                </svg>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 登出确认对话框 -->
    <el-dialog
      v-model="logoutDialogVisible"
      title="确认退出"
      width="420px"
      :show-close="false"
      class="logout-dialog"
    >
      <div class="logout-content">
        <div class="logout-icon">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
        </div>
        <h3>确定要退出登录吗？</h3>
        <p>退出后需要重新登录才能访问系统</p>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="logoutDialogVisible = false" size="large">
            取消
          </el-button>
          <el-button
            type="danger"
            @click="confirmLogout"
            :loading="logoutLoading"
            size="large"
          >
            {{ logoutLoading ? '退出中...' : '确认退出' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()

// 用户信息
const username = ref(localStorage.getItem('username') || '未知用户')
const role = ref(localStorage.getItem('role') || 'VIEWER')

// 计算属性
const isAdmin = computed(() => role.value === 'ADMIN')

const roleLabel = computed(() => {
  const roleMap: Record<string, string> = {
    ADMIN: '管理员',
    DESIGNER: '设计者',
    VIEWER: '查看者'
  }
  return roleMap[role.value] || '未知角色'
})

// 登出相关状态
const logoutDialogVisible = ref(false)
const logoutLoading = ref(false)

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人资料功能即将上线')
      break
    case 'settings':
      ElMessage.info('系统设置功能即将上线')
      break
    case 'logout':
      logoutDialogVisible.value = true
      break
  }
}

// 确认登出
const confirmLogout = async () => {
  try {
    logoutLoading.value = true

    // 调用登出API
    await authApi.logout()

    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('role')

    // 显示成功提示
    ElMessage.success('已成功退出登录')

    // 关闭对话框
    logoutDialogVisible.value = false

    // 延迟跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 800)

  } catch (error: any) {
    console.error('登出失败:', error)
    ElMessage.error('退出登录失败，请稍后重试')
  } finally {
    logoutLoading.value = false
  }
}
</script>

<style scoped>
/* Header 样式 */
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s;
}

.app-header:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}

/* Logo 区域 */
.header-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.header-logo:hover {
  transform: translateY(-1px);
}

.logo-icon {
  width: 40px;
  height: 40px;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.header-logo:hover .logo-icon {
  transform: rotate(5deg) scale(1.05);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.3px;
}

/* 导航菜单 */
.header-nav {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 10px;
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s;
  position: relative;
}

.nav-item svg {
  width: 18px;
  height: 18px;
  transition: transform 0.2s;
}

.nav-item:hover {
  color: #667eea;
  background: #f1f5f9;
}

.nav-item:hover svg {
  transform: translateY(-1px);
}

.nav-item.active {
  color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(139, 92, 246, 0.1));
}

.nav-item.active::before {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 16px;
  right: 16px;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px 2px 0 0;
}

/* 用户菜单 */
.header-user {
  flex-shrink: 0;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px 8px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.user-trigger:hover {
  background: #f8fafc;
  border-color: #e2e8f0;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  transition: transform 0.2s;
}

.user-trigger:hover .user-avatar {
  transform: scale(1.05);
}

.user-avatar svg {
  width: 22px;
  height: 22px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1;
}

.user-role {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1;
}

.dropdown-icon {
  width: 18px;
  height: 18px;
  color: #94a3b8;
  transition: transform 0.2s;
}

.user-trigger:hover .dropdown-icon {
  color: #667eea;
}

/* 下拉菜单样式 */
:deep(.user-dropdown) {
  margin-top: 8px;
  padding: 8px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  transition: all 0.2s;
}

:deep(.el-dropdown-menu__item svg) {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #f1f5f9;
  color: #667eea;
}

:deep(.el-dropdown-menu__item.logout-item) {
  color: #dc2626;
}

:deep(.el-dropdown-menu__item.logout-item:hover) {
  background: #fef2f2;
  color: #dc2626;
}

/* 登出对话框 */
:deep(.logout-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.logout-dialog .el-dialog__header) {
  padding: 24px 24px 0;
  margin: 0;
}

:deep(.logout-dialog .el-dialog__title) {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

:deep(.logout-dialog .el-dialog__body) {
  padding: 24px;
}

.logout-content {
  text-align: center;
}

.logout-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #fef2f2, #fee2e2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #dc2626;
  animation: icon-bounce 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes icon-bounce {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.logout-icon svg {
  width: 32px;
  height: 32px;
}

.logout-content h3 {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.logout-content p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

:deep(.dialog-footer .el-button) {
  min-width: 100px;
  border-radius: 10px;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }

  .logo-text {
    display: none;
  }

  .header-nav {
    gap: 4px;
  }

  .nav-item span {
    display: none;
  }

  .nav-item {
    padding: 10px;
  }

  .user-info {
    display: none;
  }
}
</style>
