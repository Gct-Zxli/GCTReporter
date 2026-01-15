# 测试规范

> 适用于GCT Reporter项目的前后端测试

---

## 📋 目录

- [测试覆盖率要求](#测试覆盖率要求)
- [后端测试](#后端测试)
- [前端测试](#前端测试)
- [E2E测试](#e2e测试)
- [测试最佳实践](#测试最佳实践)

---

## 测试覆盖率要求

### 覆盖率目标

```yaml
后端测试:
  单元测试覆盖率: ≥80%
  API集成测试: 100%核心接口
  SQL安全测试: 100%SQL注入场景

前端测试:
  组件单元测试: ≥60%
  核心流程E2E测试: 100%（登录、创建报表、查询）
```

### 测试金字塔

```
       /\
      /E2E\      ← 少量E2E测试（核心业务流程）
     /------\
    /  集成  \   ← 适量集成测试（API、组件集成）
   /----------\
  /   单元测试  \ ← 大量单元测试（业务逻辑、工具函数）
 /--------------\
```

---

## 后端测试

### 测试框架

```xml
<!-- pom.xml -->
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 1. 单元测试

**Service层测试**：

```java
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    
    @Mock
    private ReportRepository reportRepository;
    
    @Mock
    private SqlValidator sqlValidator;
    
    @InjectMocks
    private ReportServiceImpl reportService;
    
    @Test
    @DisplayName("创建报表 - 成功场景")
    void testCreateReport_Success() {
        // Given
        ReportDTO dto = ReportDTO.builder()
            .name("用户统计报表")
            .sqlContent("SELECT * FROM users")
            .build();
        
        Report mockReport = Report.builder()
            .id(1L)
            .name(dto.getName())
            .sqlContent(dto.getSqlContent())
            .build();
        
        when(reportRepository.existsByName(dto.getName())).thenReturn(false);
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);
        
        // When
        Report result = reportService.createReport(dto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("用户统计报表");
        
        verify(sqlValidator).validate(dto.getSqlContent());
        verify(reportRepository).save(any(Report.class));
    }
    
    @Test
    @DisplayName("创建报表 - 名称重复异常")
    void testCreateReport_DuplicateName() {
        // Given
        ReportDTO dto = ReportDTO.builder()
            .name("重复报表")
            .build();
        
        when(reportRepository.existsByName(dto.getName())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> reportService.createReport(dto))
            .isInstanceOf(DuplicateReportNameException.class)
            .hasMessageContaining("报表名称已存在");
        
        verify(reportRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("创建报表 - SQL校验失败")
    void testCreateReport_InvalidSql() {
        // Given
        ReportDTO dto = ReportDTO.builder()
            .name("测试报表")
            .sqlContent("DROP TABLE users")
            .build();
        
        doThrow(new SecurityException("SQL包含非法关键字"))
            .when(sqlValidator).validate(dto.getSqlContent());
        
        // When & Then
        assertThatThrownBy(() -> reportService.createReport(dto))
            .isInstanceOf(SecurityException.class);
    }
}
```

**Repository层测试**：

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReportRepositoryTest {
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("根据名称查找报表")
    void testFindByName() {
        // Given
        Report report = Report.builder()
            .name("测试报表")
            .sqlContent("SELECT 1")
            .build();
        entityManager.persist(report);
        entityManager.flush();
        
        // When
        Optional<Report> found = reportRepository.findByName("测试报表");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("测试报表");
    }
    
    @Test
    @DisplayName("检查报表名称是否存在")
    void testExistsByName() {
        // Given
        Report report = Report.builder()
            .name("已存在的报表")
            .sqlContent("SELECT 1")
            .build();
        entityManager.persist(report);
        
        // When & Then
        assertThat(reportRepository.existsByName("已存在的报表")).isTrue();
        assertThat(reportRepository.existsByName("不存在的报表")).isFalse();
    }
}
```

### 2. 集成测试

**Controller层测试**：

```java
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ReportService reportService;
    
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("创建报表接口 - 成功")
    void testCreateReportAPI_Success() throws Exception {
        // Given
        ReportDTO dto = ReportDTO.builder()
            .name("测试报表")
            .sqlContent("SELECT * FROM users")
            .build();
        
        Report mockReport = Report.builder()
            .id(1L)
            .name(dto.getName())
            .build();
        
        when(reportService.createReport(any(ReportDTO.class)))
            .thenReturn(mockReport);
        
        // When & Then
        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("测试报表"));
    }
    
    @Test
    @WithMockUser(username = "viewer", roles = "VIEWER")
    @DisplayName("创建报表接口 - 权限不足")
    void testCreateReportAPI_Forbidden() throws Exception {
        // Given
        ReportDTO dto = ReportDTO.builder()
            .name("测试报表")
            .build();
        
        // When & Then
        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("创建报表接口 - 参数校验失败")
    void testCreateReportAPI_ValidationError() throws Exception {
        // Given - 名称为空
        ReportDTO dto = ReportDTO.builder()
            .sqlContent("SELECT 1")
            .build();
        
        // When & Then
        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("报表名称不能为空"));
    }
}
```

### 3. SQL安全测试

```java
@SpringBootTest
class SqlSecurityTest {
    
    @Autowired
    private SqlValidator sqlValidator;
    
    @Test
    @DisplayName("SQL注入检测 - DROP语句")
    void testSqlInjection_Drop() {
        String maliciousSql = "SELECT * FROM users WHERE id = 1; DROP TABLE users;";
        
        assertThatThrownBy(() -> sqlValidator.validate(maliciousSql))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("DROP");
    }
    
    @Test
    @DisplayName("SQL注入检测 - DELETE语句")
    void testSqlInjection_Delete() {
        String maliciousSql = "SELECT * FROM users; DELETE FROM users WHERE 1=1;";
        
        assertThatThrownBy(() -> sqlValidator.validate(maliciousSql))
            .isInstanceOf(SecurityException.class);
    }
    
    @Test
    @DisplayName("SQL注入检测 - 仅允许SELECT")
    void testSqlInjection_OnlySelect() {
        String validSql = "SELECT id, name FROM users WHERE age > 18";
        
        assertThatCode(() -> sqlValidator.validate(validSql))
            .doesNotThrowAnyException();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "DROP TABLE users",
        "DELETE FROM users",
        "TRUNCATE TABLE users",
        "UPDATE users SET password = '123'",
        "INSERT INTO users VALUES (1, 'hacker')"
    })
    @DisplayName("SQL注入检测 - 危险关键字")
    void testDangerousKeywords(String sql) {
        assertThatThrownBy(() -> sqlValidator.validate(sql))
            .isInstanceOf(SecurityException.class);
    }
}
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=ReportServiceTest

# 生成覆盖率报告
mvn verify

# 查看覆盖率报告
open target/site/jacoco/index.html
```

---

## 前端测试

### 测试框架

```json
{
  "devDependencies": {
    "vitest": "^1.0.0",
    "@vue/test-utils": "^2.4.0",
    "@vitest/ui": "^1.0.0",
    "jsdom": "^23.0.0",
    "playwright": "^1.40.0"
  }
}
```

### 1. 组件单元测试

**简单组件测试**：

```typescript
// tests/unit/components/ReportCard.spec.ts
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ReportCard from '@/components/report/ReportCard.vue'

describe('ReportCard.vue', () => {
  it('渲染报表信息', () => {
    const wrapper = mount(ReportCard, {
      props: {
        report: {
          id: 1,
          name: '用户统计报表',
          description: '统计每日用户数'
        }
      }
    })
    
    expect(wrapper.text()).toContain('用户统计报表')
    expect(wrapper.text()).toContain('统计每日用户数')
  })
  
  it('点击卡片触发select事件', async () => {
    const wrapper = mount(ReportCard, {
      props: {
        report: { id: 1, name: '测试报表' }
      }
    })
    
    await wrapper.find('.report-card').trigger('click')
    
    expect(wrapper.emitted()).toHaveProperty('select')
    expect(wrapper.emitted('select')?.[0]).toEqual([1])
  })
  
  it('显示加载状态', () => {
    const wrapper = mount(ReportCard, {
      props: {
        loading: true
      }
    })
    
    expect(wrapper.find('.loading-skeleton').exists()).toBe(true)
  })
})
```

**复杂组件测试**：

```typescript
// tests/unit/views/ReportList.spec.ts
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ReportList from '@/views/designer/ReportList.vue'
import { useReportStore } from '@/store/modules/report'

// Mock API
vi.mock('@/api/report', () => ({
  getReportList: vi.fn(() => Promise.resolve({
    data: [
      { id: 1, name: '报表1' },
      { id: 2, name: '报表2' }
    ]
  }))
}))

describe('ReportList.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })
  
  it('加载并显示报表列表', async () => {
    const wrapper = mount(ReportList)
    
    // 等待异步操作完成
    await flushPromises()
    
    const reportItems = wrapper.findAll('.report-item')
    expect(reportItems).toHaveLength(2)
    expect(reportItems[0].text()).toContain('报表1')
  })
  
  it('搜索过滤报表', async () => {
    const wrapper = mount(ReportList)
    await flushPromises()
    
    // 输入搜索关键字
    await wrapper.find('input[type="search"]').setValue('报表1')
    await wrapper.find('.search-btn').trigger('click')
    
    expect(wrapper.findAll('.report-item')).toHaveLength(1)
  })
  
  it('点击创建按钮显示对话框', async () => {
    const wrapper = mount(ReportList)
    
    await wrapper.find('.create-btn').trigger('click')
    
    expect(wrapper.find('.create-dialog').isVisible()).toBe(true)
  })
})
```

### 2. Composables测试

```typescript
// tests/unit/composables/useTable.spec.ts
import { describe, it, expect } from 'vitest'
import { useTable } from '@/composables/useTable'

describe('useTable', () => {
  it('初始化分页参数', () => {
    const { pagination } = useTable()
    
    expect(pagination.value).toEqual({
      page: 1,
      size: 20,
      total: 0
    })
  })
  
  it('处理分页变化', () => {
    const { pagination, handlePageChange } = useTable()
    
    handlePageChange(2)
    
    expect(pagination.value.page).toBe(2)
  })
  
  it('加载数据', async () => {
    const fetchFn = vi.fn(() => Promise.resolve({
      data: [1, 2, 3],
      total: 3
    }))
    
    const { dataList, pagination, loadData } = useTable(fetchFn)
    
    await loadData()
    
    expect(dataList.value).toEqual([1, 2, 3])
    expect(pagination.value.total).toBe(3)
    expect(fetchFn).toHaveBeenCalledTimes(1)
  })
})
```

### 3. Store测试

```typescript
// tests/unit/store/report.spec.ts
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useReportStore } from '@/store/modules/report'
import * as reportApi from '@/api/report'

vi.mock('@/api/report')

describe('Report Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })
  
  it('初始状态', () => {
    const store = useReportStore()
    
    expect(store.reportList).toEqual([])
    expect(store.currentReport).toBeNull()
    expect(store.loading).toBe(false)
  })
  
  it('获取报表列表', async () => {
    const mockReports = [
      { id: 1, name: '报表1' },
      { id: 2, name: '报表2' }
    ]
    
    vi.mocked(reportApi.getReportList).mockResolvedValue({
      data: mockReports
    })
    
    const store = useReportStore()
    await store.fetchReportList()
    
    expect(store.reportList).toEqual(mockReports)
    expect(reportApi.getReportList).toHaveBeenCalled()
  })
  
  it('getters - reportCount', () => {
    const store = useReportStore()
    store.reportList = [{ id: 1 }, { id: 2 }, { id: 3 }]
    
    expect(store.reportCount).toBe(3)
  })
})
```

### 运行测试

```bash
# 运行所有测试
npm run test

# 运行测试并监听文件变化
npm run test:watch

# 生成覆盖率报告
npm run test:coverage

# 查看覆盖率报告
open coverage/index.html

# 使用UI查看测试
npm run test:ui
```

---

## E2E测试

### Playwright配置

```typescript
// playwright.config.ts
import { defineConfig } from '@playwright/test'

export default defineConfig({
  testDir: './tests/e2e',
  timeout: 30000,
  use: {
    baseURL: 'http://localhost:5173',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure'
  },
  projects: [
    {
      name: 'chromium',
      use: { browserName: 'chromium' }
    }
  ]
})
```

### 核心流程测试

```typescript
// tests/e2e/report-workflow.spec.ts
import { test, expect } from '@playwright/test'

test.describe('报表完整流程', () => {
  test.beforeEach(async ({ page }) => {
    // 登录
    await page.goto('/login')
    await page.fill('input[name="username"]', 'designer')
    await page.fill('input[name="password"]', 'designer123')
    await page.click('button[type="submit"]')
    await page.waitForURL('/designer')
  })
  
  test('创建报表流程', async ({ page }) => {
    // 1. 进入报表列表页
    await page.goto('/designer/reports')
    
    // 2. 点击创建按钮
    await page.click('button:has-text("创建报表")')
    
    // 3. 填写报表信息
    await page.fill('input[name="name"]', 'E2E测试报表')
    await page.fill('textarea[name="sqlContent"]', 'SELECT * FROM users')
    
    // 4. 保存报表
    await page.click('button:has-text("保存")')
    
    // 5. 验证创建成功
    await expect(page.locator('.success-message')).toBeVisible()
    await expect(page.locator('text=E2E测试报表')).toBeVisible()
  })
  
  test('执行报表查询', async ({ page }) => {
    await page.goto('/viewer/reports')
    
    // 选择报表
    await page.click('.report-item:first-child')
    
    // 输入参数
    await page.fill('input[name="param1"]', '测试值')
    
    // 执行查询
    await page.click('button:has-text("查询")')
    
    // 验证结果
    await expect(page.locator('.result-table')).toBeVisible()
    await expect(page.locator('.result-row')).toHaveCount.greaterThan(0)
  })
  
  test('导出Excel', async ({ page }) => {
    await page.goto('/viewer/reports/1/result')
    
    // 点击导出按钮
    const downloadPromise = page.waitForEvent('download')
    await page.click('button:has-text("导出Excel")')
    const download = await downloadPromise
    
    // 验证文件名
    expect(download.suggestedFilename()).toMatch(/\.xlsx$/)
  })
})
```

### 运行E2E测试

```bash
# 安装Playwright浏览器
npx playwright install

# 运行E2E测试
npm run test:e2e

# 带UI界面运行
npx playwright test --ui

# 查看测试报告
npx playwright show-report
```

---

## 测试最佳实践

### 1. AAA模式

```java
@Test
void testExample() {
    // Arrange（准备）- 设置测试数据和mock
    ReportDTO dto = ReportDTO.builder().name("测试").build();
    when(repository.save(any())).thenReturn(mockReport);
    
    // Act（执行）- 调用被测方法
    Report result = service.createReport(dto);
    
    // Assert（断言）- 验证结果
    assertThat(result).isNotNull();
    verify(repository).save(any());
}
```

### 2. 测试命名

```java
// ✅ 好的命名
@Test
@DisplayName("创建报表 - 名称重复时抛出异常")
void testCreateReport_WhenDuplicateName_ThrowsException()

// ❌ 差的命名
@Test
void test1()
```

### 3. 独立性原则

```java
// ✅ 测试之间互不影响
@BeforeEach
void setUp() {
    // 每个测试前初始化
}

// ❌ 测试之间有依赖
static int counter = 0;  // 不要使用共享状态
```

### 4. 一个测试一个断言（概念）

```java
// ✅ 推荐
@Test
void testCreateReport_Success() {
    Report result = service.createReport(dto);
    
    // 验证同一个概念的多个方面
    assertThat(result)
        .isNotNull()
        .extracting(Report::getName, Report::getSqlContent)
        .containsExactly("报表名", "SELECT 1");
}

// ❌ 不推荐 - 测试多个不相关的东西
@Test
void testEverything() {
    // 测试创建
    Report report = service.create(dto);
    // 测试更新
    service.update(report);
    // 测试删除
    service.delete(report.getId());
}
```

### 5. Mock vs 真实依赖

```java
// 使用Mock：外部依赖（数据库、API、文件系统）
@MockBean
private ExternalApiClient apiClient;

// 使用真实对象：简单的工具类
private DateFormatter formatter = new DateFormatter();
```

---

**最后更新**：2026-01-15  
**适用版本**：v1.0-MVP
