@echo off
REM Servicegest Companion App Launcher
REM Runs the API Health Monitor with proper JavaFX module path

setlocal enabledelayedexpansion

cd /d "%~dp0"

REM Get paths
set JAR_FILE=%~dp0companion-app.jar
set LIB_DIR=%~dp0lib

REM Check if we're in the right directory
if not exist "%JAR_FILE%" (
    echo Error: companion-app.jar not found
    echo Looking in: %~dp0
    pause
    exit /b 1
)

if not exist "%LIB_DIR%" (
    echo Error: lib folder with JavaFX not found
    echo Looking in: %~dp0
    pause
    exit /b 1
)

REM Launch with module path for JavaFX (required for JavaFX 11+)
start "" javaw.exe -cp "%JAR_FILE%" --module-path "%LIB_DIR%" --add-modules javafx.controls,javafx.fxml com.servicegest.companion.app.CompanionApp

if %ERRORLEVEL% NEQ 0 (
    REM Fallback to java with console
    java -cp "%JAR_FILE%" --module-path "%LIB_DIR%" --add-modules javafx.controls,javafx.fxml com.servicegest.companion.app.CompanionApp
)

exit /b 0
