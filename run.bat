@echo off
REM =========================================================
REM Job Portal System - Auto Setup & Start Script
REM =========================================================

setlocal enabledelayedexpansion
cd /d "%~dp0"

echo.
echo ============================================
echo   Job Portal System - Starting
echo ============================================
echo.

REM Search for Java installation
set JAVA_FOUND=0
set MAVEN_HOME=C:\Apache-Maven

REM Try common installation paths
for %%P in (
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.11+10"
    "C:\Program Files\Eclipse Foundation\jdk-21.0.11+10"
    "C:\Program Files\jdk-21"
    "C:\Program Files (x86)\jdk-21"
) do (
    if exist "%%~P\bin\java.exe" (
        set JAVA_HOME=%%~P
        set JAVA_FOUND=1
        goto JAVA_OK
    )
)

REM Search all program files
for /d %%D in ("C:\Program Files\*") do (
    if exist "%%D\bin\java.exe" (
        set JAVA_HOME=%%D
        set JAVA_FOUND=1
        goto JAVA_OK
    )
)

for /d %%D in ("C:\Program Files (x86)\*") do (
    if exist "%%D\bin\java.exe" (
        set JAVA_HOME=%%D
        set JAVA_FOUND=1
        goto JAVA_OK
    )
)

:JAVA_OK
if !JAVA_FOUND!==0 (
    echo ERROR: Java 21 not found!
    echo Please install Java from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Set paths
set PATH=!JAVA_HOME!\bin;!MAVEN_HOME!\bin;%PATH%
set DB_URL=jdbc:postgresql://localhost:5432/job_portal_db
set DB_USERNAME=postgres
set DB_PASSWORD=postgres
set JWT_SECRET=MySecureSecretKeyForJobPortal123456789012345ABCDEF
set JWT_EXPIRATION_MS=86400000

echo Java version:
"!JAVA_HOME!\bin\java.exe" -version
echo.
echo Maven location: !MAVEN_HOME!
echo.

echo Building project (first run may take 2-5 minutes)...
"!MAVEN_HOME!\bin\mvn.cmd" clean package -DskipTests -q

if errorlevel 1 (
    echo Build failed. Retrying with verbose output...
    "!MAVEN_HOME!\bin\mvn.cmd" clean package -DskipTests
    if errorlevel 1 (
        echo ERROR: Build failed!
        pause
        exit /b 1
    )
)

echo.
echo ============================================
echo   Starting Spring Boot Application
echo ============================================
echo.
echo ACCESS YOUR APPLICATION AT:
echo   • Dashboard: http://localhost:8080/api/swagger-ui.html
echo   • API: http://localhost:8080/api
echo   • Health: http://localhost:8080/api/actuator/health
echo.
echo Database: PostgreSQL (localhost:5432)
echo.
echo Press Ctrl+C to stop
echo.

"!MAVEN_HOME!\bin\mvn.cmd" spring-boot:run

pause
