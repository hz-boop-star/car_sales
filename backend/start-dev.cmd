@echo off
chcp 65001 >nul
echo ========================================
echo  汽车销售系统 - 开发环境启动 (SQLite)
echo ========================================
echo.

REM Load environment variables from .env file if exists
if exist .env (
    echo [√] 加载环境变量 .env
    for /f "usebackq tokens=1,2 delims==" %%a in (.env) do (
        if not "%%a"=="" if not "%%b"=="" (
            echo %%a | findstr /r "^[A-Z]" >nul
            if not errorlevel 1 set %%a=%%b
        )
    )
) else (
    echo [!] 警告: .env 文件不存在，使用默认配置
    echo [i] 提示: 复制 .env.example 为 .env 可自定义配置
    echo.
)

echo [i] 环境配置:
echo     Profile: dev (SQLite)
echo     数据库: %DB_FILE%
echo     端口: %SERVER_PORT%
echo.
echo [*] 启动应用...
echo.

REM Start the application with dev profile
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev -Dfile.encoding=UTF-8
