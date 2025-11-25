@echo off
REM Servicegest Companion App - Run Script
REM Launches the API Health Monitor application

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo.
echo ============================================
echo Servicegest Companion App
echo API Health Monitor
echo ============================================
echo.

REM Try to run the JAR with standard Java
echo Launching application...

REM First, try with javaw.exe (no console)
start javaw.exe -jar target\companion-app-all.jar

if %ERRORLEVEL% EQU 0 (
    echo Application launched successfully
    exit /b 0
)

REM If that fails, try with java.exe (with console)
java -jar target\companion-app-all.jar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to launch application
    echo.
    echo Possible issues:
    echo  1. Java 17+ not installed or not in PATH
    echo  2. target\companion-app-all.jar not found
    echo.
    echo Solutions:
    echo  1. Download Java from https://adoptium.net/
    echo  2. Run build-all.bat to rebuild the application
    echo.
    pause
    exit /b 1
)

exit /b 0
