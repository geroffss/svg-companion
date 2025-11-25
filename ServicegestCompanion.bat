@echo off
REM Servicegest Companion App Launcher
REM Uses bundled Java runtime with JavaFX

cd /d "%~dp0"

REM Use bundled runtime
set JAVA_EXE=%~dp0runtime\bin\javaw.exe

REM Fallback to system Java if bundled not found
if not exist "%JAVA_EXE%" (
    set JAVA_EXE=javaw.exe
)

REM Launch the application with JavaFX module path
start "" "%JAVA_EXE%" --module-path "%~dp0lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar "%~dp0app\companion-app-all.jar"
