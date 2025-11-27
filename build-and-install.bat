@echo off
REM ========================================
REM  Build + Install Script
REM  Compiles, creates installer, and runs it
REM ========================================

echo ========================================
echo  Servicegest Companion - Full Build
echo ========================================
echo.

cd /d "%~dp0"

REM Find Maven
set "MVN_CMD=mvn"
if exist "C:\tools\apache-maven-3.9.6\bin\mvn.cmd" (
    set "MVN_CMD=C:\tools\apache-maven-3.9.6\bin\mvn.cmd"
)

REM Find Inno Setup
set "ISCC_CMD=iscc.exe"
if exist "C:\Program Files (x86)\Inno Setup 6\ISCC.exe" (
    set "ISCC_CMD=C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
)

REM Step 1: Build with Maven
echo [1/3] Building JAR with Maven...
call "%MVN_CMD%" clean package -q
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)
echo       Done!
echo.

REM Step 2: Build installer
echo [2/3] Creating installer...
"%ISCC_CMD%" installer.iss /Q
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Installer build failed!
    pause
    exit /b 1
)
echo       Done!
echo.

echo ========================================
echo  Build complete!
echo ========================================
echo.
echo  Installer: ServicegestCompanion-Setup-1.2.3.exe
echo.
echo  To install, run the installer manually or
echo  upload to GitHub for auto-update.
echo ========================================
