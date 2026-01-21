<template>
  <div class="sql-editor">
    <div class="editor-toolbar">
      <div class="toolbar-left">
        <el-tag type="info" size="small">SQL编辑器</el-tag>
        <el-text size="small" type="info" style="margin-left: 12px;">
          快捷键: Ctrl+S 保存草稿 | Ctrl+Enter 执行SQL
        </el-text>
      </div>
      <div class="toolbar-right">
        <el-button 
          :icon="Document" 
          size="small" 
          @click="saveDraft"
          :loading="saving"
        >
          保存草稿
        </el-button>
        <el-button 
          :icon="Refresh" 
          size="small" 
          @click="clearDraft"
        >
          清空
        </el-button>
      </div>
    </div>
    <Codemirror
      v-model="code"
      :style="{ height: editorHeight }"
      :autofocus="true"
      :indent-with-tab="true"
      :tab-size="2"
      :extensions="extensions"
      @ready="handleReady"
      @change="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, onMounted, onUnmounted } from 'vue'
import { Codemirror } from 'vue-codemirror'
import { sql } from '@codemirror/lang-sql'
import { oneDark } from '@codemirror/theme-one-dark'
import { keymap } from '@codemirror/view'
import { ElMessage } from 'element-plus'
import { Document, Refresh } from '@element-plus/icons-vue'

// Props
interface Props {
  modelValue?: string
  height?: string
  draftKey?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  height: '400px',
  draftKey: 'sql-editor-draft'
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: string]
  'execute': [sql: string]
  'save': [sql: string]
}>()

// State
const code = ref(props.modelValue)
const editorHeight = ref(props.height)
const view = shallowRef()
const saving = ref(false)
const autoSaveTimer = ref<NodeJS.Timeout | null>(null)

// CodeMirror扩展配置
const extensions = [
  sql(),
  oneDark,
  keymap.of([
    {
      key: 'Ctrl-s',
      run: () => {
        saveDraft()
        return true
      }
    },
    {
      key: 'Ctrl-Enter',
      run: () => {
        executeSQL()
        return true
      }
    }
  ])
]

// 编辑器就绪回调
const handleReady = (payload: any) => {
  view.value = payload.view
}

// 编辑器内容变化
const handleChange = (value: string) => {
  emit('update:modelValue', value)
  
  // 重置自动保存定时器
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value)
  }
  
  // 30秒后自动保存
  autoSaveTimer.value = setTimeout(() => {
    autoSaveDraft()
  }, 30000)
}

// 手动保存草稿
const saveDraft = () => {
  saving.value = true
  
  try {
    localStorage.setItem(props.draftKey, code.value)
    ElMessage.success('草稿保存成功')
    emit('save', code.value)
  } catch (error) {
    ElMessage.error('保存草稿失败')
    console.error('Save draft error:', error)
  } finally {
    setTimeout(() => {
      saving.value = false
    }, 300)
  }
}

// 自动保存草稿（静默）
const autoSaveDraft = () => {
  try {
    localStorage.setItem(props.draftKey, code.value)
    console.log('Auto-saved draft')
  } catch (error) {
    console.error('Auto-save draft error:', error)
  }
}

// 清空编辑器
const clearDraft = () => {
  code.value = ''
  localStorage.removeItem(props.draftKey)
  ElMessage.info('已清空编辑器')
}

// 执行SQL
const executeSQL = () => {
  if (!code.value.trim()) {
    ElMessage.warning('请输入SQL语句')
    return
  }
  emit('execute', code.value)
}

// 加载草稿
const loadDraft = () => {
  try {
    const draft = localStorage.getItem(props.draftKey)
    if (draft && !props.modelValue) {
      code.value = draft
      ElMessage.success('已加载上次的草稿')
    }
  } catch (error) {
    console.error('Load draft error:', error)
  }
}

// 生命周期
onMounted(() => {
  loadDraft()
})

onUnmounted(() => {
  if (autoSaveTimer.value) {
    clearTimeout(autoSaveTimer.value)
  }
})

// 暴露方法给父组件
defineExpose({
  saveDraft,
  clearDraft,
  executeSQL
})
</script>

<style scoped>
.sql-editor {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: var(--el-fill-color-light);
  border-bottom: 1px solid var(--el-border-color);
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

:deep(.cm-editor) {
  font-size: 14px;
  line-height: 1.6;
}

:deep(.cm-scroller) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

:deep(.cm-gutters) {
  background-color: #282c34;
  border-right: 1px solid #4b5563;
}

:deep(.cm-lineNumbers) {
  color: #6b7280;
}
</style>
