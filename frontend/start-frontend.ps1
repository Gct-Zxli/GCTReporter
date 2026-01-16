# 添加Node.js到PATH
$env:Path = "C:\Program Files\nodejs;" + $env:Path

# 安装依赖（首次运行需要）
if (-not (Test-Path "node_modules")) {
    Write-Host "安装依赖..." -ForegroundColor Green
    npm install
}

# 启动前端开发服务器
Write-Host "启动前端服务..." -ForegroundColor Green
npm run dev
