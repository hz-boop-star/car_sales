@echo off
chcp 65001 >nul
echo ========================================
echo  汽车销售系统 - 生产环境启动 (openGauss)
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
    echo [!] 错误: .env 文件不存在
    echo [i] 提示: 复制 .env.prod 为 .env 并配置数据库连接
    echo.
    pause
    exit /b 1
)

echo [i] 环境配置:
echo     Profile: prod (openGauss/PostgreSQL)
echo     数据库: %DB_HOST%:%DB_PORT%/%DB_NAME%
echo     用户: %DB_USERNAME%
echo     端口: %SERVER_PORT%
echo.
echo [*] 启动应用...
echo.

REM Start the application with prod profile
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod -Dfile.encoding=UTF-8
