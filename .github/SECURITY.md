# 安全政策

## 支持的版本

目前支持安全更新的项目版本：

| 版本 | 支持状态 |
| ------- | ------------------ |
| 1.0.x (MVP)   | :white_check_mark: 正在开发 |

## 报告安全漏洞

我们非常重视安全问题。如果您发现了安全漏洞，请**不要**通过公开的Issue报告。

### 如何报告

请通过以下方式之一私密地报告安全问题：

1. **首选方式**：发送邮件至 [security@example.com]（待定）
2. **备选方式**：通过GitHub Security Advisory私密报告

### 报告内容应包括

- 漏洞类型（SQL注入、XSS、权限绕过等）
- 受影响的组件和版本
- 复现步骤（尽可能详细）
- 潜在影响评估
- 建议的修复方案（如有）

### 响应时间

- **确认收到**：24小时内
- **初步评估**：3个工作日内
- **修复方案**：根据严重程度，7-30天内

## 安全最佳实践

### 开发者指南

#### 1. SQL安全

**强制要求**：
```java
// ✅ 正确：使用参数化查询
NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
template.queryForList("SELECT * FROM users WHERE username = :username", params);

// ❌ 错误：字符串拼接
String sql = "SELECT * FROM users WHERE username = '" + username + "'";  // SQL注入风险！
```

**关键字黑名单**：
- 禁止：`DROP`, `DELETE`, `TRUNCATE`, `UPDATE`, `INSERT`, `ALTER`, `CREATE`, `GRANT`, `EXECUTE`
- 仅允许：`SELECT` 语句

#### 2. 身份认证

**密码存储**：
```java
// ✅ 使用BCrypt加密
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode(plainPassword);
```

**Session管理**：
```yaml
# application.yml
server:
  servlet:
    session:
      timeout: 30m  # 30分钟超时
      cookie:
        http-only: true
        secure: true  # 生产环境必须启用HTTPS
        same-site: strict
```

#### 3. 权限控制

**API权限注解**：
```java
@PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
public Report createReport(@RequestBody ReportDTO dto) {
    // 实现逻辑
}
```

**权限验证矩阵**：

| 操作 | ADMIN | DESIGNER | VIEWER |
|------|-------|----------|--------|
| 创建报表 | ✅ | ✅ | ❌ |
| 编辑报表 | ✅ | ✅ (仅自己创建的) | ❌ |
| 删除报表 | ✅ | ✅ (仅自己创建的) | ❌ |
| 查询报表 | ✅ | ✅ | ✅ (仅授权的) |
| 用户管理 | ✅ | ❌ | ❌ |

#### 4. 数据验证

**输入验证**：
```java
@RestController
public class ReportController {
    
    @PostMapping("/api/reports")
    public Report createReport(@Valid @RequestBody ReportDTO dto) {
        // @Valid触发JSR-303验证
    }
}

@Data
public class ReportDTO {
    @NotBlank(message = "报表名称不能为空")
    @Size(max = 100, message = "报表名称不能超过100字符")
    private String name;
    
    @NotBlank(message = "SQL内容不能为空")
    @Pattern(regexp = "^SELECT.*", message = "仅允许SELECT语句")
    private String sqlContent;
}
```

#### 5. 敏感信息保护

**日志脱敏**：
```java
// ✅ 正确
log.info("用户登录: username={}", username);

// ❌ 错误
log.info("用户登录: username={}, password={}", username, password);
```

**配置文件加密**：
```yaml
# 使用环境变量或加密工具
spring:
  datasource:
    password: ${DB_PASSWORD}  # 从环境变量读取
```

### 部署安全

#### 生产环境检查清单

**应用配置**：
- [ ] HTTPS已启用
- [ ] Session Cookie设置为Secure和HttpOnly
- [ ] CORS配置限制来源域名
- [ ] 关闭详细错误信息（仅返回通用错误）
- [ ] 启用请求速率限制

**数据库配置**：
- [ ] 使用只读账号执行查询
- [ ] 数据库密码强度符合要求（≥12位，包含大小写、数字、特殊字符）
- [ ] 限制数据库连接来源IP
- [ ] 启用SQL审计日志
- [ ] 定期备份数据库

**网络安全**：
- [ ] 防火墙规则配置正确
- [ ] 仅开放必要端口（80/443）
- [ ] 启用DDoS防护
- [ ] 配置WAF（Web应用防火墙）

**监控告警**：
- [ ] 异常登录行为监控
- [ ] SQL注入尝试告警
- [ ] 权限异常访问告警
- [ ] 系统资源使用监控

## 已知安全限制

### MVP版本限制

以下安全增强功能在MVP版本中不包含，将在后续版本实现：

- ❌ 双因素认证（2FA）
- ❌ 登录失败次数限制和账号锁定
- ❌ IP白名单/黑名单
- ❌ 详细的审计日志（仅记录基本操作）
- ❌ 数据脱敏（查询结果中的敏感字段）
- ❌ 细粒度的数据权限控制（行级权限）

### 使用限制

MVP版本适用场景：
- ✅ 内部网络环境
- ✅ 可信用户群体
- ✅ 非敏感数据查询
- ❌ 不建议用于公网暴露环境
- ❌ 不建议查询包含个人隐私的数据

## 安全更新

### 依赖管理

定期更新依赖包以修复已知漏洞：

```bash
# 检查依赖漏洞（Java）
mvn dependency-check:check

# 检查依赖漏洞（前端）
npm audit
```

### 订阅安全公告

关注以下安全公告：
- [Spring Security Advisories](https://spring.io/security)
- [Vue.js Security](https://vuejs.org/about/security.html)
- [GitHub Security Advisories](https://github.com/advisories)

## 安全团队

负责安全事务的团队成员：

- **安全负责人**：[待定]
- **技术负责人**：[待定]

---

**最后更新**：2026-01-15
**版本**：v1.0
