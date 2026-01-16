# US005用户管理CRUD功能测试脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "US005 用户管理CRUD功能测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080"
$testResults = @()

# 测试1: 登录
Write-Host "[测试 1/6] 登录功能..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "admin"
        password = "admin123"
    } | ConvertTo-Json
    
    $loginResp = Invoke-WebRequest `
        -Uri "$baseUrl/api/v1/auth/login" `
        -Method POST `
        -Body $loginBody `
        -ContentType "application/json; charset=utf-8" `
        -SessionVariable session `
        -ErrorAction Stop
    
    if ($loginResp.StatusCode -eq 200) {
        Write-Host "  ✓ 登录成功" -ForegroundColor Green
        $testResults += "✓ 登录功能"
    }
} catch {
    Write-Host "  ✗ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
    $testResults += "✗ 登录功能"
    exit 1
}

Start-Sleep -Seconds 1

# 测试2: 获取用户列表
Write-Host "[测试 2/6] 获取用户列表..." -ForegroundColor Yellow
try {
    $usersResp = Invoke-WebRequest `
        -Uri "$baseUrl/api/v1/users" `
        -Method GET `
        -WebSession $session `
        -ErrorAction Stop
    
    $users = ($usersResp.Content | ConvertFrom-Json).data
    Write-Host "  ✓ 获取用户列表成功，共 $($users.Count) 个用户" -ForegroundColor Green
    $testResults += "✓ 用户列表展示"
} catch {
    Write-Host "  ✗ 获取用户列表失败: $($_.Exception.Message)" -ForegroundColor Red
    $testResults += "✗ 用户列表展示"
}

Start-Sleep -Seconds 1

# 测试3: 创建新用户
Write-Host "[测试 3/6] 创建新用户..." -ForegroundColor Yellow
try {
    $createBody = @{
        username = "testuser_$(Get-Date -Format 'HHmmss')"
        password = "test123"
        role = "VIEWER"
        enabled = $true
    } | ConvertTo-Json
    
    $createResp = Invoke-WebRequest `
        -Uri "$baseUrl/api/v1/users" `
        -Method POST `
        -Body $createBody `
        -ContentType "application/json; charset=utf-8" `
        -WebSession $session `
        -ErrorAction Stop
    
    $newUser = ($createResp.Content | ConvertFrom-Json).data
    $script:testUserId = $newUser.id
    Write-Host "  ✓ 创建用户成功，ID: $($newUser.id)" -ForegroundColor Green
    $testResults += "✓ 创建用户"
} catch {
    Write-Host "  ✗ 创建用户失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $errorBody = $reader.ReadToEnd()
        Write-Host "  错误详情: $errorBody" -ForegroundColor Red
    }
    $testResults += "✗ 创建用户"
}

Start-Sleep -Seconds 1

# 测试4: 编辑用户
Write-Host "[测试 4/6] 编辑用户信息..." -ForegroundColor Yellow
if ($script:testUserId) {
    try {
        $updateBody = @{
            role = "DESIGNER"
            enabled = $false
        } | ConvertTo-Json
        
        $updateResp = Invoke-WebRequest `
            -Uri "$baseUrl/api/v1/users/$($script:testUserId)" `
            -Method PUT `
            -Body $updateBody `
            -ContentType "application/json; charset=utf-8" `
            -WebSession $session `
            -ErrorAction Stop
        
        Write-Host "  ✓ 更新用户成功" -ForegroundColor Green
        $testResults += "✓ 编辑用户"
    } catch {
        Write-Host "  ✗ 更新用户失败: $($_.Exception.Message)" -ForegroundColor Red
        $testResults += "✗ 编辑用户"
    }
} else {
    Write-Host "  ⊘ 跳过（创建用户失败）" -ForegroundColor Gray
    $testResults += "⊘ 编辑用户"
}

Start-Sleep -Seconds 1

# 测试5: 启用/禁用用户
Write-Host "[测试 5/6] 启用/禁用用户..." -ForegroundColor Yellow
if ($script:testUserId) {
    try {
        $toggleBody = @{
            enabled = $true
        } | ConvertTo-Json
        
        $toggleResp = Invoke-WebRequest `
            -Uri "$baseUrl/api/v1/users/$($script:testUserId)" `
            -Method PUT `
            -Body $toggleBody `
            -ContentType "application/json; charset=utf-8" `
            -WebSession $session `
            -ErrorAction Stop
        
        Write-Host "  ✓ 切换用户状态成功" -ForegroundColor Green
        $testResults += "✓ 启用/禁用用户"
    } catch {
        Write-Host "  ✗ 切换用户状态失败: $($_.Exception.Message)" -ForegroundColor Red
        $testResults += "✗ 启用/禁用用户"
    }
} else {
    Write-Host "  ⊘ 跳过（创建用户失败）" -ForegroundColor Gray
    $testResults += "⊘ 启用/禁用用户"
}

Start-Sleep -Seconds 1

# 测试6: 删除用户
Write-Host "[测试 6/6] 删除用户..." -ForegroundColor Yellow
if ($script:testUserId) {
    try {
        $deleteResp = Invoke-WebRequest `
            -Uri "$baseUrl/api/v1/users/$($script:testUserId)" `
            -Method DELETE `
            -WebSession $session `
            -ErrorAction Stop
        
        Write-Host "  ✓ 删除用户成功" -ForegroundColor Green
        $testResults += "✓ 删除用户"
    } catch {
        Write-Host "  ✗ 删除用户失败: $($_.Exception.Message)" -ForegroundColor Red
        $testResults += "✗ 删除用户"
    }
} else {
    Write-Host "  ⊘ 跳过（创建用户失败）" -ForegroundColor Gray
    $testResults += "⊘ 删除用户"
}

# 输出测试结果摘要
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试结果摘要" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
foreach ($result in $testResults) {
    if ($result -like "✓*") {
        Write-Host $result -ForegroundColor Green
    } elseif ($result -like "✗*") {
        Write-Host $result -ForegroundColor Red
    } else {
        Write-Host $result -ForegroundColor Gray
    }
}
Write-Host ""
