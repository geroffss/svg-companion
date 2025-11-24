# Build Scripts

@echo off
echo Building Companion App...
echo.

REM Step 1: Clean and build with Maven
echo [1/3] Building JAR with Maven...
call mvn clean package
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)

REM Step 2: Create launcher directory
echo.
echo [2/3] Creating launcher directory...
if not exist "launcher" mkdir launcher

REM Step 3: Instructions for manual steps
echo.
echo [3/3] Build complete!
echo.
echo JAR file created: target\companion-app-1.0.0.jar
echo.
echo === Next Steps ===
echo.
echo To create Windows installer:
echo.
echo Option 1 - Launch4j + Inno Setup (Recommended):
echo   1. Install Launch4j from: https://launch4j.sourceforge.net/
echo   2. Run: launch4jc launch4j-config.xml
echo   3. Install Inno Setup from: https://jrsoftware.org/isdl.php
echo   4. Run: iscc installer.iss
echo   5. Find installer in: target\installer\
echo.
echo Option 2 - JPackage (Java 14+):
echo   Run: jpackage --input target --name CompanionApp --main-jar companion-app-1.0.0.jar --main-class com.companion.app.CompanionApp --type exe --win-menu --win-shortcut --win-dir-chooser
echo.

pause
