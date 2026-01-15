# GCT Reporter - 后端服务

## 项目信息

- **Group ID**: com.gct
- **Artifact ID**: report-generator
- **Version**: 0.0.1-SNAPSHOT
- **Java Version**: 17
- **Spring Boot Version**: 3.1.5

## 前置要求

- JDK 17 或更高版本
- Maven 3.8+ (可选，项目包含Maven Wrapper)

## 快速开始

### 使用Maven Wrapper（推荐）

Windows:
```bash
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

Linux/Mac:
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

### 使用本地Maven

```bash
mvn clean compile
mvn spring-boot:run
```

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gct/reportgenerator/
│   │   │       └── ReportGeneratorApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
│           └── com/gct/reportgenerator/
│               └── ReportGeneratorApplicationTests.java
├── pom.xml
└── .gitignore
```

## 验收标准

- [x] SpringBoot 3.1.x项目创建成功
- [x] Group: com.gct, Artifact: report-generator
- [x] Java版本17
- [x] pom.xml格式正确
- [ ] `mvn clean compile`编译成功（需要安装Maven或使用Maven Wrapper）

## 访问地址

启动后访问: http://localhost:8080

## 注意事项

- 本项目使用Java 17，请确保已安装正确的JDK版本
- 如果没有安装Maven，建议使用Maven Wrapper（mvnw）
- 首次运行会下载依赖，请确保网络连接正常
