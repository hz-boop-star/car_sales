@echo off
echo Starting Car Sales System Backend (Development Mode)...
echo.

REM Set environment variables (if not already set)
if not defined DB_USERNAME set DB_USERNAME=admin_navicat
if not defined DB_PASSWORD set DB_PASSWORD=BigData@123

echo Database Configuration:
echo   Host: 124.70.48.79:26000
echo   Database: car_sales_db
echo   Username: %DB_USERNAME%
echo.

REM Start the application with dev profile
mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
