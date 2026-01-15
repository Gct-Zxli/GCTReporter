# GitHub版本管理规范

> GCT Reporter项目分支保护与协作流程

---

## 🔒 分支保护规则

### Main分支保护

**严格禁止**直接在`main`分支提交代码，所有代码必须通过Pull Request合并。

---

## 📋 标准工作流程

### 1️⃣ 创建功能分支

```bash
# 1. 更新develop分支
git checkout develop
git pull origin develop

# 2. 从develop创建功能分支
git checkout -b feature/US001-user-login

# 分支命名规范：
# feature/<story-id>-<简短描述>    # 新功能
# bugfix/<issue-id>-<简短描述>     # Bug修复
# hotfix/<版本>-<简短描述>         # 紧急修复
```

### 2️⃣ 开发与提交

```bash
# 1. 进行开发工作
# ...

# 2. 提交代码（遵循Conventional Commits规范）
git add .
git commit -m "feat(auth): 实现用户登录功能"

# 3. 推送到远程分支
git push origin feature/US001-user-login
```

### 3️⃣ 创建Pull Request

**在GitHub网页操作：**

1. 访问 https://github.com/Gct-Zxli/GCTReporter
2. 点击 "Pull requests" → "New pull request"
3. 选择分支：
   - **Base**: `develop` （目标分支）
   - **Compare**: `feature/US001-user-login` （你的分支）
4. 填写PR信息：
   - 标题：`[US001] 实现用户登录功能`
   - 使用PR模板填写详细信息
5. 指定审查人员（Reviewers）
6. 点击 "Create pull request"

### 4️⃣ 代码审查

- 至少1人审查通过
- 解决所有审查意见
- CI检查全部通过

### 5️⃣ 合并PR

**由审查人员或项目管理员操作：**

1. 审查通过后，点击 "Merge pull request"
2. 选择合并方式：
   - **Squash and merge**（推荐）：合并为一个提交
   - **Merge commit**：保留所有提交历史
3. 确认合并
4. 删除功能分支（GitHub会提示）

### 6️⃣ 更新本地仓库

```bash
# 1. 切换到develop分支
git checkout develop

# 2. 拉取最新代码
git pull origin develop

# 3. 删除本地功能分支
git branch -d feature/US001-user-login
```

---

## ⚙️ GitHub分支保护设置

### 配置步骤

1. 访问仓库 Settings → Branches
2. 点击 "Add branch protection rule"
3. 配置 `main` 分支保护：

**必选项**：
- ✅ Branch name pattern: `main`
- ✅ **Require a pull request before merging**
  - ✅ Require approvals: `1`（至少1人审查）
  - ✅ Dismiss stale pull request approvals when new commits are pushed
- ✅ **Require status checks to pass before merging**
  - ✅ Require branches to be up to date before merging
- ✅ **Do not allow bypassing the above settings**

**可选项**：
- ☑️ Require conversation resolution before merging
- ☑️ Require signed commits
- ☑️ Require linear history

4. 点击 "Create" 保存

### 配置 `develop` 分支保护（可选）

重复以上步骤，配置`develop`分支：
- ✅ Require a pull request before merging
- ✅ Require approvals: `1`

---

## 🚫 禁止操作

### ❌ 直接推送到main

```bash
# ❌ 错误：直接在main分支提交
git checkout main
git add .
git commit -m "直接提交"
git push origin main  # 这将被拒绝！
```

**错误提示**：
```
remote: error: GH006: Protected branch update failed
remote: error: Cannot push to main branch
```

### ❌ 强制推送

```bash
# ❌ 错误：强制推送会破坏历史
git push -f origin main
```

---

## ✅ 正确操作示例

### 场景1：新增功能

```bash
# 1. 从develop创建分支
git checkout develop
git pull origin develop
git checkout -b feature/US010-report-export

# 2. 开发并提交
git add .
git commit -m "feat(export): 添加Excel导出功能"
git push origin feature/US010-report-export

# 3. 在GitHub创建PR: feature/US010-report-export → develop
# 4. 等待审查和合并
```

### 场景2：修复Bug

```bash
# 1. 从develop创建修复分支
git checkout develop
git pull origin develop
git checkout -b bugfix/123-fix-login-error

# 2. 修复并提交
git add .
git commit -m "fix(auth): 修复登录超时问题

修复了JWT Token验证逻辑错误导致的登录超时问题。

Closes #123"
git push origin bugfix/123-fix-login-error

# 3. 在GitHub创建PR并关联Issue
# 4. 审查通过后合并
```

### 场景3：紧急修复（Hotfix）

```bash
# 1. 从main创建hotfix分支
git checkout main
git pull origin main
git checkout -b hotfix/v1.0.1-security-patch

# 2. 修复并提交
git add .
git commit -m "fix(security): 修复SQL注入漏洞"
git push origin hotfix/v1.0.1-security-patch

# 3. 创建PR到main（紧急修复）
# 4. 审查通过后合并到main
# 5. 同时合并到develop
git checkout develop
git merge hotfix/v1.0.1-security-patch
git push origin develop
```

---

## 📊 分支策略图

```
main (受保护)
  ├── develop (受保护)
  │   ├── feature/US001-user-login
  │   ├── feature/US002-report-design
  │   ├── bugfix/123-fix-error
  │   └── ...
  └── hotfix/v1.0.1-security-patch
```

**合并路径**：
- Feature/Bugfix → **PR** → `develop` → **PR** → `main`
- Hotfix → **PR** → `main` → Merge → `develop`

---

## 🔍 PR审查检查清单

提交PR前确认：

- [ ] ✅ 从最新的develop分支创建
- [ ] ✅ 分支命名符合规范
- [ ] ✅ 提交信息符合Conventional Commits
- [ ] ✅ 代码已格式化
- [ ] ✅ 所有测试通过
- [ ] ✅ 无编译警告
- [ ] ✅ PR模板已完整填写
- [ ] ✅ 已指定审查人员

---

## 🆘 常见问题

### Q1: 如果误操作推送到main怎么办？

**A**: 分支保护生效后会自动拒绝，不会成功。如果保护前推送了，需要：
```bash
# 联系管理员回滚
git revert <commit-hash>
```

### Q2: PR冲突怎么解决？

**A**: 
```bash
# 1. 更新本地develop
git checkout develop
git pull origin develop

# 2. 切回功能分支并rebase
git checkout feature/US001-user-login
git rebase develop

# 3. 解决冲突
# ... 手动解决冲突 ...
git add .
git rebase --continue

# 4. 强制推送更新PR
git push -f origin feature/US001-user-login
```

### Q3: 可以跳过PR直接合并吗？

**A**: **不可以！** 这是强制规则，即使是管理员也应遵守。紧急情况可以：
- 创建PR后立即自己审查并合并
- 但仍需通过PR流程

---

## 📖 相关文档

- [Git提交规范](../.github/guidelines/GIT_GUIDELINES.md)
- [代码审查规范](../.github/guidelines/CODE_REVIEW.md)
- [PR模板](../.github/PULL_REQUEST_TEMPLATE.md)

---

**最后更新**: 2026-01-15  
**适用版本**: v1.0
