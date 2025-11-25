@echo off
REM Servicegest Companion App - Final Build Script
REM Creates portable package with all dependencies

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo.
echo ============================================
echo Servicegest Companion App - Build Complete
echo ============================================
echo.

REM Create portable package folder
set PORTABLE_DIR=%CD%\portable
set BUILD_DIR=%CD%\target

if exist "%PORTABLE_DIR%" rmdir /s /q "%PORTABLE_DIR%"
mkdir "%PORTABLE_DIR%"

echo Creating portable package...
echo.

REM Copy JAR file
if exist "%BUILD_DIR%\companion-app.jar" (
    copy "%BUILD_DIR%\companion-app.jar" "%PORTABLE_DIR%\companion-app.jar" >nul
    echo [OK] Copied JAR file
) else (
    echo [ERROR] JAR file not found
    exit /b 1
)

REM Copy lib folder with all dependencies
if exist "%BUILD_DIR%\lib" (
    mkdir "%PORTABLE_DIR%\lib"
    xcopy "%BUILD_DIR%\lib\*" "%PORTABLE_DIR%\lib\" /Y /Q >nul
    echo [OK] Copied JavaFX and dependencies
) else (
    echo [ERROR] lib folder not found
    exit /b 1
)

REM Copy launcher script
if exist "%CD%\companion-launcher.bat" (
    copy "%CD%\companion-launcher.bat" "%PORTABLE_DIR%\run.bat" >nul
    echo [OK] Copied launcher script
)

REM Copy run.bat
if exist "%CD%\run.bat" (
    copy "%CD%\run.bat" "%PORTABLE_DIR%\start.bat" >nul
)

echo.
echo ============================================
echo Build Complete!
echo ============================================
echo.

echo Portable Package Location:
echo   %PORTABLE_DIR%\
echo.

echo To Launch:
echo   1. Go to: portable folder
echo   2. Double-click: run.bat
echo.

echo Or run from anywhere:
cd /d "%PORTABLE_DIR%"
start cmd /k run.bat

exit /b 0
