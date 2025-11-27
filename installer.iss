; Inno Setup Script for Servicegest Companion
; Creates proper installer with working Start Menu shortcut

#define MyAppName "Servicegest - Companion"
#define MyAppVersion "1.2.3"
#define MyAppPublisher "Servicegest"
#define MyAppURL "https://servicegest.ro"

[Setup]
AppId={{A1B2C3D4-E5F6-7890-ABCD-EF1234567890}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
DefaultDirName={localappdata}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
OutputDir=.
OutputBaseFilename=ServicegestCompanion-Setup-{#MyAppVersion}
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
DisableProgramGroupPage=yes
WizardStyle=modern
; Application icon
SetupIconFile=icon.ico
UninstallDisplayIcon={app}\icon.ico

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Create a desktop shortcut"; GroupDescription: "Additional icons:"
Name: "startupicon"; Description: "Run at Windows startup"; GroupDescription: "Startup Options:"

[Files]
; Batch launcher
Source: "ServicegestCompanion.bat"; DestDir: "{app}"; Flags: ignoreversion

; Version info for auto-updates
Source: "version.json"; DestDir: "{app}"; Flags: ignoreversion

; Application JAR and libs
Source: "target\companion-app-all.jar"; DestDir: "{app}\app"; Flags: ignoreversion
Source: "target\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs

; Application icon
Source: "icon.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "src\main\resources\icon.png"; DestDir: "{app}\app"; Flags: ignoreversion

; Bundled Java Runtime
Source: "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\*"; DestDir: "{app}\runtime"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
; Start Menu shortcut
Name: "{group}\{#MyAppName}"; Filename: "{app}\ServicegestCompanion.bat"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"; Comment: "Servicegest API Health Monitor"

; Desktop shortcut (optional)
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\ServicegestCompanion.bat"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"; Comment: "Servicegest API Health Monitor"; Tasks: desktopicon

[Registry]
Root: HKCU; Subkey: "Software\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "{#MyAppName}"; ValueData: """{app}\ServicegestCompanion.bat"""; Flags: uninsdeletevalue; Tasks: startupicon

[Run]
; Launch after install
Filename: "{app}\ServicegestCompanion.bat"; WorkingDir: "{app}"; Description: "Launch ServicegestCompanion"; Flags: nowait postinstall skipifsilent shellexec

[UninstallDelete]
Type: filesandordirs; Name: "{app}"
