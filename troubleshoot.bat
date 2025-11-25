@echo off
REM Servicegest Companion App - Troubleshooting Script
REM Use this if the app won't launch

cls
echo.
echo ============================================
echo Servicegest Companion App - Troubleshooting
echo ============================================
echo.

REM Check if running from correct directory
echo Checking installation directory...
echo Current location: %cd%
echo.

REM Check for JAR file
if exist "companion-app-all.jar" (
    echo [OK] companion-app-all.jar found
    for /F "tokens=*" %%A in ('dir /b /s companion-app-all.jar ^| find /c /v ""') do echo Size: %%A bytes
) else (
    echo [ERROR] companion-app-all.jar NOT found!
    echo Please verify installation directory contains the JAR file.
    pause
    exit /b 1
)
echo.

REM Check for Java
echo Checking Java installation...
java -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Java found in PATH
    java -version
) else (
    echo [ERROR] Java NOT found in PATH!
    echo.
    echo Solutions:
    echo 1. Download Java 17 or later from: https://adoptium.net/
    echo 2. Add Java to your system PATH
    echo 3. Or set JAVA_HOME environment variable
    echo.
    pause
    exit /b 1
)
echo.

REM Check for common installation issues
echo Checking common issues...
if exist "%APPDATA%\Servicegest" (
    echo [OK] User appdata folder exists
) else (
    echo [INFO] User appdata folder doesn't exist (may be created on first run)
)
echo.

REM Try to launch the app
echo ============================================
echo Attempting to launch application...
echo ============================================
echo.

cd /d "%~dp0"
start javaw.exe -jar companion-app-all.jar

if %ERRORLEVEL% EQU 0 (
    echo [OK] Application launched successfully!
    echo.
    echo The app should open in a new window.
    echo If it doesn't appear, check the console for errors.
    pause
) else (
    echo [ERROR] Failed to launch application
    echo.
    echo Trying with console output for debugging...
    echo.
    java -jar companion-app-all.jar
)

exit /b 0
