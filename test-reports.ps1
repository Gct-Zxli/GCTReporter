# 报表管理功能测试脚本

$baseUrl = "http://localhost:8080/api/v1"

# 测试1: 创建报表
Write-Host "=== 测试1: 创建报表 ===" -ForegroundColor Green

$reportData = @{
    name = "测试报表001"
    description = "这是一个测试报表"
    sqlContent = "SELECT * FROM users"
    params = @(
        @{
            paramName = "username"
            paramType = "STRING"
            required = $true
        }
    )
    columns = @(
        @{
            fieldName = "id"
            displayName = "用户ID"
            formatType = "NUMBER"
        },
        @{
            fieldName = "username"
            displayName = "用户名"
            formatType = "TEXT"
        }
    )
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/reports" -Method Post -Body $reportData -ContentType "application/json"
    Write-Host "✓ 报表创建成功" -ForegroundColor Green
    Write-Host "报表ID: $($response.id)" -ForegroundColor Yellow
    $reportId = $response.id
} catch {
    Write-Host "✗ 报表创建失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 测试2: 获取报表列表
Write-Host "`n=== 测试2: 获取报表列表 ===" -ForegroundColor Green

try {
    $reports = Invoke-RestMethod -Uri "$baseUrl/reports" -Method Get
    Write-Host "✓ 获取报表列表成功, 共 $($reports.Count) 个报表" -ForegroundColor Green
} catch {
    Write-Host "✗ 获取报表列表失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试3: 获取报表完整配置
Write-Host "`n=== 测试3: 获取报表完整配置 ===" -ForegroundColor Green

try {
    $fullReport = Invoke-RestMethod -Uri "$baseUrl/reports/$reportId/full" -Method Get
    Write-Host "✓ 获取报表配置成功" -ForegroundColor Green
    Write-Host "报表名称: $($fullReport.name)" -ForegroundColor Yellow
    Write-Host "参数数量: $($fullReport.params.Count)" -ForegroundColor Yellow
    Write-Host "列数量: $($fullReport.columns.Count)" -ForegroundColor Yellow
} catch {
    Write-Host "✗ 获取报表配置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试4: 更新报表
Write-Host "`n=== 测试4: 更新报表 ===" -ForegroundColor Green

$updateData = @{
    name = "测试报表001（已更新）"
    description = "这是更新后的描述"
    sqlContent = "SELECT * FROM users WHERE enabled = 1"
    params = @(
        @{
            paramName = "role"
            paramType = "STRING"
            required = $false
        }
    )
    columns = @(
        @{
            fieldName = "id"
            displayName = "ID"
            formatType = "NUMBER"
        },
        @{
            fieldName = "username"
            displayName = "用户名"
            formatType = "TEXT"
        },
        @{
            fieldName = "role"
            displayName = "角色"
            formatType = "TEXT"
        }
    )
} | ConvertTo-Json -Depth 10

try {
    $updated = Invoke-RestMethod -Uri "$baseUrl/reports/$reportId" -Method Put -Body $updateData -ContentType "application/json"
    Write-Host "✓ 报表更新成功" -ForegroundColor Green
    Write-Host "更新后名称: $($updated.name)" -ForegroundColor Yellow
} catch {
    Write-Host "✗ 报表更新失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试5: 删除报表
Write-Host "`n=== 测试5: 删除报表 ===" -ForegroundColor Green

try {
    Invoke-RestMethod -Uri "$baseUrl/reports/$reportId" -Method Delete
    Write-Host "✓ 报表删除成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 报表删除失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 验证删除
Write-Host "`n=== 验证删除 ===" -ForegroundColor Green

try {
    $reports = Invoke-RestMethod -Uri "$baseUrl/reports" -Method Get
    Write-Host "✓ 当前报表数量: $($reports.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 验证失败" -ForegroundColor Red
}

Write-Host "`n=== 所有测试完成 ===" -ForegroundColor Cyan
