@echo off
cd /d "%~dp0target"
start javaw --module-path "lib" --add-modules javafx.controls,javafx.fxml -jar companion-app-all.jar
