<template>
  <div class="sql-editor-test">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>SQL编辑器测试页面</span>
        </div>
      </template>

      <div class="test-container">
        <SqlEditor
          v-model="sqlContent"
          height="500px"
          draft-key="test-sql-draft"
          @execute="handleExecute"
          @save="handleSave"
        />

        <el-divider />

        <div class="result-section">
          <h3>当前SQL内容：</h3>
          <el-input
            v-model="sqlContent"
            type="textarea"
            :rows="10"
            placeholder="SQL内容会同步显示在这里"
            readonly
          />
        </div>

        <div v-if="lastExecuted" class="execution-info">
          <h3>最后执行的SQL：</h3>
          <pre>{{ lastExecuted }}</pre>
        </div>

        <div v-if="lastSaved" class="save-info">
          <h3>最后保存时间：</h3>
          <p>{{ lastSaved }}</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SqlEditor from '@/components/SqlEditor.vue'
import { ElMessage } from 'element-plus'

const sqlContent = ref(`SELECT 
  u.id,
  u.username,
  u.role,
  u.enabled,
  u.created_at
FROM users u
WHERE u.enabled = 1
ORDER BY u.created_at DESC`)

const lastExecuted = ref('')
const lastSaved = ref('')

const handleExecute = (sql: string) => {
  lastExecuted.value = sql
  ElMessage.success('SQL已执行（模拟）')
  console.log('Execute SQL:', sql)
}

const handleSave = (sql: string) => {
  lastSaved.value = new Date().toLocaleString('zh-CN')
  console.log('Save SQL:', sql)
}
</script>

<style scoped>
.sql-editor-test {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-container {
  padding: 20px 0;
}

.result-section,
.execution-info,
.save-info {
  margin-top: 20px;
}

.result-section h3,
.execution-info h3,
.save-info h3 {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.execution-info pre {
  background-color: var(--el-fill-color-light);
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
}

.save-info p {
  color: var(--el-color-success);
  font-weight: 500;
}
</style>
