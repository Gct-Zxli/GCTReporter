# Git与分支管理规范

> 适用于GCT Reporter项目的Git操作规范

---

## 📋 目录

- [Git提交规范](#git提交规范)
- [分支管理策略](#分支管理策略)
- [分支操作流程](#分支操作流程)
- [Commit最佳实践](#commit最佳实践)
- [冲突解决](#冲突解决)

---

## Git提交规范

### Conventional Commits

我们采用[Conventional Commits](https://www.conventionalcommits.org/)规范。

### 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Type类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(report): 添加Excel导出功能` |
| `fix` | Bug修复 | `fix(auth): 修复登录超时问题` |
| `docs` | 文档更新 | `docs(readme): 更新安装说明` |
| `style` | 代码格式调整<br/>（不影响功能） | `style(user): 格式化代码缩进` |
| `refactor` | 重构代码<br/>（不是新功能也不是修复Bug） | `refactor(service): 重构查询服务` |
| `perf` | 性能优化 | `perf(query): 优化SQL查询性能` |
| `test` | 测试相关 | `test(report): 添加报表单元测试` |
| `build` | 构建系统或外部依赖变更 | `build(maven): 升级Spring Boot到3.2.0` |
| `ci` | CI配置文件和脚本变更 | `ci(github): 添加GitHub Actions工作流` |
| `chore` | 其他不修改src或test的变更 | `chore(deps): 更新依赖包` |
| `revert` | 回退之前的commit | `revert: 回退feat(report)提交` |

#### Scope范围

常用范围（根据功能模块）：

```
auth      - 认证授权
user      - 用户管理
role      - 角色管理
report    - 报表功能
query     - 查询执行
export    - 导出功能
param     - 参数管理
column    - 列配置
ui        - 前端界面
api       - API接口
db        - 数据库
config    - 配置
```

#### Subject主题

- 使用祈使句，现在时（如"添加"而非"已添加"）
- 不要大写首字母
- 结尾不加句号
- 简洁明了（建议<50字符）

#### Body正文（可选）

- 详细说明改动的原因和内容
- 可以分多行
- 使用`-`或`*`列举要点

#### Footer页脚（可选）

- 关闭Issue：`Closes #123`
- 破坏性变更：`BREAKING CHANGE: 描述`
- 关联PR：`Related to #456`

### 提交示例

#### 简单提交

```bash
feat(report): 添加SQL语法高亮功能
```

#### 详细提交

```bash
feat(report): 添加SQL语法高亮功能

- 集成Monaco Editor
- 支持MySQL语法高亮
- 添加自动补全功能
- 添加错误提示

Closes #123
```

#### 修复Bug

```bash
fix(auth): 修复JWT Token过期判断错误

修复了Token过期时间判断逻辑错误，导致用户在Token还有效时
被强制登出的问题。

Closes #456
```

#### 破坏性变更

```bash
feat(api): 重构报表查询API接口

将查询接口从GET改为POST方法，支持复杂参数传递。

BREAKING CHANGE: 
查询接口路径从 GET /api/reports/{id}/query 
改为 POST /api/reports/{id}/execute
客户端需要相应更新调用方式。
```

#### 回退提交

```bash
revert: feat(report): 添加SQL语法高亮功能

This reverts commit abc123def456.

原因：Monaco Editor导致打包体积过大，影响加载性能。
```

---

## 分支管理策略

### 分支类型

```
main                # 主分支，受保护
  ├── develop       # 开发分支
  │   ├── feature/US001-user-login          # 功能分支
  │   ├── feature/US010-report-design       # 功能分支
  │   ├── bugfix/123-fix-query-timeout      # Bug修复分支
  │   └── refactor/optimize-sql-parser      # 重构分支
  ├── release/v1.0.0     # 发布分支
  └── hotfix/v1.0.1-security-patch  # 热修复分支
```

### 分支说明

| 分支类型 | 命名规则 | 来源 | 合并目标 | 说明 |
|---------|---------|------|---------|------|
| `main` | `main` | - | - | 生产环境代码，受保护 |
| `develop` | `develop` | `main` | `main` | 开发主分支 |
| `feature` | `feature/<story-id>-<desc>` | `develop` | `develop` | 新功能开发 |
| `bugfix` | `bugfix/<issue-id>-<desc>` | `develop` | `develop` | Bug修复 |
| `release` | `release/v<version>` | `develop` | `main`+`develop` | 发布准备 |
| `hotfix` | `hotfix/v<version>-<desc>` | `main` | `main`+`develop` | 紧急修复 |

### 分支命名规范

**功能分支**：
```bash
feature/US001-user-login           # ✅ 正确：包含Story ID
feature/US010-report-design
feature/add-export-function        # ✅ 正确：描述性名称

feature/new                        # ❌ 错误：太简单
feature/US001                      # ❌ 错误：缺少描述
```

**Bug修复分支**：
```bash
bugfix/123-fix-login-timeout       # ✅ 正确：包含Issue ID
bugfix/fix-query-error             # ✅ 正确：描述性名称

bugfix/fix                         # ❌ 错误：不明确
```

**发布分支**：
```bash
release/v1.0.0                     # ✅ 正确：语义化版本
release/v1.1.0-beta

release/release1                   # ❌ 错误：不是语义化版本
```

---

## 分支操作流程

### 1. 创建功能分支

```bash
# 1. 切换到develop分支并更新
git checkout develop
git pull origin develop

# 2. 创建新功能分支
git checkout -b feature/US001-user-login

# 3. 查看当前分支
git branch
```

### 2. 开发和提交

```bash
# 1. 查看修改
git status

# 2. 添加文件到暂存区
git add .                          # 添加所有文件
git add src/main/java/...          # 添加指定文件

# 3. 提交
git commit -m "feat(auth): 实现用户登录功能"

# 4. 推送到远程
git push origin feature/US001-user-login
```

### 3. 保持分支更新

```bash
# 方式1: Merge（保留完整历史）
git checkout feature/US001-user-login
git merge develop

# 方式2: Rebase（线性历史，推荐）
git checkout feature/US001-user-login
git rebase develop

# 如果有冲突，解决后继续
git add .
git rebase --continue
```

### 4. 创建Pull Request

1. 推送分支到远程
2. 在GitHub/GitLab上创建PR
3. 填写PR模板
4. 指定审查人员
5. 等待代码审查

### 5. 合并到develop

```bash
# 方式1: GitHub/GitLab界面合并（推荐）
# 点击"Merge Pull Request"

# 方式2: 命令行合并
git checkout develop
git pull origin develop
git merge --no-ff feature/US001-user-login  # 保留分支历史
git push origin develop
```

### 6. 删除已合并分支

```bash
# 删除本地分支
git branch -d feature/US001-user-login

# 删除远程分支
git push origin --delete feature/US001-user-login
```

---

## Commit最佳实践

### 1. 原子性提交

每个commit只做一件事。

```bash
# ✅ 好的做法
git commit -m "feat(auth): 添加用户登录接口"
git commit -m "test(auth): 添加登录接口测试"

# ❌ 不好的做法
git commit -m "添加登录功能、修复Bug、更新文档"
```

### 2. 提交前检查

```bash
# 查看修改内容
git diff

# 查看暂存区内容
git diff --staged

# 确认要提交的文件
git status
```

### 3. 修改最后一次提交

```bash
# 修改提交信息
git commit --amend -m "新的提交信息"

# 添加遗漏的文件到最后一次提交
git add forgotten_file.java
git commit --amend --no-edit
```

### 4. 交互式Rebase整理提交

```bash
# 整理最近3个提交
git rebase -i HEAD~3

# 可以进行的操作：
# pick   - 保留提交
# reword - 修改提交信息
# edit   - 修改提交内容
# squash - 合并到前一个提交
# drop   - 删除提交
```

### 5. 使用Git Hooks

**Pre-commit Hook**（提交前检查）：

```bash
#!/bin/bash
# .git/hooks/pre-commit

# 运行代码格式检查
echo "Running code style check..."

# 后端检查
cd backend
mvn spotless:check
if [ $? -ne 0 ]; then
  echo "❌ 后端代码格式检查失败，请运行: mvn spotless:apply"
  exit 1
fi

# 前端检查
cd ../frontend
npm run lint
if [ $? -ne 0 ]; then
  echo "❌ 前端代码检查失败，请运行: npm run lint:fix"
  exit 1
fi

echo "✅ 代码检查通过"
```

---

## 冲突解决

### 1. 识别冲突

```bash
git merge develop
# 或
git rebase develop

# 输出类似：
# CONFLICT (content): Merge conflict in src/main/java/User.java
```

### 2. 查看冲突文件

```bash
git status

# 输出：
# both modified: src/main/java/User.java
```

### 3. 解决冲突

打开冲突文件，会看到：

```java
<<<<<<< HEAD
private String name;  // 你的修改
=======
private String username;  // develop分支的修改
>>>>>>> develop
```

修改为：

```java
private String username;  // 保留develop的修改
// 或选择你的修改，或合并两者
```

### 4. 标记为已解决

```bash
# 添加已解决的文件
git add src/main/java/User.java

# 如果是merge，提交解决
git commit -m "merge: 解决User.java冲突"

# 如果是rebase，继续
git rebase --continue
```

### 5. 中止操作

```bash
# 中止merge
git merge --abort

# 中止rebase
git rebase --abort
```

---

## 常用Git命令

### 查看历史

```bash
# 查看提交历史
git log

# 单行显示
git log --oneline

# 图形化显示
git log --graph --oneline --all

# 查看某个文件的历史
git log -- path/to/file
```

### 撤销操作

```bash
# 撤销工作区修改
git checkout -- file.java

# 撤销暂存区修改
git reset HEAD file.java

# 回退到上一个提交（保留修改）
git reset --soft HEAD~1

# 回退到上一个提交（不保留修改）
git reset --hard HEAD~1

# 回退到指定提交
git reset --hard abc123
```

### 标签管理

```bash
# 创建标签
git tag v1.0.0

# 创建带注释的标签
git tag -a v1.0.0 -m "Release version 1.0.0"

# 推送标签
git push origin v1.0.0

# 推送所有标签
git push origin --tags

# 删除标签
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0
```

### Stash暂存

```bash
# 暂存当前修改
git stash

# 暂存时添加说明
git stash save "临时保存：正在开发的功能"

# 查看stash列表
git stash list

# 恢复最新的stash
git stash pop

# 恢复指定的stash
git stash apply stash@{0}

# 删除stash
git stash drop stash@{0}
```

---

## Git配置

### 全局配置

```bash
# 配置用户信息
git config --global user.name "张三"
git config --global user.email "zhangsan@example.com"

# 配置默认编辑器
git config --global core.editor "vim"

# 配置命令别名
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.ci commit
git config --global alias.st status
git config --global alias.lg "log --graph --oneline --all"

# 查看配置
git config --list
```

### 项目配置

```bash
# 配置行尾符（Windows开发者）
git config core.autocrlf true

# 配置行尾符（Mac/Linux开发者）
git config core.autocrlf input

# 配置忽略文件权限变更
git config core.filemode false
```

---

## 提交检查清单

提交前请确认：

- [ ] ✅ 代码已通过编译
- [ ] ✅ 代码已格式化
- [ ] ✅ 测试已通过
- [ ] ✅ 提交信息符合规范
- [ ] ✅ 无调试代码（console.log, System.out等）
- [ ] ✅ 无敏感信息（密码、Token等）
- [ ] ✅ 文件已正确添加到.gitignore

---

**最后更新**：2026-01-15  
**适用版本**：v1.0-MVP
