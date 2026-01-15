# 快速参考：分支保护工作流

## 🔄 日常开发流程（5步）

```bash
# 1️⃣ 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/US001-user-login

# 2️⃣ 开发并提交
git add .
git commit -m "feat(auth): 实现用户登录功能"

# 3️⃣ 推送分支
git push origin feature/US001-user-login

# 4️⃣ 在GitHub创建PR
# 访问: https://github.com/Gct-Zxli/GCTReporter/pulls
# 点击 "New pull request"
# Base: develop ← Compare: feature/US001-user-login

# 5️⃣ PR合并后更新本地
git checkout develop
git pull origin develop
git branch -d feature/US001-user-login
```

## ⚙️ GitHub分支保护配置

**Settings → Branches → Add rule**

### Main分支保护
```
Branch name pattern: main

✅ Require a pull request before merging
  ✅ Require approvals: 1
✅ Require status checks to pass
✅ Do not allow bypassing
```

### Develop分支保护（可选）
```
Branch name pattern: develop

✅ Require a pull request before merging
  ✅ Require approvals: 1
```

## 🚫 禁止操作

```bash
# ❌ 直接推送到main（会被拒绝）
git push origin main

# ❌ 强制推送
git push -f origin main
```

## ✅ 分支命名规范

```
feature/US001-user-login       # 新功能
bugfix/123-fix-timeout         # Bug修复  
hotfix/v1.0.1-security         # 紧急修复
refactor/optimize-query        # 重构
```

## 📝 提交信息规范

```bash
feat(scope): 简短描述          # 新功能
fix(scope): 简短描述           # Bug修复
docs(scope): 简短描述          # 文档更新
test(scope): 简短描述          # 测试
refactor(scope): 简短描述      # 重构
```

## 🔗 快速链接

- 仓库: https://github.com/Gct-Zxli/GCTReporter
- PR列表: https://github.com/Gct-Zxli/GCTReporter/pulls
- 详细规范: [BRANCH_PROTECTION.md](BRANCH_PROTECTION.md)
