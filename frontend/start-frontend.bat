@echo off
echo 启动前端开发服务器...
cd /d %~dp0
set PATH=C:\Program Files\nodejs;%PATH%
call npm run dev
