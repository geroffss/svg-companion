# Post-install script to fix Start Menu shortcut
$javaPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\javaw.exe"
$appDir = "$env:LOCALAPPDATA\ServicegestCompanion\app"

# Find the shortcut
$shortcutPath = Get-ChildItem "$env:APPDATA\Microsoft\Windows\Start Menu\Programs" -Recurse -Filter "ServicegestCompanion.lnk" -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName

if ($shortcutPath) {
    $shell = New-Object -ComObject WScript.Shell
    $link = $shell.CreateShortcut($shortcutPath)
    $link.TargetPath = $javaPath
    $link.Arguments = "--module-path `"$appDir\lib`" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar `"$appDir\companion-app-all.jar`""
    $link.WorkingDirectory = $appDir
    $link.Description = "Servicegest API Health Monitor"
    $link.Save()
    Write-Host "Shortcut fixed successfully"
}
