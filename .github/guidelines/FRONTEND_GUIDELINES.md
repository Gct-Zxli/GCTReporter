# 前端开发规范（Vue/TypeScript）

> 适用于GCT Reporter项目的Vue 3前端开发

---

## 📋 目录

- [技术栈](#技术栈)
- [环境搭建](#环境搭建)
- [项目结构](#项目结构)
- [编码规范](#编码规范)
- [组件规范](#组件规范)
- [状态管理](#状态管理)
- [API调用](#api调用)
- [路由规范](#路由规范)
- [样式规范](#样式规范)
- [性能优化](#性能优化)

---

## 技术栈

### 核心框架

```yaml
语言: TypeScript 5.x
框架: Vue 3.3.x (组合式API)
构建工具: Vite 4.x
UI组件库: Element Plus 2.3.x
代码编辑器: Monaco Editor
HTTP客户端: Axios 1.x
状态管理: Pinia 2.x
路由: Vue Router 4.x
```

### 主要依赖

```json
{
  "dependencies": {
    "vue": "^3.3.0",
    "vue-router": "^4.2.0",
    "pinia": "^2.1.0",
    "axios": "^1.6.0",
    "element-plus": "^2.4.0",
    "@element-plus/icons-vue": "^2.1.0",
    "monaco-editor": "^0.44.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^4.5.0",
    "typescript": "^5.2.0",
    "vite": "^4.5.0",
    "eslint": "^8.54.0",
    "prettier": "^3.1.0",
    "vitest": "^1.0.0",
    "@vue/test-utils": "^2.4.0"
  }
}
```

---

## 环境搭建

### 1. 安装Node.js

```bash
# 使用nvm安装（推荐）
nvm install 18
nvm use 18

# 或直接下载安装
# https://nodejs.org/

# 验证安装
node -v  # 应显示 v18.x.x
npm -v   # 应显示 9.x.x
```

### 2. 配置npm镜像（可选）

```bash
# 使用淘宝镜像加速
npm config set registry https://registry.npmmirror.com
```

### 3. 配置VS Code

**安装扩展**：
- Volar（Vue 3官方扩展）
- TypeScript Vue Plugin (Volar)
- ESLint
- Prettier - Code formatter
- Vue VSCode Snippets

**settings.json配置**：
```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "[vue]": {
    "editor.defaultFormatter": "Vue.volar"
  },
  "eslint.validate": ["javascript", "typescript", "vue"]
}
```

### 4. 初始化项目

```bash
# 克隆项目
git clone https://github.com/your-org/gct-reporter.git
cd gct-reporter/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问应用
# http://localhost:5173
```

---

## 项目结构

### 标准目录结构

```
frontend/
├── public/                      # 静态资源
│   └── favicon.ico
├── src/
│   ├── assets/                  # 资源文件
│   │   ├── images/
│   │   ├── styles/
│   │   │   ├── variables.scss   # SCSS变量
│   │   │   └── global.scss      # 全局样式
│   │   └── icons/
│   ├── components/              # 公共组件
│   │   ├── common/              # 通用组件
│   │   │   ├── AppHeader.vue
│   │   │   ├── AppSidebar.vue
│   │   │   └── Breadcrumb.vue
│   │   └── report/              # 报表相关组件
│   │       ├── ReportList.vue
│   │       ├── ReportForm.vue
│   │       └── SqlEditor.vue
│   ├── views/                   # 页面组件
│   │   ├── admin/               # 管理端页面
│   │   │   ├── UserManage.vue
│   │   │   └── RoleManage.vue
│   │   ├── designer/            # 设计端页面
│   │   │   ├── ReportDesign.vue
│   │   │   └── ReportPreview.vue
│   │   ├── viewer/              # 用户端页面
│   │   │   ├── ReportQuery.vue
│   │   │   └── ReportResult.vue
│   │   └── Login.vue
│   ├── router/                  # 路由配置
│   │   ├── index.ts
│   │   └── modules/
│   │       ├── admin.ts
│   │       ├── designer.ts
│   │       └── viewer.ts
│   ├── store/                   # Pinia状态管理
│   │   ├── index.ts
│   │   └── modules/
│   │       ├── user.ts
│   │       ├── report.ts
│   │       └── app.ts
│   ├── api/                     # API接口
│   │   ├── request.ts           # Axios封装
│   │   ├── user.ts
│   │   └── report.ts
│   ├── types/                   # TypeScript类型定义
│   │   ├── user.ts
│   │   ├── report.ts
│   │   └── api.ts
│   ├── utils/                   # 工具函数
│   │   ├── format.ts
│   │   ├── validate.ts
│   │   └── storage.ts
│   ├── directives/              # 自定义指令
│   │   └── permission.ts
│   ├── composables/             # 组合式函数
│   │   ├── useTable.ts
│   │   └── useReport.ts
│   ├── App.vue                  # 根组件
│   └── main.ts                  # 入口文件
├── tests/                       # 测试文件
│   ├── unit/
│   └── e2e/
├── .eslintrc.js                 # ESLint配置
├── .prettierrc.js               # Prettier配置
├── tsconfig.json                # TypeScript配置
├── vite.config.ts               # Vite配置
└── package.json
```

---

## 编码规范

### 1. 命名规范

**组件命名**：PascalCase
```typescript
// ✅ 正确
import ReportList from '@/components/report/ReportList.vue'
import UserManage from '@/views/admin/UserManage.vue'

// ❌ 错误
import reportList from '@/components/report/reportList.vue'
```

**文件命名**：
```
组件文件: PascalCase.vue
工具文件: camelCase.ts
类型文件: camelCase.ts
```

**变量/函数命名**：camelCase
```typescript
// ✅ 正确
const reportList = ref([])
const fetchReportList = async () => {}

// ❌ 错误
const ReportList = ref([])
const FetchReportList = async () => {}
```

**常量命名**：UPPER_SNAKE_CASE
```typescript
// ✅ 正确
const MAX_PAGE_SIZE = 100
const API_BASE_URL = '/api'

// ❌ 错误
const maxPageSize = 100
```

### 2. 使用组合式API

```vue
<template>
  <div class="report-list">
    <el-table :data="reportList" :loading="loading">
      <el-table-column prop="name" label="报表名称" />
      <el-table-column prop="description" label="描述" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Report } from '@/types/report'
import { getReportList } from '@/api/report'

// 响应式数据
const reportList = ref<Report[]>([])
const loading = ref(false)

// 加载报表列表
const loadReports = async () => {
  loading.value = true
  try {
    const { data } = await getReportList()
    reportList.value = data
  } catch (error) {
    console.error('加载报表失败:', error)
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadReports()
})
</script>

<style scoped lang="scss">
.report-list {
  padding: 20px;
}
</style>
```

### 3. TypeScript类型定义

**定义接口**：
```typescript
// types/report.ts

/** 报表实体 */
export interface Report {
  id: number
  name: string
  description?: string
  sqlContent: string
  createdAt: string
  updatedAt: string
}

/** 报表查询参数 */
export interface ReportQueryParams {
  page?: number
  size?: number
  keyword?: string
}

/** 报表DTO */
export interface ReportDTO {
  name: string
  description?: string
  sqlContent: string
}
```

**使用类型**：
```typescript
<script setup lang="ts">
import type { Report, ReportDTO } from '@/types/report'

const report = ref<Report | null>(null)

const createReport = async (dto: ReportDTO) => {
  // 实现
}
</script>
```

---

## 组件规范

### 1. 组件结构

```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup lang="ts">
// 1. 导入
import { ref, computed, onMounted } from 'vue'
import type { PropType } from 'vue'

// 2. Props定义
interface Props {
  reportId: number
  mode?: 'view' | 'edit'
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'view'
})

// 3. Emits定义
interface Emits {
  (e: 'update', id: number): void
  (e: 'delete', id: number): void
}

const emit = defineEmits<Emits>()

// 4. 响应式数据
const loading = ref(false)
const formData = ref({})

// 5. 计算属性
const isEditMode = computed(() => props.mode === 'edit')

// 6. 方法
const handleSubmit = () => {
  emit('update', props.reportId)
}

// 7. 生命周期
onMounted(() => {
  // 初始化
})
</script>

<style scoped lang="scss">
/* 样式 */
</style>
```

### 2. Props验证

```typescript
<script setup lang="ts">
import type { PropType } from 'vue'

interface Props {
  // 必填属性
  reportId: number
  
  // 可选属性
  title?: string
  
  // 带默认值
  size?: 'small' | 'medium' | 'large'
  
  // 复杂类型
  config?: {
    autoLoad: boolean
    pageSize: number
  }
}

const props = withDefaults(defineProps<Props>(), {
  title: '默认标题',
  size: 'medium',
  config: () => ({
    autoLoad: true,
    pageSize: 20
  })
})
</script>
```

### 3. 组件注释

```vue
<template>
  <!-- 组件模板 -->
</template>

<script setup lang="ts">
/**
 * 报表列表组件
 * 
 * @description 显示用户有权限访问的报表列表，支持搜索、分页
 * @author 张三
 * @since 1.0
 * 
 * @example
 * ```vue
 * <ReportList
 *   :mode="'view'"
 *   @select="handleReportSelect"
 * />
 * ```
 */

// 组件逻辑
</script>
```

---

## 状态管理

### 使用Pinia

```typescript
// store/modules/report.ts
import { defineStore } from 'pinia'
import type { Report } from '@/types/report'
import { getReportList, createReport } from '@/api/report'

export const useReportStore = defineStore('report', {
  // State
  state: () => ({
    reportList: [] as Report[],
    currentReport: null as Report | null,
    loading: false
  }),
  
  // Getters
  getters: {
    reportCount: (state) => state.reportList.length,
    
    getReportById: (state) => {
      return (id: number) => state.reportList.find(r => r.id === id)
    }
  },
  
  // Actions
  actions: {
    async fetchReportList() {
      this.loading = true
      try {
        const { data } = await getReportList()
        this.reportList = data
      } catch (error) {
        console.error('获取报表列表失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async addReport(dto: ReportDTO) {
      const { data } = await createReport(dto)
      this.reportList.push(data)
      return data
    },
    
    setCurrentReport(report: Report | null) {
      this.currentReport = report
    }
  }
})
```

**在组件中使用**：
```typescript
<script setup lang="ts">
import { useReportStore } from '@/store/modules/report'
import { storeToRefs } from 'pinia'

const reportStore = useReportStore()

// 响应式引用state
const { reportList, loading } = storeToRefs(reportStore)

// 调用action
const loadReports = () => {
  reportStore.fetchReportList()
}
</script>
```

---

## API调用

### 1. Axios封装

```typescript
// api/request.ts
import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    
    // 添加Token
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    
    if (code === 'SUCCESS') {
      return data
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      // 未授权，跳转登录
      ElMessage.error('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
```

### 2. API模块

```typescript
// api/report.ts
import request from './request'
import type { Report, ReportDTO, ReportQueryParams } from '@/types/report'

/**
 * 获取报表列表
 */
export const getReportList = (params?: ReportQueryParams) => {
  return request<Report[]>({
    url: '/api/reports',
    method: 'get',
    params
  })
}

/**
 * 获取报表详情
 */
export const getReportById = (id: number) => {
  return request<Report>({
    url: `/api/reports/${id}`,
    method: 'get'
  })
}

/**
 * 创建报表
 */
export const createReport = (data: ReportDTO) => {
  return request<Report>({
    url: '/api/reports',
    method: 'post',
    data
  })
}

/**
 * 执行报表查询
 */
export const executeReport = (id: number, params: Record<string, any>) => {
  return request<any[]>({
    url: `/api/reports/${id}/execute`,
    method: 'post',
    data: params
  })
}
```

---

## 路由规范

### 路由配置

```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: {
      title: '管理端',
      requiresAuth: true,
      roles: ['ADMIN']
    },
    children: [
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

---

## 样式规范

### 1. SCSS变量

```scss
// assets/styles/variables.scss

// 颜色
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;

// 字体
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto;
$font-size-base: 14px;

// 间距
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 16px;
$spacing-lg: 24px;
```

### 2. Scoped样式

```vue
<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.report-list {
  padding: $spacing-lg;
  
  &__header {
    margin-bottom: $spacing-md;
    display: flex;
    justify-content: space-between;
  }
  
  &__title {
    font-size: 18px;
    font-weight: bold;
    color: $primary-color;
  }
}
</style>
```

---

## 性能优化

### 1. 组件懒加载

```typescript
// 路由懒加载
const routes = [
  {
    path: '/report',
    component: () => import('@/views/ReportList.vue')
  }
]

// 组件懒加载
const ReportForm = defineAsyncComponent(() =>
  import('@/components/report/ReportForm.vue')
)
```

### 2. 虚拟滚动

```vue
<template>
  <el-table-v2
    :columns="columns"
    :data="largeDataList"
    :width="700"
    :height="400"
  />
</template>
```

### 3. 防抖节流

```typescript
import { debounce } from 'lodash-es'

const handleSearch = debounce((keyword: string) => {
  // 搜索逻辑
}, 300)
```

---

**最后更新**：2026-01-15  
**适用版本**：v1.0-MVP
