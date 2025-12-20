@echo off
echo Starting Car Sales System Backend...
echo.

REM Set environment variables
if not defined DB_USERNAME set DB_USERNAME=admin_navicat
if not defined DB_PASSWORD set DB_PASSWORD=BigData@123

echo Database: 124.70.48.79:26000/car_sales_db
echo Username: %DB_USERNAME%
echo.
echo Application will start on http://localhost:8080/api
echo Logs are being written to: logs\car-sales-system.log
echo Press Ctrl+C to stop the application
echo.

REM Run the compiled application directly
java -Dspring.profiles.active=dev -jar target\car-sales-system-1.0.0.jar
