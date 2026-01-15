# 后端开发规范（Java/Spring Boot）

> 适用于GCT Reporter项目的Java后端开发

---

## 📋 目录

- [技术栈](#技术栈)
- [环境搭建](#环境搭建)
- [项目结构](#项目结构)
- [编码规范](#编码规范)
- [数据库规范](#数据库规范)
- [API设计规范](#api设计规范)
- [安全规范](#安全规范)
- [性能优化](#性能优化)
- [日志规范](#日志规范)
- [依赖管理](#依赖管理)

---

## 技术栈

### 核心框架

```yaml
语言: Java 17 LTS
框架: Spring Boot 3.1.x
ORM: 
  - Spring Data JPA (开发阶段)
  - MyBatis (生产阶段)
数据库:
  - SQLite 3.x (开发环境)
  - Oracle 12g (生产环境)
构建工具: Maven 3.8+
```

### 主要依赖

```xml
<!-- pom.xml 核心依赖 -->
<dependencies>
    <!-- Spring Boot核心 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- 数据访问 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- 安全框架 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- 数据验证 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    
    <!-- Excel处理 -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
    </dependency>
</dependencies>
```

---

## 环境搭建

### 1. 安装JDK 17

```bash
# macOS (使用Homebrew)
brew install openjdk@17

# Windows (使用Chocolatey)
choco install openjdk17

# 验证安装
java -version  # 应显示 java version "17.x.x"
```

### 2. 安装Maven

```bash
# macOS
brew install maven

# Windows
choco install maven

# 验证安装
mvn -version
```

### 3. 配置IDE

**IntelliJ IDEA（推荐）**

1. 安装插件：
   - Lombok
   - CheckStyle-IDEA
   - SonarLint

2. 配置代码风格：
   - Settings → Editor → Code Style → Java
   - 导入项目根目录的 `codestyle.xml`（待创建）

3. 启用注解处理：
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - 勾选 "Enable annotation processing"

**Eclipse**

1. 安装Lombok：下载lombok.jar并运行安装程序
2. 安装CheckStyle插件
3. 导入代码风格配置

### 4. 初始化项目

```bash
# 克隆项目
git clone https://github.com/your-org/gct-reporter.git
cd gct-reporter/backend

# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run

# 访问API文档
# http://localhost:8080/swagger-ui.html
```

---

## 项目结构

### 标准目录结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gct/reporter/
│   │   │       ├── GctReporterApplication.java      # 启动类
│   │   │       ├── config/                          # 配置类
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── DataSourceConfig.java
│   │   │       │   └── SwaggerConfig.java
│   │   │       ├── controller/                      # 控制器
│   │   │       │   ├── UserController.java
│   │   │       │   ├── ReportController.java
│   │   │       │   └── QueryController.java
│   │   │       ├── service/                         # 服务层
│   │   │       │   ├── UserService.java
│   │   │       │   ├── ReportService.java
│   │   │       │   └── impl/
│   │   │       │       ├── UserServiceImpl.java
│   │   │       │       └── ReportServiceImpl.java
│   │   │       ├── repository/                      # 数据访问层
│   │   │       │   ├── UserRepository.java
│   │   │       │   └── ReportRepository.java
│   │   │       ├── entity/                          # 实体类
│   │   │       │   ├── User.java
│   │   │       │   └── Report.java
│   │   │       ├── dto/                             # 数据传输对象
│   │   │       │   ├── UserDTO.java
│   │   │       │   └── ReportDTO.java
│   │   │       ├── vo/                              # 视图对象
│   │   │       │   └── ReportVO.java
│   │   │       ├── exception/                       # 异常类
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── BusinessException.java
│   │   │       ├── security/                        # 安全相关
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   └── UserDetailsServiceImpl.java
│   │   │       ├── util/                            # 工具类
│   │   │       │   ├── SqlValidator.java
│   │   │       │   └── ExcelExporter.java
│   │   │       └── constant/                        # 常量类
│   │   │           └── RoleConstant.java
│   │   └── resources/
│   │       ├── application.yml                      # 主配置文件
│   │       ├── application-dev.yml                  # 开发环境
│   │       ├── application-prod.yml                 # 生产环境
│   │       └── db/migration/                        # Flyway迁移脚本
│   │           ├── V1__init_schema.sql
│   │           └── V2__add_indexes.sql
│   └── test/
│       └── java/
│           └── com/gct/reporter/
│               ├── service/
│               │   └── ReportServiceTest.java
│               └── controller/
│                   └── ReportControllerTest.java
├── pom.xml
└── README.md
```

### 包命名规范

```
com.gct.reporter          # 根包
├── config                # 配置类（@Configuration）
├── controller            # 控制器（@RestController）
├── service               # 业务接口
│   └── impl              # 业务实现
├── repository            # 数据访问（@Repository）
├── entity                # 实体类（@Entity）
├── dto                   # 数据传输对象（用于接口入参）
├── vo                    # 视图对象（用于接口出参）
├── exception             # 异常类
├── security              # 安全认证
├── util                  # 工具类
└── constant              # 常量类
```

---

## 编码规范

### 1. 命名规范

**类名**：PascalCase（大驼峰）
```java
// ✅ 正确
public class UserService { }
public class ReportController { }

// ❌ 错误
public class userservice { }
public class report_controller { }
```

**方法名/变量名**：camelCase（小驼峰）
```java
// ✅ 正确
public void createReport() { }
private String reportName;

// ❌ 错误
public void CreateReport() { }
private String report_name;
```

**常量名**：UPPER_SNAKE_CASE
```java
// ✅ 正确
public static final String DEFAULT_ROLE = "VIEWER";
public static final int MAX_PAGE_SIZE = 100;

// ❌ 错误
public static final String defaultRole = "VIEWER";
```

**包名**：全小写
```java
// ✅ 正确
package com.gct.reporter.service;

// ❌ 错误
package com.gct.reporter.Service;
```

### 2. 注释规范

**类注释**：
```java
/**
 * 报表服务接口
 * 
 * <p>提供报表的CRUD操作、查询执行、Excel导出等功能</p>
 *
 * @author 张三
 * @since 1.0
 */
public interface ReportService {
}
```

**方法注释**：
```java
/**
 * 执行报表查询
 *
 * @param reportId 报表ID
 * @param params 查询参数Map，key为参数名，value为参数值
 * @return 查询结果列表，每个元素为一行数据
 * @throws ReportNotFoundException 报表不存在时抛出
 * @throws SqlExecutionException SQL执行失败时抛出
 */
List<Map<String, Object>> executeReport(Long reportId, Map<String, Object> params);
```

**字段注释**：
```java
public class Report {
    /** 报表ID */
    private Long id;
    
    /** 报表名称，唯一索引 */
    @Column(unique = true)
    private String name;
    
    /** SQL查询语句，仅支持SELECT */
    @Column(columnDefinition = "TEXT")
    private String sqlContent;
}
```

### 3. 代码格式

**使用Lombok减少样板代码**：
```java
// ✅ 推荐
@Data
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String sqlContent;
    private LocalDateTime createdAt;
}

// ❌ 不推荐（手写getter/setter）
public class Report {
    private Long id;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... 重复代码
}
```

**使用Builder模式**：
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String name;
    private String description;
    private String sqlContent;
}

// 使用示例
ReportDTO dto = ReportDTO.builder()
    .name("用户统计报表")
    .description("统计每日用户注册数")
    .sqlContent("SELECT * FROM users")
    .build();
```

### 4. 异常处理

**自定义业务异常**：
```java
@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}

// 具体异常
public class ReportNotFoundException extends BusinessException {
    public ReportNotFoundException(Long reportId) {
        super("REPORT_NOT_FOUND", "报表不存在: " + reportId);
    }
}
```

**全局异常处理**：
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReportNotFound(ReportNotFoundException e) {
        log.error("报表未找到: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("系统异常", e);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "系统错误，请联系管理员");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## 数据库规范

### 1. 实体类规范

```java
@Data
@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_creator", columnList = "creator_id")
})
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "sql_content", nullable = false, columnDefinition = "TEXT")
    private String sqlContent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### 2. Repository规范

```java
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    /**
     * 根据报表名称查询
     */
    Optional<Report> findByName(String name);
    
    /**
     * 查询指定创建者的所有报表
     */
    List<Report> findByCreatorId(Long creatorId);
    
    /**
     * 检查报表名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 自定义查询：查询用户有权限的报表
     */
    @Query("SELECT r FROM Report r " +
           "JOIN RoleReport rr ON r.id = rr.reportId " +
           "JOIN UserRole ur ON rr.role = ur.role " +
           "WHERE ur.userId = :userId")
    List<Report> findAuthorizedReports(@Param("userId") Long userId);
}
```

### 3. 数据库迁移（Flyway）

**命名规范**：`V<版本号>__<描述>.sql`

```sql
-- V1__init_schema.sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- V2__add_reports_table.sql
CREATE TABLE reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    sql_content TEXT NOT NULL,
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE INDEX idx_reports_name ON reports(name);
CREATE INDEX idx_reports_creator ON reports(creator_id);
```

---

## API设计规范

### 1. RESTful API规范

**URL设计**：
```
GET    /api/reports          # 获取报表列表
GET    /api/reports/{id}     # 获取单个报表
POST   /api/reports          # 创建报表
PUT    /api/reports/{id}     # 更新报表
DELETE /api/reports/{id}     # 删除报表
POST   /api/reports/{id}/execute  # 执行报表查询
```

**Controller示例**：
```java
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * 获取报表列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'VIEWER')")
    public ResponseEntity<List<ReportVO>> listReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<ReportVO> reports = reportService.listReports(page, size);
        return ResponseEntity.ok(reports);
    }
    
    /**
     * 创建报表
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER')")
    public ResponseEntity<ReportVO> createReport(
            @Valid @RequestBody ReportDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("创建报表: {}, 创建人: {}", dto.getName(), userDetails.getUsername());
        ReportVO report = reportService.createReport(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
    
    /**
     * 执行报表查询
     */
    @PostMapping("/{id}/execute")
    @PreAuthorize("hasAnyRole('ADMIN', 'DESIGNER', 'VIEWER')")
    public ResponseEntity<QueryResultVO> executeReport(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("执行报表: {}, 用户: {}", id, userDetails.getUsername());
        QueryResultVO result = reportService.executeReport(id, params);
        return ResponseEntity.ok(result);
    }
}
```

### 2. 统一响应格式

```java
@Data
@Builder
public class ApiResponse<T> {
    private String code;      // 响应码
    private String message;   // 响应消息
    private T data;           // 响应数据
    private Long timestamp;   // 时间戳
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .code("SUCCESS")
            .message("操作成功")
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
            .code(code)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
```

### 3. 参数验证

```java
@Data
public class ReportDTO {
    
    @NotBlank(message = "报表名称不能为空")
    @Size(max = 100, message = "报表名称不能超过100字符")
    private String name;
    
    @Size(max = 500, message = "描述不能超过500字符")
    private String description;
    
    @NotBlank(message = "SQL内容不能为空")
    @Pattern(regexp = "^SELECT.*", flags = Pattern.Flag.CASE_INSENSITIVE, 
             message = "仅支持SELECT查询语句")
    private String sqlContent;
}
```

---

## 安全规范

### 1. SQL注入防护

**✅ 正确示例**：
```java
@Service
@RequiredArgsConstructor
public class QueryService {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params) {
        // 使用参数化查询
        return jdbcTemplate.queryForList(sql, params);
    }
}
```

**❌ 错误示例**：
```java
// 危险！不要这样做
public List<Map<String, Object>> executeQueryUnsafe(String sql, Map<String, Object> params) {
    for (Map.Entry<String, Object> entry : params.entrySet()) {
        sql = sql.replace(":" + entry.getKey(), String.valueOf(entry.getValue()));
    }
    return jdbcTemplate.queryForList(sql);  // SQL注入风险！
}
```

### 2. SQL关键字校验

```java
@Component
public class SqlValidator {
    
    private static final List<String> FORBIDDEN_KEYWORDS = Arrays.asList(
        "DROP", "DELETE", "TRUNCATE", "UPDATE", "INSERT",
        "ALTER", "CREATE", "GRANT", "REVOKE", "EXECUTE"
    );
    
    /**
     * 验证SQL是否为SELECT语句且不包含危险关键字
     */
    public void validate(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL不能为空");
        }
        
        String upperSql = sql.trim().toUpperCase();
        
        // 检查是否为SELECT语句
        if (!upperSql.startsWith("SELECT")) {
            throw new SecurityException("仅支持SELECT查询语句");
        }
        
        // 检查是否包含危险关键字
        for (String keyword : FORBIDDEN_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                throw new SecurityException("SQL包含非法关键字: " + keyword);
            }
        }
    }
}
```

### 3. 密码加密

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    public void createUser(String username, String plainPassword) {
        String encodedPassword = passwordEncoder.encode(plainPassword);
        
        User user = User.builder()
            .username(username)
            .password(encodedPassword)
            .build();
            
        userRepository.save(user);
    }
}
```

---

## 性能优化

### 1. 数据库查询优化

```java
// ✅ 使用懒加载避免N+1问题
@Entity
public class Report {
    @ManyToOne(fetch = FetchType.LAZY)  // 懒加载
    @JoinColumn(name = "creator_id")
    private User creator;
}

// ✅ 使用JOIN FETCH优化查询
@Query("SELECT r FROM Report r JOIN FETCH r.creator WHERE r.id = :id")
Optional<Report> findByIdWithCreator(@Param("id") Long id);

// ✅ 使用分页
public Page<Report> listReports(Pageable pageable) {
    return reportRepository.findAll(pageable);
}
```

### 2. 缓存策略

```java
@Service
@CacheConfig(cacheNames = "reports")
public class ReportService {
    
    @Cacheable(key = "#id")
    public Report getReport(Long id) {
        return reportRepository.findById(id)
            .orElseThrow(() -> new ReportNotFoundException(id));
    }
    
    @CacheEvict(key = "#id")
    public void updateReport(Long id, ReportDTO dto) {
        // 更新逻辑
    }
}
```

---

## 日志规范

### 1. 日志级别

```java
@Service
@Slf4j
public class ReportService {
    
    public void executeReport(Long id) {
        log.debug("开始执行报表: {}", id);  // 调试信息
        
        try {
            // 业务逻辑
            log.info("报表执行成功: {}", id);  // 重要信息
        } catch (Exception e) {
            log.error("报表执行失败: {}", id, e);  // 错误信息
        }
    }
}
```

### 2. 日志脱敏

```java
// ✅ 正确：不记录敏感信息
log.info("用户登录: username={}", username);

// ❌ 错误：记录了密码
log.info("用户登录: username={}, password={}", username, password);
```

---

## 依赖管理

### 版本统一管理

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.1.5</spring-boot.version>
    <lombok.version>1.18.30</lombok.version>
    <poi.version>5.2.3</poi.version>
</properties>
```

### 定期更新依赖

```bash
# 检查依赖更新
mvn versions:display-dependency-updates

# 检查安全漏洞
mvn dependency-check:check
```

---

## 代码检查

### 运行检查

```bash
# 代码格式检查
mvn spotless:check

# 代码格式化
mvn spotless:apply

# 静态代码分析
mvn checkstyle:check
```

---

**最后更新**：2026-01-15  
**适用版本**：v1.0-MVP
