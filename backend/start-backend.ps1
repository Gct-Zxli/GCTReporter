# 设置Java 17环境
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 显示Java版本
Write-Host "使用Java版本:" -ForegroundColor Green
java -version

# 启动Spring Boot应用
Write-Host "`n启动后端服务..." -ForegroundColor Green
.\mvnw.cmd spring-boot:run
