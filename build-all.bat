@echo off
REM Servicegest Companion App - Build Script
REM Builds the application and creates Windows installer

setlocal enabledelayedexpansion

echo.
echo ============================================
echo Servicegest Companion App - Build Script
echo ============================================
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if errorlevel 1 (
    echo Error: Maven is not installed or not in PATH
    echo Please download Maven from https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Check if Java is available
where java >nul 2>nul
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please download Java 17+ from https://adoptium.net/
    pause
    exit /b 1
)

echo Step 1: Cleaning previous build...
call mvn clean
if errorlevel 1 goto :build_failed

echo.
echo Step 2: Building JAR file with Maven...
call mvn package -DskipTests
if errorlevel 1 goto :build_failed

echo.
echo Step 3: Creating installer directory...
if not exist "installer-output" mkdir "installer-output"

REM Check if Inno Setup is installed
echo.
echo Step 4: Creating Windows Installer...
set "INNO_SETUP=C:\Program Files (x86)\Inno Setup 6\iscc.exe"
if not exist "!INNO_SETUP!" (
    set "INNO_SETUP=C:\Program Files\Inno Setup 6\iscc.exe"
)

if not exist "!INNO_SETUP!" (
    echo Warning: Inno Setup not found
    echo JAR file has been built successfully in: target\companion-app-all.jar
    echo.
    echo To create an installer, download Inno Setup from: https://jrsoftware.org/isdl.php
    echo Then run: "!INNO_SETUP!" setup.iss
    goto :build_success
) else (
    echo Found Inno Setup at: !INNO_SETUP!
    call "!INNO_SETUP!" setup.iss
    if errorlevel 1 (
        echo Warning: Installer creation failed, but JAR was built successfully
        goto :build_success
    )
)

:build_success
echo.
echo ============================================
echo Build completed successfully!
echo ============================================
echo.
echo Output files:
echo - JAR: target\companion-app-all.jar
if exist "installer-output\*.exe" (
    echo - Installer: installer-output\Servicegest-Companion-*.exe
)
echo.
pause
exit /b 0

:build_failed
echo.
echo ============================================
echo Build failed!
echo ============================================
echo.
pause
exit /b 1
