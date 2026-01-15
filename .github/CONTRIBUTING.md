# 贡献指南

欢迎为GCT Reporter项目做出贡献！本文档是项目规范的总入口，详细规范请查看以下分类文档。

---

## 📚 规范文档导航

### 开发规范

- 📘 [后端开发规范](guidelines/BACKEND_GUIDELINES.md) - Java/Spring Boot开发规范
- 📗 [前端开发规范](guidelines/FRONTEND_GUIDELINES.md) - Vue/TypeScript开发规范
- 🧪 [测试规范](guidelines/TESTING_GUIDELINES.md) - 前后端测试要求和示例

### 协作规范

- 🌿 [Git与分支管理](guidelines/GIT_GUIDELINES.md) - Git提交、分支策略
- 🔍 [代码审查规范](guidelines/CODE_REVIEW.md) - PR流程和审查清单
- 🔒 [安全政策](SECURITY.md) - 安全要求和漏洞报告

### 项目配置

- ⚙️ [.editorconfig](../.editorconfig) - 编辑器统一配置
- 📋 [.gitignore](../.gitignore) - Git忽略规则
- 📝 [PR模板](PULL_REQUEST_TEMPLATE.md) - Pull Request模板
- 🤝 [行为准则](CODE_OF_CONDUCT.md) - 社区行为准则

---

## 开发规范

### 技术栈要求

**后端 (Java SpringBoot)**
- Java 17 LTS
- Spring Boot 3.1.x
- Spring Data JPA (开发) / MyBatis (生产)
- SQLite 3.x (开发) / Oracle 12g (生产)
- Maven 3.8+

**前端 (Vue)**
- Node.js 18.x LTS
- Vue 3.3.x (组合式API)
- TypeScript
- Vite 4.x
- Element Plus 2.3.x

### 开发环境搭建

```bash
# 1. 克隆项目
git clone https://github.com/your-org/gct-reporter.git
cd gct-reporter

# 2. 安装后端依赖
cd backend
mvn clean install

# 3. 启动后端服务
mvn spring-boot:run

# 4. 安装前端依赖
cd ../frontend
npm install

# 5. 启动前端开发服务器
npm run dev
```

---

## 代码规范

### Java代码规范

**强制要求**：
- ✅ 遵循[阿里巴巴Java开发手册](https://github.com/alibaba/p3c)
- ✅ 使用Lombok减少样板代码
- ✅ 使用CheckStyle检查代码风格
- ✅ 使用SonarLint进行代码质量检查
- ✅ 所有public方法必须添加JavaDoc注释
- ✅ 类名使用PascalCase，方法名使用camelCase

**推荐实践**：
```java
// ✅ 推荐：清晰的命名和注释
/**
 * 执行报表查询
 * 
 * @param reportId 报表ID
 * @param params 查询参数
 * @return 查询结果列表
 * @throws ReportNotFoundException 报表不存在
 */
public List<Map<String, Object>> executeReport(Long reportId, Map<String, Object> params) {
    // 实现逻辑
}

// ❌ 不推荐：无注释，命名不清晰
public List<Map<String, Object>> exec(Long id, Map<String, Object> p) {
    // 实现逻辑
}
```

### 前端代码规范

**强制要求**：
- ✅ 遵循[Vue 3官方风格指南](https://cn.vuejs.org/style-guide/)
- ✅ 使用ESLint + Prettier格式化代码
- ✅ 使用TypeScript类型检查
- ✅ 组件命名采用PascalCase
- ✅ 组件文件名采用PascalCase
- ✅ 所有组件必须添加注释说明用途

**推荐实践**：
```typescript
// ✅ 推荐：使用组合式API + TypeScript
<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Report {
  id: number
  name: string
  description: string
}

const reportList = ref<Report[]>([])

/**
 * 加载报表列表
 */
const loadReports = async () => {
  // 实现逻辑
}

onMounted(() => {
  loadReports()
})
</script>

// ❌ 不推荐：使用选项式API，无类型定义
<script>
export default {
  data() {
    return {
      reportList: []
    }
  }
}
</script>
```

### 代码格式化配置

项目已配置自动格式化工具，提交前请确保代码已格式化：

```bash
# Java代码格式化（使用Maven插件）
mvn spotless:apply

# 前端代码格式化
npm run format

# 代码检查
npm run lint
```

---

## 测试规范

### 测试覆盖率要求

```yaml
后端测试:
  单元测试覆盖率: ≥80%
  API集成测试: 100%核心接口
  SQL安全测试: 100%SQL注入场景

前端测试:
  组件单元测试: ≥60%
  E2E测试: 核心流程（登录、创建报表、查询）
```

### Java测试框架

**使用工具**：
- JUnit 5（单元测试）
- MockMvc（API集成测试）
- Mockito（Mock框架）
- JaCoCo（覆盖率统计）
- Selenium / Playwright（E2E测试）

**测试示例**：

```java
// 单元测试示例
@SpringBootTest
class ReportServiceTest {
    
    @Autowired
    private ReportService reportService;
    
    @MockBean
    private ReportRepository reportRepository;
    
    @Test
    @DisplayName("测试创建报表 - 成功场景")
    void testCreateReport_Success() {
        // Given
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setName("测试报表");
        
        Report mockReport = new Report();
        mockReport.setId(1L);
        when(reportRepository.save(any())).thenReturn(mockReport);
        
        // When
        Report result = reportService.createReport(reportDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reportRepository, times(1)).save(any());
    }
    
    @Test
    @DisplayName("测试创建报表 - 名称重复异常")
    void testCreateReport_DuplicateName() {
        // Given
        when(reportRepository.existsByName(anyString())).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateReportNameException.class, () -> {
            reportService.createReport(new ReportDTO());
        });
    }
}

// API集成测试示例
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "DESIGNER")
    void testCreateReportAPI() throws Exception {
        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"测试报表\",\"sqlContent\":\"SELECT * FROM users\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
```

### 前端测试框架

**使用工具**：
- Vitest（单元测试）
- Vue Test Utils（组件测试）
- Playwright（E2E测试）

**测试示例**：

```typescript
// 组件单元测试示例
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import ReportList from '@/components/ReportList.vue'

describe('ReportList.vue', () => {
  it('渲染报表列表', () => {
    const wrapper = mount(ReportList, {
      props: {
        reports: [
          { id: 1, name: '测试报表1' },
          { id: 2, name: '测试报表2' }
        ]
      }
    })
    
    expect(wrapper.findAll('.report-item')).toHaveLength(2)
  })
})
```

### 运行测试

```bash
# 后端测试
cd backend
mvn test                    # 运行所有测试
mvn test -Dtest=ReportServiceTest  # 运行指定测试
mvn verify                  # 运行测试并生成覆盖率报告

# 前端测试
cd frontend
npm run test               # 运行单元测试
npm run test:e2e          # 运行E2E测试
npm run test:coverage     # 生成覆盖率报告
```

---

## 安全规范

### SQL安全要求

**强制规则**：

1. **✅ 必须使用参数化查询**

```java
// ✅ 正确示例：使用PreparedStatement
public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params) {
    NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    return jdbcTemplate.queryForList(sql, params);
}

// ❌ 错误示例：字符串拼接（SQL注入风险）
public List<Map<String, Object>> executeQueryUnsafe(String sql, Map<String, Object> params) {
    for (Map.Entry<String, Object> entry : params.entrySet()) {
        sql = sql.replace(":" + entry.getKey(), String.valueOf(entry.getValue()));
    }
    return jdbcTemplate.queryForList(sql);  // 危险！
}
```

2. **✅ SQL关键字黑名单校验**

禁止以下操作：
- `DROP`、`DELETE`、`TRUNCATE`、`UPDATE`
- `INSERT`、`ALTER`、`CREATE`、`GRANT`
- `EXECUTE`、`EXEC`、仅允许`SELECT`语句

3. **✅ 查询超时控制**

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setConnectionTimeout(5000);  // 5秒超时
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
}
```

4. **✅ 使用只读数据库账号**

生产环境必须使用只读权限的数据库账号执行查询。

### 身份认证与授权

**强制要求**：

1. **✅ 密码加密存储**

```java
// 使用BCrypt加密
@Service
public class UserService {
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    public void createUser(String username, String plainPassword) {
        String encodedPassword = passwordEncoder.encode(plainPassword);
        // 保存到数据库
    }
}
```

2. **✅ Session安全**

```yaml
# application.yml
server:
  servlet:
    session:
      timeout: 30m  # Session超时30分钟
      cookie:
        http-only: true
        secure: true  # 生产环境启用HTTPS
```

3. **✅ RBAC权限控制**

所有API接口必须添加权限注解：

```java
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'VIEWER')")
    public List<Report> listReports() {
        // 实现逻辑
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public Report createReport(@RequestBody ReportDTO dto) {
        // 实现逻辑
    }
}
```

### 数据安全

**强制要求**：

1. **✅ 敏感数据脱敏**

日志中不得输出敏感信息（密码、Token等）

```java
// ✅ 正确
log.info("用户登录成功: username={}", username);

// ❌ 错误
log.info("用户登录成功: username={}, password={}", username, password);
```

2. **✅ 完整的审计日志**

所有查询操作必须记录：

```java
@Service
public class QueryLogService {
    
    public void logQuery(Long userId, Long reportId, Map<String, Object> params, boolean success) {
        ExecutionLog log = new ExecutionLog();
        log.setUserId(userId);
        log.setReportId(reportId);
        log.setParamsJson(JSON.toJSONString(params));
        log.setExecuteTime(LocalDateTime.now());
        log.setSuccess(success);
        executionLogRepository.save(log);
    }
}
```

### 安全检查清单

提交代码前请确认：

- [ ] ✅ 所有SQL查询使用参数化
- [ ] ✅ 密码使用BCrypt加密
- [ ] ✅ API接口添加权限注解
- [ ] ✅ SQL关键字黑名单校验
- [ ] ✅ 查询超时控制（5秒）
- [ ] ✅ 执行日志完整记录
- [ ] ✅ 敏感信息不记录到日志
- [ ] ✅ CORS跨域配置正确

---

## Git提交规范

### 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(report): 添加报表导出功能` |
| `fix` | Bug修复 | `fix(auth): 修复登录超时问题` |
| `docs` | 文档更新 | `docs(readme): 更新安装说明` |
| `style` | 代码格式调整 | `style(user): 格式化代码缩进` |
| `refactor` | 重构代码 | `refactor(service): 重构查询服务` |
| `perf` | 性能优化 | `perf(query): 优化SQL查询性能` |
| `test` | 测试相关 | `test(report): 添加报表单元测试` |
| `build` | 构建系统 | `build(maven): 升级依赖版本` |
| `ci` | CI配置 | `ci(github): 添加GitHub Actions` |
| `chore` | 其他杂项 | `chore(deps): 更新依赖包` |

### Scope范围

常用范围：
- `auth` - 认证授权
- `user` - 用户管理
- `report` - 报表功能
- `query` - 查询执行
- `export` - 导出功能
- `ui` - 前端界面
- `api` - API接口
- `db` - 数据库

### 提交示例

```bash
# 好的提交示例
feat(report): 添加SQL语法高亮功能

- 集成CodeMirror编辑器
- 支持MySQL语法高亮
- 添加自动补全功能

Closes #123

# 简单的提交
fix(auth): 修复密码加密问题

# 破坏性变更
feat(api): 重构报表查询API接口

BREAKING CHANGE: 查询接口路径从 /query 改为 /execute
```

### 提交注意事项

- ✅ 一次提交只做一件事
- ✅ 提交信息清晰描述改动内容
- ✅ 关联Issue编号（如 `Closes #123`）
- ✅ 破坏性变更必须标注 `BREAKING CHANGE`
- ❌ 避免提交无意义的信息（如 "update", "fix bug"）
- ❌ 避免提交未完成的代码到develop/main分支

---

## 分支管理

### 分支策略

```
main         # 主分支，受保护，仅发布版本
  ├── develop       # 开发分支，日常开发合并目标
  │   ├── feature/US001-user-login      # 功能分支
  │   ├── feature/US010-report-design   # 功能分支
  │   └── bugfix/fix-query-timeout      # Bug修复分支
  └── release/v1.0.0   # 发布分支
```

### 分支命名规范

| 分支类型 | 命名格式 | 示例 |
|---------|---------|------|
| 功能分支 | `feature/<story-id>-<description>` | `feature/US001-user-login` |
| Bug修复 | `bugfix/<issue-id>-<description>` | `bugfix/123-fix-timeout` |
| 热修复 | `hotfix/<version>-<description>` | `hotfix/v1.0.1-security-patch` |
| 发布分支 | `release/v<version>` | `release/v1.0.0` |

### 分支操作流程

**创建功能分支**：

```bash
# 1. 从develop创建新分支
git checkout develop
git pull origin develop
git checkout -b feature/US001-user-login

# 2. 开发完成后提交
git add .
git commit -m "feat(auth): 实现用户登录功能"

# 3. 推送到远程
git push origin feature/US001-user-login

# 4. 创建Pull Request到develop分支
```

**合并流程**：

```bash
# 1. 更新develop分支
git checkout develop
git pull origin develop

# 2. 合并功能分支（使用--no-ff保留分支历史）
git merge --no-ff feature/US001-user-login

# 3. 删除已合并的功能分支
git branch -d feature/US001-user-login
git push origin --delete feature/US001-user-login
```

---

## 质量门禁

### 代码提交前检查

自动化检查（Pre-commit Hook）：

```bash
# .git/hooks/pre-commit
#!/bin/bash

# Java代码格式检查
cd backend
mvn spotless:check
if [ $? -ne 0 ]; then
  echo "❌ Java代码格式检查失败，请运行 mvn spotless:apply"
  exit 1
fi

# 前端代码格式检查
cd ../frontend
npm run lint
if [ $? -ne 0 ]; then
  echo "❌ 前端代码检查失败，请运行 npm run lint:fix"
  exit 1
fi

echo "✅ 代码检查通过"
```

手动检查清单：

- [ ] ✅ 代码已格式化
- [ ] ✅ 单元测试已编写并通过
- [ ] ✅ 无编译警告
- [ ] ✅ 已添加必要的注释
- [ ] ✅ 提交信息符合规范

### 合并到develop前检查

- [ ] ✅ 代码审查通过（至少1人Review）
- [ ] ✅ 所有单元测试通过
- [ ] ✅ 集成测试通过
- [ ] ✅ 无代码冲突
- [ ] ✅ 满足验收标准（AC）

### 发布到main前检查

- [ ] ✅ 所有功能测试通过
- [ ] ✅ 性能测试达标
- [ ] ✅ 安全扫描无高危漏洞
- [ ] ✅ 产品验收通过
- [ ] ✅ 发布文档已更新
- [ ] ✅ 数据库迁移脚本已准备

---

## Pull Request流程

### 创建PR

1. **确保分支最新**：

```bash
git checkout develop
git pull origin develop
git checkout feature/US001-user-login
git rebase develop
```

2. **创建PR**：

- 标题格式：`[US001] 实现用户登录功能`
- 描述使用PR模板（见`.github/PULL_REQUEST_TEMPLATE.md`）
- 关联相关Issue
- 添加合适的标签（`enhancement`, `bug`, `documentation`）
- 指定审查人员

### 代码审查清单

**功能性**：
- [ ] 功能实现符合需求
- [ ] 边界条件处理正确
- [ ] 错误处理完善

**代码质量**：
- [ ] 代码可读性好
- [ ] 命名清晰规范
- [ ] 无重复代码
- [ ] 注释充分

**测试**：
- [ ] 单元测试覆盖充分
- [ ] 测试用例合理
- [ ] 边界测试完整

**安全**：
- [ ] 无SQL注入风险
- [ ] 权限控制正确
- [ ] 敏感信息处理安全

### 审查反馈处理

1. 根据审查意见修改代码
2. 回复每条评论说明修改情况
3. 重新请求审查
4. 审查通过后由审查人员合并

---

## 定义完成（Definition of Done）

每个用户故事必须满足以下标准才算完成：

### 开发层面

- [ ] ✅ 代码编写完成并通过编译
- [ ] ✅ 代码符合编码规范
- [ ] ✅ 代码已提交到功能分支
- [ ] ✅ 无编译警告

### 测试层面

- [ ] ✅ 单元测试编写并通过（覆盖率≥80%）
- [ ] ✅ 集成测试通过
- [ ] ✅ 满足所有验收标准（AC）
- [ ] ✅ 手工测试通过

### 质量层面

- [ ] ✅ 代码审查通过（至少1人Review）
- [ ] ✅ 无P0/P1级别Bug
- [ ] ✅ 性能测试达标
- [ ] ✅ 安全检查通过

### 文档层面

- [ ] ✅ API文档已更新
- [ ] ✅ 用户文档已更新（如需要）
- [ ] ✅ 数据库变更已记录

### 部署层面

- [ ] ✅ 代码已合并到develop分支
- [ ] ✅ 数据库迁移脚本已准备（如需要）
- [ ] ✅ 配置文件已更新（如需要）

---

## 性能要求

### 性能指标

| 指标 | 要求 | 测试方法 |
|------|------|---------|
| 1000行数据查询 | P95 < 3秒 | JMeter压力测试 |
| Excel导出（1000行） | < 5秒 | 功能测试 |
| 报表列表加载 | < 1秒 | Lighthouse性能测试 |
| 登录响应时间 | P95 < 2秒 | 50并发用户测试 |
| 5用户并发查询 | 无阻塞 | 并发测试 |

### 性能优化建议

**后端优化**：
- 数据库索引优化（username、report_id、execute_time字段）
- 连接池配置（HikariCP，最大连接数10）
- 查询超时控制（5秒限制）
- 适当使用缓存（Redis）

**前端优化**：
- 虚拟滚动（大数据量表格）
- 懒加载（报表列表分页加载）
- 防抖/节流（搜索输入框）
- 代码分割（路由懒加载）

---

## 获取帮助

### 常见问题

1. **如何运行测试？**
   - 后端：`mvn test`
   - 前端：`npm run test`

2. **如何格式化代码？**
   - 后端：`mvn spotless:apply`
   - 前端：`npm run format`

3. **如何查看测试覆盖率？**
   - 后端：`mvn verify`，查看 `target/site/jacoco/index.html`
   - 前端：`npm run test:coverage`，查看 `coverage/index.html`

### 联系方式

- 技术问题：创建Issue或在讨论区提问
- 紧急问题：联系技术负责人
- 文档问题：提交PR修改文档

---

**感谢您的贡献！** 🎉
