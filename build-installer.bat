@echo off
echo ========================================
echo  Companion App - Installer Builder
echo ========================================
echo.

REM Check if Inno Setup is installed
where iscc.exe >nul 2>&1
if %ERRORLEVEL% EQU 0 goto :build

REM Check common installation paths
if exist "C:\Program Files (x86)\Inno Setup 6\iscc.exe" (
    set ISCC="C:\Program Files (x86)\Inno Setup 6\iscc.exe"
    goto :build
)

if exist "C:\Program Files\Inno Setup 6\iscc.exe" (
    set ISCC="C:\Program Files\Inno Setup 6\iscc.exe"
    goto :build
)

echo ERROR: Inno Setup not found!
echo.
echo Please install Inno Setup 6:
echo 1. Download from: https://jrsoftware.org/isdl.php
echo 2. Install it (default installation is fine)
echo 3. Run this script again
echo.
echo Or download directly:
powershell -Command "Start-Process 'https://jrsoftware.org/isdl.php'"
pause
exit /b 1

:build
if not defined ISCC set ISCC=iscc.exe

echo Building installer with Inno Setup...
echo.

%ISCC% setup.iss

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo Installer created in: installer-output\
    echo File: CompanionApp-Setup-1.0.0.exe
    echo.
    echo You can now distribute this installer file!
    echo.
) else (
    echo.
    echo ERROR: Build failed!
    echo Check the errors above.
)

pause
