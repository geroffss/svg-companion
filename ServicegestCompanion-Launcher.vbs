Set WshShell = CreateObject("WScript.Shell")
appDir = WshShell.ExpandEnvironmentStrings("%LOCALAPPDATA%") & "\ServicegestCompanion\app"
javaPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\javaw.exe"

command = Chr(34) & javaPath & Chr(34) & " --module-path " & Chr(34) & appDir & "\lib" & Chr(34) & " --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar " & Chr(34) & appDir & "\companion-app-all.jar" & Chr(34)

WshShell.Run command, 0, False
