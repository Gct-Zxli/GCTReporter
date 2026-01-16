<template>
  <div class="page-container">
    <AppHeader />
    <main class="page-main">
      <!-- 页面标题和操作栏 -->
      <div class="page-header">
        <div class="header-left">
          <h1>用户管理</h1>
          <p class="page-desc">管理系统用户、角色和权限</p>
        </div>
        <div class="header-right">
          <el-button type="primary" size="large" @click="handleCreate" class="create-btn">
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clip-rule="evenodd" />
            </svg>
            <span>新建用户</span>
          </el-button>
        </div>
      </div>

      <!-- 用户列表 -->
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="users"
          stripe
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', color: '#475569', fontWeight: '600' }"
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="150">
            <template #default="{ row }">
              <div class="username-cell">
                <div class="user-avatar">
                  <svg viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                  </svg>
                </div>
                <span class="username-text">{{ row.username }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="role" label="角色" width="120">
            <template #default="{ row }">
              <el-tag :type="getRoleTagType(row.role)" size="small">
                {{ getRoleLabel(row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="enabled" label="状态" width="100">
            <template #default="{ row }">
              <el-switch
                v-model="row.enabled"
                :disabled="row.id === getCurrentUserId()"
                @change="handleToggleStatus(row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit(row)">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                </svg>
                编辑
              </el-button>
              <el-button
                link
                type="danger"
                size="small"
                :disabled="row.id === getCurrentUserId()"
                @click="handleDelete(row)"
              >
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
                </svg>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 创建用户对话框 -->
      <el-dialog
        v-model="createDialogVisible"
        title="新建用户"
        width="500px"
        :close-on-click-modal="false"
        class="user-dialog"
      >
        <el-form
          ref="createFormRef"
          :model="createForm"
          :rules="createRules"
          label-width="80px"
          label-position="left"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="createForm.username"
              placeholder="请输入用户名（3-20个字符）"
              maxlength="20"
              show-word-limit
              clearable
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="createForm.password"
              type="password"
              placeholder="请输入密码（至少6个字符）"
              maxlength="50"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="createForm.role" placeholder="请选择角色" style="width: 100%">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="设计者" value="DESIGNER" />
              <el-option label="查看者" value="VIEWER" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="enabled">
            <el-switch v-model="createForm.enabled" active-text="启用" inactive-text="禁用" />
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="createDialogVisible = false" size="large">取消</el-button>
            <el-button type="primary" @click="handleCreateSubmit" :loading="submitting" size="large">
              {{ submitting ? '创建中...' : '确认创建' }}
            </el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 编辑用户对话框 -->
      <el-dialog
        v-model="editDialogVisible"
        title="编辑用户"
        width="500px"
        :close-on-click-modal="false"
        class="user-dialog"
      >
        <el-form
          ref="editFormRef"
          :model="editForm"
          :rules="editRules"
          label-width="80px"
          label-position="left"
        >
          <el-form-item label="用户名">
            <el-input v-model="currentUser.username" disabled />
          </el-form-item>
          <el-form-item label="新密码" prop="password">
            <el-input
              v-model="editForm.password"
              type="password"
              placeholder="不修改请留空"
              maxlength="50"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="editForm.role" placeholder="请选择角色" style="width: 100%">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="设计者" value="DESIGNER" />
              <el-option label="查看者" value="VIEWER" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="enabled">
            <el-switch v-model="editForm.enabled" active-text="启用" inactive-text="禁用" />
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="editDialogVisible = false" size="large">取消</el-button>
            <el-button type="primary" @click="handleEditSubmit" :loading="submitting" size="large">
              {{ submitting ? '保存中...' : '确认保存' }}
            </el-button>
          </div>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import { userApi, type UserDTO, type CreateUserRequest, type UpdateUserRequest } from '@/api/user'

// 响应式数据
const loading = ref(false)
const users = ref<UserDTO[]>([])
const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const submitting = ref(false)

// 表单引用
const createFormRef = ref<FormInstance>()
const editFormRef = ref<FormInstance>()

// 创建用户表单
const createForm = ref<CreateUserRequest>({
  username: '',
  password: '',
  role: 'VIEWER',
  enabled: true
})

// 编辑用户表单
const editForm = ref<UpdateUserRequest>({
  password: '',
  role: undefined,
  enabled: undefined
})

const currentUser = ref<UserDTO>({
  id: 0,
  username: '',
  role: 'VIEWER',
  enabled: true,
  createdAt: '',
  updatedAt: ''
})

// 表单验证规则
const createRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度必须在3-20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '用户名只能包含字母、数字、下划线和横线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度必须在6-50个字符之间', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const editRules: FormRules = {
  password: [
    { min: 6, max: 50, message: '密码长度必须在6-50个字符之间', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 加载用户列表
const loadUsers = async () => {
  try {
    loading.value = true
    const { data } = await userApi.getAllUsers()
    users.value = data.data
  } catch (error: any) {
    console.error('加载用户列表失败:', error)
    ElMessage.error(error.response?.data?.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 打开创建对话框
const handleCreate = () => {
  createForm.value = {
    username: '',
    password: '',
    role: 'VIEWER',
    enabled: true
  }
  createFormRef.value?.clearValidate()
  createDialogVisible.value = true
}

// 提交创建
const handleCreateSubmit = async () => {
  if (!createFormRef.value) return

  try {
    await createFormRef.value.validate()
    submitting.value = true

    const { data } = await userApi.createUser(createForm.value)
    ElMessage.success(data.message || '用户创建成功')
    createDialogVisible.value = false
    loadUsers()
  } catch (error: any) {
    console.error('创建用户失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('创建用户失败')
    }
  } finally {
    submitting.value = false
  }
}

// 打开编辑对话框
const handleEdit = (row: UserDTO) => {
  currentUser.value = { ...row }
  editForm.value = {
    password: '',
    role: row.role,
    enabled: row.enabled
  }
  editFormRef.value?.clearValidate()
  editDialogVisible.value = true
}

// 提交编辑
const handleEditSubmit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    submitting.value = true

    const { data } = await userApi.updateUser(currentUser.value.id, editForm.value)
    ElMessage.success(data.message || '用户更新成功')
    editDialogVisible.value = false
    loadUsers()
  } catch (error: any) {
    console.error('更新用户失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('更新用户失败')
    }
  } finally {
    submitting.value = false
  }
}

// 切换用户状态
const handleToggleStatus = async (row: UserDTO) => {
  try {
    const { data } = await userApi.updateUser(row.id, { enabled: row.enabled })
    ElMessage.success(data.message || '状态更新成功')
    loadUsers()
  } catch (error: any) {
    console.error('更新状态失败:', error)
    row.enabled = !row.enabled // 回滚状态
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('更新状态失败')
    }
  }
}

// 删除用户
const handleDelete = async (row: UserDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${row.username}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    const { data } = await userApi.deleteUser(row.id)
    ElMessage.success(data.message || '用户删除成功')
    loadUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error('删除用户失败')
      }
    }
  }
}

// 获取角色标签类型
const getRoleTagType = (role: string) => {
  const typeMap: Record<string, any> = {
    ADMIN: 'danger',
    DESIGNER: 'warning',
    VIEWER: 'info'
  }
  return typeMap[role] || 'info'
}

// 获取角色标签文本
const getRoleLabel = (role: string) => {
  const labelMap: Record<string, string> = {
    ADMIN: '管理员',
    DESIGNER: '设计者',
    VIEWER: '查看者'
  }
  return labelMap[role] || role
}

// 格式化日期时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取当前用户ID
const getCurrentUserId = () => {
  return Number(localStorage.getItem('userId'))
}

// 组件挂载时加载数据
onMounted(() => {
  loadUsers()
})
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

/* 页面标题栏 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  gap: 20px;
}

.header-left h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px;
}

.page-desc {
  font-size: 15px;
  color: #64748b;
  margin: 0;
}

.header-right {
  flex-shrink: 0;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.create-btn svg {
  width: 18px;
  height: 18px;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

/* 表格容器 */
.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 用户名单元格 */
.username-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-avatar svg {
  width: 18px;
  height: 18px;
  color: white;
}

.username-text {
  font-weight: 500;
  color: #1e293b;
}

/* 操作按钮 */
:deep(.el-button) svg {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

/* 对话框样式 */
.user-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e5e7eb;
  padding: 20px 24px;
}

.user-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.user-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid #e5e7eb;
  padding: 16px 24px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 平板适配 */
@media (max-width: 768px) {
  .page-main {
    padding: 24px 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-left h1 {
    font-size: 28px;
  }
  
  .create-btn {
    width: 100%;
    justify-content: center;
  }
  
  .table-container {
    overflow-x: auto;
  }
}

/* 手机适配 */
@media (max-width: 480px) {
  .page-main {
    padding: 20px 12px;
  }
  
  .header-left h1 {
    font-size: 24px;
  }
  
  .page-desc {
    font-size: 14px;
  }
  
  .user-dialog {
    width: 90% !important;
  }
}
</style>
