@echo off
echo Starting Car Sales System Backend...
echo.

REM Load environment variables from .env file if exists
if exist .env (
    echo Loading environment from .env file...
    for /f "usebackq eol=# tokens=1,* delims==" %%a in (.env) do (
        if not "%%a"=="" if not "%%b"=="" set %%a=%%b
    )
) else (
    echo WARNING: .env file not found! Please copy .env.example to .env and configure it.
    echo.
    pause
    exit /b 1
)

echo Database: %DB_HOST%:%DB_PORT%/%DB_NAME%
echo Username: %DB_USERNAME%
echo.
echo Application will start on http://localhost:%SERVER_PORT%
echo Logs are being written to: logs\car-sales-system.log
echo Press Ctrl+C to stop the application
echo.

REM Run the compiled application directly
java -Dspring.profiles.active=dev -jar target\car-sales-system-1.0.0.jar
