@echo off
chcp 65001 >nul
echo Starting Car Sales System Backend (Development Mode)...
echo.

REM Load environment variables from .env file if exists
if exist .env (
    echo Loading environment from .env file...
    for /f "usebackq tokens=1,2 delims==" %%a in (.env) do (
        if not "%%a"=="" if not "%%b"=="" (
            echo %%a | findstr /r "^[A-Z]" >nul
            if not errorlevel 1 set %%a=%%b
        )
    )
) else (
    echo WARNING: .env file not found! Please copy .env.example to .env and configure it.
    echo.
    pause
    exit /b 1
)

echo Database Configuration:
echo   Host: %DB_HOST%:%DB_PORT%
echo   Database: %DB_NAME%
echo   Username: %DB_USERNAME%
echo.

REM Start the application with dev profile
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev -Dfile.encoding=UTF-8
