@echo off
setlocal enabledelayedexpansion
echo Starting Car Sales System Backend (Development Mode)...
echo.

REM Load environment variables from .env file if exists
if exist .env (
    echo Loading environment from .env file...
    for /f "usebackq eol=# tokens=1,* delims==" %%a in (".env") do (
        set "%%a=%%b"
    )
)

echo Database Configuration:
echo   Host: %DB_HOST%:%DB_PORT%
echo   Database: %DB_NAME%
echo   Username: %DB_USERNAME%
echo.

REM Start the application with dev profile
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
