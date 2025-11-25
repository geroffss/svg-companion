@echo off
REM Servicegest Companion App - Portable Package Launcher
REM This script launches the app with proper JavaFX module path

setlocal enabledelayedexpansion

cd /d "%~dp0"

REM Get the directory of this script
set APP_HOME=%~dp0
set JAR_FILE=%APP_HOME%companion-app.jar
set LIB_DIR=%APP_HOME%lib

REM Check if JAR exists
if not exist "%JAR_FILE%" (
    echo Error: companion-app.jar not found at %JAR_FILE%
    pause
    exit /b 1
)

REM Check if lib directory exists
if not exist "%LIB_DIR%" (
    echo Error: lib directory not found at %LIB_DIR%
    echo This means JavaFX libraries are missing.
    pause
    exit /b 1
)

echo Launching Servicegest Companion App...

REM Launch with module path for JavaFX (required for JavaFX 11+)
javaw.exe -cp "%JAR_FILE%" --module-path "%LIB_DIR%" --add-modules javafx.controls,javafx.fxml com.servicegest.companion.app.CompanionApp

if %ERRORLEVEL% NEQ 0 (
    REM Try with console if javaw fails
    echo Retrying with console...
    java -cp "%JAR_FILE%" --module-path "%LIB_DIR%" --add-modules javafx.controls,javafx.fxml com.servicegest.companion.app.CompanionApp
)

exit /b %ERRORLEVEL%
