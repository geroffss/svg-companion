@echo off
echo ====================================
echo  Quick Run - Companion App
echo ====================================
echo.

REM Check if compiled
if not exist "out\classes\com\companion\app\CompanionApp.class" (
    echo Classes not found. Building first...
    call build-jpackage.bat
    if %ERRORLEVEL% NEQ 0 exit /b 1
)

REM Run the application
echo Running Companion App...
echo.
set CLASSPATH=lib\*;out\classes
java --module-path lib --add-modules javafx.controls com.companion.app.CompanionApp
