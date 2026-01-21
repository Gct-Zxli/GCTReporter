<template>
  <div class="page-container">
    <AppHeader />
    <main class="page-main">
      <div class="header-section">
        <h1>报表管理</h1>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建报表
        </el-button>
      </div>

      <!-- 报表列表 -->
      <el-table 
        :data="reports" 
        style="width: 100%"
        v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="报表名称" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 创建/编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑报表' : '新建报表'"
        width="900px"
        :close-on-click-modal="false">
        <el-steps :active="currentStep" finish-status="success" align-center>
          <el-step title="基本信息" />
          <el-step title="SQL编辑" />
          <el-step title="参数配置" />
          <el-step title="列配置" />
        </el-steps>

        <div class="step-content">
          <!-- Step 1: 基本信息 -->
          <div v-show="currentStep === 0">
            <el-form :model="formData" label-width="100px">
              <el-form-item label="报表名称" required>
                <el-input v-model="formData.name" placeholder="请输入报表名称" />
              </el-form-item>
              <el-form-item label="报表描述">
                <el-input 
                  v-model="formData.description" 
                  type="textarea" 
                  :rows="3"
                  placeholder="请输入报表描述" />
              </el-form-item>
            </el-form>
          </div>

          <!-- Step 2: SQL编辑 -->
          <div v-show="currentStep === 1">
            <SqlEditor v-model="formData.sqlContent" />
          </div>

          <!-- Step 3: 参数配置 -->
          <div v-show="currentStep === 2">
            <el-button @click="addParam" type="primary" size="small" style="margin-bottom: 16px">
              <el-icon><Plus /></el-icon>
              添加参数
            </el-button>
            <el-table :data="formData.params" border>
              <el-table-column label="参数名称" width="200">
                <template #default="scope">
                  <el-input v-model="scope.row.paramName" placeholder="参数名称" />
                </template>
              </el-table-column>
              <el-table-column label="参数类型" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.paramType" placeholder="选择类型">
                    <el-option label="字符串" value="STRING" />
                    <el-option label="数字" value="NUMBER" />
                    <el-option label="日期" value="DATE" />
                    <el-option label="布尔" value="BOOLEAN" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="是否必填" width="120">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.required" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button 
                    link 
                    type="danger" 
                    @click="removeParam(scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- Step 4: 列配置 -->
          <div v-show="currentStep === 3">
            <el-button @click="addColumn" type="primary" size="small" style="margin-bottom: 16px">
              <el-icon><Plus /></el-icon>
              添加列
            </el-button>
            <el-table :data="formData.columns" border>
              <el-table-column label="字段名" width="200">
                <template #default="scope">
                  <el-input v-model="scope.row.fieldName" placeholder="字段名" />
                </template>
              </el-table-column>
              <el-table-column label="显示名称" width="200">
                <template #default="scope">
                  <el-input v-model="scope.row.displayName" placeholder="显示名称" />
                </template>
              </el-table-column>
              <el-table-column label="格式类型" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.formatType" placeholder="选择格式">
                    <el-option label="文本" value="TEXT" />
                    <el-option label="数字" value="NUMBER" />
                    <el-option label="日期" value="DATE" />
                    <el-option label="货币" value="CURRENCY" />
                    <el-option label="百分比" value="PERCENTAGE" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button 
                    link 
                    type="danger" 
                    @click="removeColumn(scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <template #footer>
          <div class="dialog-footer">
            <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button v-if="currentStep < 3" type="primary" @click="nextStep">下一步</el-button>
            <el-button v-else type="primary" @click="handleSubmit" :loading="submitting">
              保存
            </el-button>
          </div>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import SqlEditor from '@/components/SqlEditor.vue'
import axios from 'axios'

interface ReportParam {
  paramName: string
  paramType: string
  required: boolean
}

interface ReportColumn {
  fieldName: string
  displayName: string
  formatType: string
}

interface ReportFormData {
  name: string
  description: string
  sqlContent: string
  params: ReportParam[]
  columns: ReportColumn[]
}

interface Report {
  id: number
  name: string
  description: string
  creatorId: number
  createdAt: string
  updatedAt: string
}

const reports = ref<Report[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentEditId = ref<number | null>(null)
const currentStep = ref(0)
const submitting = ref(false)

const formData = ref<ReportFormData>({
  name: '',
  description: '',
  sqlContent: '',
  params: [],
  columns: []
})

// 加载报表列表
const loadReports = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/v1/reports')
    reports.value = response.data
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载报表列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 新建报表
const handleCreate = () => {
  isEdit.value = false
  currentEditId.value = null
  currentStep.value = 0
  formData.value = {
    name: '',
    description: '',
    sqlContent: '',
    params: [],
    columns: []
  }
  dialogVisible.value = true
}

// 编辑报表
const handleEdit = async (row: Report) => {
  isEdit.value = true
  currentEditId.value = row.id
  currentStep.value = 0
  
  try {
    const response = await axios.get(`/api/v1/reports/${row.id}/full`)
    formData.value = {
      name: response.data.name,
      description: response.data.description || '',
      sqlContent: response.data.sqlContent,
      params: response.data.params || [],
      columns: response.data.columns || []
    }
    dialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载报表配置失败')
  }
}

// 删除报表
const handleDelete = async (row: Report) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除报表"${row.name}"吗？删除后将无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    await axios.delete(`/api/v1/reports/${row.id}`)
    ElMessage.success('删除成功')
    await loadReports()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 步骤导航
const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 添加参数
const addParam = () => {
  formData.value.params.push({
    paramName: '',
    paramType: 'STRING',
    required: false
  })
}

// 移除参数
const removeParam = (index: number) => {
  formData.value.params.splice(index, 1)
}

// 添加列
const addColumn = () => {
  formData.value.columns.push({
    fieldName: '',
    displayName: '',
    formatType: 'TEXT'
  })
}

// 移除列
const removeColumn = (index: number) => {
  formData.value.columns.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  // 验证
  if (!formData.value.name) {
    ElMessage.warning('请输入报表名称')
    currentStep.value = 0
    return
  }
  if (!formData.value.sqlContent) {
    ElMessage.warning('请输入SQL内容')
    currentStep.value = 1
    return
  }

  submitting.value = true
  try {
    if (isEdit.value && currentEditId.value) {
      // 更新报表
      await axios.put(`/api/v1/reports/${currentEditId.value}`, formData.value)
      ElMessage.success('更新成功')
    } else {
      // 创建报表
      await axios.post('/api/v1/reports', formData.value)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    await loadReports()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadReports()
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

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.step-content {
  margin: 30px 0;
  min-height: 400px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-step__title) {
  font-size: 14px;
}

/* 平板适配 */
@media (max-width: 768px) {
  .page-main {
    padding: 24px 16px;
  }
  
  h1 {
    font-size: 28px;
  }
  
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
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
}
</style>
