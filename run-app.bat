@echo off
REM Start the application in background
cd /d D:\Downloads\DoAnQuanLy-SpringBoot
start java -jar target/hr-management-1.0.0.jar

REM Wait for app to start (5 seconds)
timeout /t 5 /nobreak

REM Open browser
start http://localhost:8080

pause
