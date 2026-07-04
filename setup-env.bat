@echo off
REM =========================================================
REM Job Portal System - Windows Setup Helper
REM =========================================================
REM This script helps set up environment variables for the Java project

setlocal enabledelayedexpansion

echo.
echo ============================================
echo   Job Portal System - Setup Helper
echo ============================================
echo.

REM Check Administrator privileges
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo ERROR: This script must be run as Administrator!
    echo Please right-click and select "Run as administrator"
    pause
    exit /b 1
)

echo Step 1: Checking prerequisites...
echo.

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [X] Java NOT found
    echo Please install Java 17+ from https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
) else (
    echo [OK] Java is installed
    for /f "tokens=2" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do set JAVA_VER=%%i
    echo     Version: !JAVA_VER!
)

REM Check Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [X] Maven NOT found
    echo Please install Maven from https://maven.apache.org/download.cgi
    echo And add it to your PATH environment variable
    pause
    exit /b 1
) else (
    echo [OK] Maven is installed
    for /f "tokens=3" %%i in ('mvn -version 2^>^&1 ^| findstr /R "Apache Maven"') do set MAVEN_VER=%%i
    echo     Version: !MAVEN_VER!
)

REM Check PostgreSQL
where psql >nul 2>&1
if errorlevel 1 (
    echo [X] PostgreSQL NOT found (optional but recommended)
    echo Please install PostgreSQL from https://www.postgresql.org/download/
) else (
    echo [OK] PostgreSQL is installed
)

echo.
echo Step 2: Setting Environment Variables...
echo.

REM Set environment variables
setx DB_URL "jdbc:postgresql://localhost:5432/job_portal_db" >nul
echo [OK] DB_URL set to: jdbc:postgresql://localhost:5432/job_portal_db

setx DB_USERNAME "postgres" >nul
echo [OK] DB_USERNAME set to: postgres

setx DB_PASSWORD "postgres" >nul
echo [OK] DB_PASSWORD set to: postgres

REM Generate JWT Secret
echo.
echo Step 3: Generating JWT Secret...
echo.
echo Please provide a JWT secret (or press Enter to generate one)
echo You can generate one with OpenSSL: openssl rand -base64 32
echo Or just use: MySecretKeyForJobPortal2024IsSuperSecure1234567890ABC
echo.

set /p JWT_SECRET="Enter JWT_SECRET: "

if "!JWT_SECRET!"=="" (
    set JWT_SECRET=MySecretKeyForJobPortal2024IsSuperSecure1234567890ABC
    echo Using default: !JWT_SECRET!
)

setx JWT_SECRET "!JWT_SECRET!" >nul
echo [OK] JWT_SECRET has been set

setx JWT_EXPIRATION_MS "86400000" >nul
echo [OK] JWT_EXPIRATION_MS set to: 86400000 (24 hours)

setx UPLOAD_DIR "uploads/resumes" >nul
echo [OK] UPLOAD_DIR set to: uploads/resumes

echo.
echo ============================================
echo   Setup Complete!
echo ============================================
echo.
echo Environment Variables Set:
echo   - DB_URL: jdbc:postgresql://localhost:5432/job_portal_db
echo   - DB_USERNAME: postgres
echo   - DB_PASSWORD: postgres
echo   - JWT_SECRET: (set securely)
echo   - JWT_EXPIRATION_MS: 86400000
echo   - UPLOAD_DIR: uploads/resumes
echo.
echo Next Steps:
echo   1. Create PostgreSQL database:
echo      - Open pgAdmin or psql
echo      - Run: CREATE DATABASE job_portal_db;
echo.
echo   2. Run the project:
echo      - Open Command Prompt in the project directory
echo      - Run: mvn spring-boot:run
echo.
echo   3. Access the application:
echo      - API Docs: http://localhost:8080/api/swagger-ui.html
echo      - Health: http://localhost:8080/api/actuator/health
echo.
echo Note: You may need to RESTART your Command Prompt for environment
echo variables to take effect.
echo.
pause
