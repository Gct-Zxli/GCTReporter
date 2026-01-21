import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import SqlEditor from '@/components/SqlEditor.vue'
import { nextTick } from 'vue'
import ElementPlus from 'element-plus'

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString()
    },
    removeItem: (key: string) => {
      delete store[key]
    },
    clear: () => {
      store = {}
    }
  }
})()

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock
})

// Global setup for all tests
const createWrapper = (props = {}) => {
  return mount(SqlEditor, {
    props,
    global: {
      plugins: [ElementPlus]
    }
  })
}

describe('SqlEditor', () => {
  beforeEach(() => {
    localStorageMock.clear()
  })

  it('应该正确渲染编辑器', () => {
    const wrapper = createWrapper({
      modelValue: 'SELECT * FROM users'
    })

    expect(wrapper.find('.sql-editor').exists()).toBe(true)
    expect(wrapper.find('.editor-toolbar').exists()).toBe(true)
  })

  it('应该显示快捷键提示', () => {
    const wrapper = createWrapper()
    
    expect(wrapper.text()).toContain('Ctrl+S 保存草稿')
    expect(wrapper.text()).toContain('Ctrl+Enter 执行SQL')
  })

  it('应该能保存草稿到localStorage', async () => {
    const wrapper = createWrapper({
      draftKey: 'test-draft'
    })

    wrapper.vm.code = 'SELECT * FROM users WHERE id = 1'
    await wrapper.vm.saveDraft()

    expect(localStorageMock.getItem('test-draft')).toBe('SELECT * FROM users WHERE id = 1')
  })

  it('应该在挂载时加载草稿', async () => {
    localStorageMock.setItem('test-draft-2', 'SELECT * FROM reports')

    const wrapper = createWrapper({
      draftKey: 'test-draft-2'
    })

    await nextTick()
    
    expect(wrapper.vm.code).toBe('SELECT * FROM reports')
  })

  it('应该能清空编辑器和草稿', async () => {
    localStorageMock.setItem('test-draft-3', 'SELECT * FROM users')

    const wrapper = createWrapper({
      draftKey: 'test-draft-3',
      modelValue: 'SELECT * FROM users'
    })

    await wrapper.vm.clearDraft()

    expect(wrapper.vm.code).toBe('')
    expect(localStorageMock.getItem('test-draft-3')).toBeNull()
  })

  it('应该触发execute事件', async () => {
    const wrapper = createWrapper()
    
    wrapper.vm.code = 'SELECT * FROM users'
    await wrapper.vm.executeSQL()

    expect(wrapper.emitted('execute')).toBeTruthy()
    expect(wrapper.emitted('execute')?.[0]).toEqual(['SELECT * FROM users'])
  })

  it('应该触发save事件', async () => {
    const wrapper = createWrapper({
      draftKey: 'test-save'
    })
    
    wrapper.vm.code = 'SELECT * FROM reports'
    await wrapper.vm.saveDraft()

    expect(wrapper.emitted('save')).toBeTruthy()
    expect(wrapper.emitted('save')?.[0]).toEqual(['SELECT * FROM reports'])
  })

  it('空SQL不应该执行', async () => {
    const wrapper = createWrapper()
    
    wrapper.vm.code = ''
    await wrapper.vm.executeSQL()

    // 空SQL不应该触发execute事件
    expect(wrapper.emitted('execute')).toBeFalsy()
  })

  it('应该使用自定义高度', () => {
    const wrapper = createWrapper({
      height: '600px'
    })

    expect(wrapper.vm.editorHeight).toBe('600px')
  })
})
