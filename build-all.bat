@echo off
setlocal enabledelayedexpansion

echo ============================================
echo   CompanionApp - Complete Build Script
echo ============================================
echo.
echo This script will:
echo  1. Compile Java sources
echo  2. Create JAR file
echo  3. Build Windows installer
echo.
pause

REM ===== Step 1: Check Prerequisites =====
echo [1/5] Checking prerequisites...
echo.

REM Check Java
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found!
    echo Please install Java 17+ from: https://adoptium.net/
    pause
    exit /b 1
)
echo   ✓ Java found

REM Check Inno Setup
set "ISCC="
where iscc.exe >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set "ISCC=iscc.exe"
    goto :prerequisites_ok
)

if exist "C:\Program Files (x86)\Inno Setup 6\iscc.exe" (
    set "ISCC=C:\Program Files (x86)\Inno Setup 6\iscc.exe"
    goto :prerequisites_ok
)

if exist "C:\Program Files\Inno Setup 6\iscc.exe" (
    set "ISCC=C:\Program Files\Inno Setup 6\iscc.exe"
    goto :prerequisites_ok
)

echo ERROR: Inno Setup not found!
echo Please install Inno Setup 6 from: https://jrsoftware.org/isdl.php
pause
exit /b 1

:prerequisites_ok
echo   ✓ Inno Setup found
echo.

REM ===== Step 2: Create Directories =====
echo [2/5] Creating output directories...
if not exist "out" mkdir out
if not exist "out\classes" mkdir out\classes
if not exist "lib" mkdir lib
echo   ✓ Directories ready
echo.

REM ===== Step 3: Compile Java Sources =====
echo [3/5] Compiling Java sources...
javac -d out\classes --module-path lib --add-modules javafx.controls src\main\java\com\companion\app\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Please check your Java source files for errors.
    pause
    exit /b 1
)
echo   ✓ Compilation successful
echo.

REM ===== Step 4: Create JAR File =====
echo [4/5] Creating JAR file...
cd out\classes
jar --create --file ..\companion-app.jar --main-class com.companion.app.CompanionApp com\companion\app\*.class
cd ..\..

if not exist "out\companion-app.jar" (
    echo.
    echo ERROR: JAR creation failed!
    pause
    exit /b 1
)
echo   ✓ JAR created: out\companion-app.jar
echo.

REM ===== Step 5: Copy Libraries =====
echo Copying libraries...
if exist "lib\*.jar" (
    xcopy /Y /Q "lib\*.jar" "out\" >nul 2>&1
)
echo   ✓ Libraries copied
echo.

REM ===== Step 6: Create Installer =====
echo [5/5] Building Windows installer...
echo.
"%ISCC%" setup.iss

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Installer build failed!
    echo Check the Inno Setup output above for details.
    pause
    exit /b 1
)

echo.
echo ============================================
echo   ✓ BUILD SUCCESSFUL!
echo ============================================
echo.
echo Your installer is ready:
echo   📦 installer-output\CompanionApp-Setup.exe
echo.
echo You can now:
echo   • Test the installer locally
echo   • Distribute to users
echo   • Upload to GitLab releases
echo.
pause
