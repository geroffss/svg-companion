@echo off
cd /d "%~dp0"
echo ========================================
echo  Starting Companion App
echo ========================================
echo.
echo If a login window doesn't appear, check for errors below:
echo.

REM Run with visible console to see errors
java -cp "out\companion-app.jar;lib\*" --module-path lib --add-modules javafx.controls,javafx.graphics com.companion.app.CompanionApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Application failed to start
    echo Error code: %ERRORLEVEL%
    echo.
    pause
)
