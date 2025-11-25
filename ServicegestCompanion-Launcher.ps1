$appDir = "$env:LOCALAPPDATA\ServicegestCompanion\app"
$javaPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\javaw.exe"

Start-Process -FilePath $javaPath -ArgumentList "--module-path", "$appDir\lib", "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics", "-jar", "$appDir\companion-app-all.jar" -WindowStyle Hidden
