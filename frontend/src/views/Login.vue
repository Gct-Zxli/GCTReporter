<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="grid-pattern"></div>
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- Logo 和标题区域 -->
      <div class="login-header">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="8" y="8" width="32" height="32" rx="4" fill="url(#logo-gradient)" />
              <path d="M16 20h16M16 24h12M16 28h16" stroke="white" stroke-width="2" stroke-linecap="round" />
              <defs>
                <linearGradient id="logo-gradient" x1="8" y1="8" x2="40" y2="40">
                  <stop offset="0%" stop-color="#6366f1" />
                  <stop offset="100%" stop-color="#8b5cf6" />
                </linearGradient>
              </defs>
            </svg>
          </div>
        </div>
        <h1 class="login-title">GCT Reporter</h1>
        <p class="login-subtitle">程序员报表生成工具</p>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <div class="input-wrapper">
            <div class="input-icon">
              <svg viewBox="0 0 20 20" fill="currentColor">
                <path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" />
              </svg>
            </div>
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              :prefix-icon="null"
              autocomplete="username"
              @keyup.enter="handleLogin"
            />
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <div class="input-wrapper">
            <div class="input-icon">
              <svg viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
              </svg>
            </div>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              :prefix-icon="null"
              autocomplete="current-password"
              show-password
              @keyup.enter="handleLogin"
            />
          </div>
        </el-form-item>

        <!-- 错误提示 -->
        <transition name="error-slide">
          <div v-if="errorMessage" class="error-message">
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
            <span>{{ errorMessage }}</span>
          </div>
        </transition>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            native-type="submit"
            class="login-button"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部提示 -->
      <div class="login-footer">
        <div class="test-accounts-hint">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
          </svg>
          <span>测试账号：admin/admin123 或 designer/designer123</span>
        </div>
      </div>
    </div>

    <!-- 版本信息 -->
    <div class="version-info">
      <span>v1.0.0 Sprint 1</span>
      <span class="separator">•</span>
      <span>© 2026 GCT Team</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ]
}

// 响应式状态
const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const errorMessage = ref('')

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 清除之前的错误信息
    errorMessage.value = ''

    // 表单验证
    await loginFormRef.value.validate()

    // 开始登录
    loading.value = true

    // 调用登录API
    const { data } = await authApi.login({
      username: loginForm.username,
      password: loginForm.password
    })

    // 保存 token 到 localStorage
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)

    // 显示成功提示
    ElMessage.success({
      message: `欢迎回来，${data.username}！`,
      duration: 2000
    })

    // 延迟跳转，让用户看到成功提示
    setTimeout(() => {
      router.push('/dashboard')
    }, 1000)

  } catch (error: any) {
    // 显示错误信息
    if (error.response) {
      const { status, data } = error.response
      
      if (status === 401) {
        errorMessage.value = '用户名或密码错误'
      } else if (status === 403) {
        errorMessage.value = '账号已被禁用，请联系管理员'
      } else {
        errorMessage.value = data?.message || '登录失败，请稍后重试'
      }
    } else {
      errorMessage.value = '网络错误，请检查网络连接'
    }

    // 3秒后自动清除错误信息
    setTimeout(() => {
      errorMessage.value = ''
    }, 3000)

  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 容器样式 */
.login-container {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  opacity: 0.6;
}

.grid-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: grid-move 20s linear infinite;
}

@keyframes grid-move {
  0% { transform: translate(0, 0); }
  100% { transform: translate(50px, 50px); }
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  animation: float 20s ease-in-out infinite;
}

.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.4), transparent);
  top: -250px;
  left: -150px;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.3), transparent);
  bottom: -200px;
  right: -100px;
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

.floating-shapes {
  position: absolute;
  inset: 0;
}

.shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  animation: drift 25s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  transform: rotate(45deg);
}

.shape-2 {
  width: 60px;
  height: 60px;
  top: 60%;
  right: 15%;
  transform: rotate(30deg);
  animation-delay: -8s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 15%;
  left: 20%;
  transform: rotate(15deg);
  animation-delay: -15s;
}

@keyframes drift {
  0%, 100% { transform: translate(0, 0) rotate(45deg); }
  33% { transform: translate(40px, -40px) rotate(135deg); }
  66% { transform: translate(-30px, 30px) rotate(225deg); }
}

/* 登录卡片 */
.login-card {
  position: relative;
  width: 440px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  animation: card-enter 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 1;
}

@keyframes card-enter {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Logo 和标题 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
  animation: fade-in 0.8s ease-out 0.2s both;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-wrapper {
  display: inline-block;
  margin-bottom: 20px;
  animation: logo-bounce 1s ease-out 0.4s both;
}

@keyframes logo-bounce {
  0% {
    opacity: 0;
    transform: scale(0.3) rotate(-10deg);
  }
  50% {
    transform: scale(1.1) rotate(5deg);
  }
  100% {
    opacity: 1;
    transform: scale(1) rotate(0deg);
  }
}

.logo-icon {
  width: 64px;
  height: 64px;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.logo-icon:hover {
  transform: rotate(5deg) scale(1.05);
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

/* 表单样式 */
.login-form {
  animation: fade-in 0.8s ease-out 0.4s both;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  color: #94a3b8;
  pointer-events: none;
  z-index: 1;
  transition: color 0.2s;
}

.input-wrapper:focus-within .input-icon {
  color: #667eea;
}

:deep(.el-input__wrapper) {
  padding-left: 48px !important;
  height: 52px;
  border-radius: 12px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #cbd5e1 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea inset, 0 4px 12px rgba(102, 126, 234, 0.15);
}

:deep(.el-input__inner) {
  font-size: 15px;
  color: #1e293b;
  font-weight: 500;
}

:deep(.el-input__inner::placeholder) {
  color: #94a3b8;
  font-weight: 400;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

/* 错误提示 */
.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 10px;
  color: #dc2626;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 20px;
}

.error-message svg {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.error-slide-enter-active,
.error-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.error-slide-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.error-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 
    0 4px 12px rgba(102, 126, 234, 0.3),
    0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 8px 20px rgba(102, 126, 234, 0.4),
    0 2px 6px rgba(0, 0, 0, 0.1);
}

.login-button:hover::before {
  opacity: 1;
}

.login-button:active {
  transform: translateY(0);
}

.login-button.is-loading {
  pointer-events: none;
}

/* 底部提示 */
.login-footer {
  margin-top: 32px;
  animation: fade-in 0.8s ease-out 0.6s both;
}

.test-accounts-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: linear-gradient(135deg, #f0f4ff, #e0e7ff);
  border-radius: 10px;
  font-size: 13px;
  color: #4c51bf;
  font-weight: 500;
}

.test-accounts-hint svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

/* 版本信息 */
.version-info {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  font-weight: 500;
  z-index: 1;
  animation: fade-in 1s ease-out 0.8s both;
}

.separator {
  opacity: 0.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    max-width: 400px;
    padding: 40px 32px;
  }

  .login-title {
    font-size: 24px;
  }

  .orb-1,
  .orb-2 {
    filter: blur(60px);
  }
}
</style>
