@echo off
REM Servicegest Companion App - Complete Build Script
REM Builds Java app, creates native EXE, and creates Windows installer

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo.
echo ============================================
echo   SERVICEGEST COMPANION - FULL BUILD
echo ============================================
echo.

REM Add Maven to PATH if needed
set "PATH=%PATH%;C:\tools\apache-maven-3.9.6\bin"

REM Step 1: Maven build
echo [1/3] Building Java application with Maven...
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)
echo       Done.
echo.

REM Step 2: Launch4j native EXE
echo [2/3] Creating native EXE with Launch4j...
if exist "launch4j\launch4jc.exe" (
    launch4j\launch4jc.exe launch4j-config.xml
    if not exist "target\ServicegestCompanion.exe" (
        echo ERROR: Launch4j failed to create EXE!
        pause
        exit /b 1
    )
    echo       Done.
) else (
    echo       Skipped - Launch4j not found
)
echo.

REM Step 3: Inno Setup installer
echo [3/3] Building installer with Inno Setup...
set "INNO_SETUP=C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
if exist "!INNO_SETUP!" (
    "!INNO_SETUP!" installer.iss
    if errorlevel 1 (
        echo ERROR: Inno Setup failed!
        pause
        exit /b 1
    )
    echo       Done.
) else (
    echo       Skipped - Inno Setup not found
)

echo.
echo ============================================
echo   BUILD COMPLETE!
echo ============================================
echo.
echo Output files:
echo   - JAR: target\companion-app-all.jar
echo   - EXE: target\ServicegestCompanion.exe
for %%f in (ServicegestCompanion-Setup-*.exe) do echo   - Installer: %%f
echo.
pause
exit /b 0
