@echo off
REM First-run script to fix shortcut and launch app using BUNDLED Java
setlocal

set INSTALL_DIR=%LOCALAPPDATA%\ServicegestCompanion
set JAVA_PATH=%INSTALL_DIR%\runtime\bin\javaw.exe
set APP_DIR=%INSTALL_DIR%\app

REM Check if shortcut fix is needed
if not exist "%INSTALL_DIR%\.shortcut-fixed" (
    echo Configuring shortcut for first use...
    
    REM Find and fix the Start Menu shortcut using PowerShell
    powershell -ExecutionPolicy Bypass -Command "$shortcut = Get-ChildItem \"$env:APPDATA\Microsoft\Windows\Start Menu\Programs\" -Recurse -Filter 'ServicegestCompanion.lnk' -ErrorAction SilentlyContinue | Select-Object -First 1; if ($shortcut) { $shell = New-Object -ComObject WScript.Shell; $link = $shell.CreateShortcut($shortcut.FullName); $link.TargetPath = '%%LOCALAPPDATA%%\ServicegestCompanion\runtime\bin\javaw.exe'; $link.Arguments = '--module-path \"%%LOCALAPPDATA%%\ServicegestCompanion\app\lib\" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar \"%%LOCALAPPDATA%%\ServicegestCompanion\app\companion-app-all.jar\"'; $link.WorkingDirectory = '%%LOCALAPPDATA%%\ServicegestCompanion\app'; $link.Description = 'Servicegest API Health Monitor'; $link.Save() }"
    
    REM Mark shortcut as fixed
    echo fixed > "%INSTALL_DIR%\.shortcut-fixed"
)

REM Launch the application
cd /d "%APP_DIR%"
start "" "%JAVA_PATH%" --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar companion-app-all.jar
