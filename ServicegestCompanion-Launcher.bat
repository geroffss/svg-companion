@echo off
:: Servicegest Companion Launcher
cd /d "%LOCALAPPDATA%\ServicegestCompanion\app"

:: Try to find Java in common locations
set JAVA_CMD=javaw.exe
if exist "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\javaw.exe" (
    set JAVA_CMD="C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\javaw.exe"
) else if exist "%JAVA_HOME%\bin\javaw.exe" (
    set JAVA_CMD="%JAVA_HOME%\bin\javaw.exe"
)

start "" %JAVA_CMD% --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar companion-app-all.jar
