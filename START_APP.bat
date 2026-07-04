@echo off
REM =========================================================
REM Job Portal System - Complete Startup Script
REM =========================================================

setlocal enabledelayedexpansion

echo.
echo ============================================
echo   Job Portal System - Starting Application
echo ============================================
echo.

REM Set Maven and Java paths
set MAVEN_HOME=C:\Apache-Maven
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Try to find Java
for /f "tokens=*" %%A in ('where java 2^>nul') do (
    set JAVA_PATH=%%A
    goto JAVA_FOUND
)

REM If not found, try common locations
if exist "C:\Program Files\Eclipse Adoptium\jdk-21.0.11+10\bin\java.exe" (
    set JAVA_PATH=C:\Program Files\Eclipse Adoptium\jdk-21.0.11+10\bin\java.exe
    goto JAVA_FOUND
)

if exist "C:\Program Files\Eclipse Foundation\jdk-21.0.11+10\bin\java.exe" (
    set JAVA_PATH=C:\Program Files\Eclipse Foundation\jdk-21.0.11+10\bin\java.exe
    goto JAVA_FOUND
)

REM Search in Program Files
for /d %%D in ("C:\Program Files\*") do (
    if exist "%%D\bin\java.exe" (
        set JAVA_PATH=%%D\bin\java.exe
        goto JAVA_FOUND
    )
)

echo [ERROR] Java not found in PATH or standard locations
echo Please ensure Java 17+ is installed and in your PATH
pause
exit /b 1

:JAVA_FOUND
echo [OK] Java found at: %JAVA_PATH%
for /f "tokens=*" %%A in ('"%JAVA_PATH%" -version 2^>^&1 ^| findstr /R "version"') do (
    echo     %%A
)

echo.
echo [CHECKING] Maven installation...
%MAVEN_HOME%\bin\mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven not found or not working
    echo Maven location: %MAVEN_HOME%
    pause
    exit /b 1
)
echo [OK] Maven is installed and working

echo.
echo [SETTING] Environment variables...
set JAVA_HOME=%JAVA_PATH:~0,-8%
set DB_URL=jdbc:postgresql://localhost:5432/job_portal_db
set DB_USERNAME=postgres
set DB_PASSWORD=postgres
set JWT_SECRET=MySecureSecretKeyForJobPortal123456789012345ABCDEF
set JWT_EXPIRATION_MS=86400000
set UPLOAD_DIR=uploads/resumes

echo [OK] Environment variables set

echo.
echo [CHECKING] Database connection...
REM Try to check if PostgreSQL is running (using psql if available)
where psql >nul 2>&1
if errorlevel 1 (
    echo [WARNING] PostgreSQL might not be running
    echo Make sure PostgreSQL is running and database exists
    echo To create database, run: psql -U postgres -c "CREATE DATABASE job_portal_db;"
) else (
    psql -U postgres -d postgres -c "SELECT 1" >nul 2>&1
    if errorlevel 1 (
        echo [WARNING] Cannot connect to PostgreSQL
        echo Make sure PostgreSQL is running and credentials are correct
    ) else (
        echo [OK] PostgreSQL connection successful
    )
)

echo.
echo ============================================
echo   Building Project with Maven...
echo ============================================
echo.

cd /d "c:\Users\Sourav Rajput\Downloads\Online Class\job portal system"

REM Build project
call %MAVEN_HOME%\bin\mvn clean package -DskipTests -q

if errorlevel 1 (
    echo [ERROR] Build failed!
    echo Trying alternate build method...
    call %MAVEN_HOME%\bin\mvn clean package -DskipTests
    if errorlevel 1 (
        pause
        exit /b 1
    )
)

echo.
echo [OK] Build successful!

echo.
echo ============================================
echo   Starting Spring Boot Application
echo ============================================
echo.
echo Access points:
echo   API Base:       http://localhost:8080/api
echo   Swagger UI:     http://localhost:8080/api/swagger-ui.html
echo   Health Check:   http://localhost:8080/api/actuator/health
echo.
echo Press Ctrl+C to stop the application
echo.

REM Run the application
call %MAVEN_HOME%\bin\mvn spring-boot:run

pause
