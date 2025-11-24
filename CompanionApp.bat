@echo off
cd /d "%~dp0"
echo Starting Companion App...
start javaw -cp "out\companion-app.jar;lib\*" --module-path lib --add-modules javafx.controls com.companion.app.CompanionApp
