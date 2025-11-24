; Inno Setup Script for Companion App
; Requires Inno Setup 6.0 or later

#define MyAppName "Companion App"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "Companion"
#define MyAppExeName "CompanionApp.exe"

[Setup]
AppId={{COMPANION-APP-UNIQUE-ID}}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={autopf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
OutputDir=target\installer
OutputBaseFilename=CompanionApp-Setup-{#MyAppVersion}
SetupIconFile=src\main\resources\icon.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=admin
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"
Name: "startupicon"; Description: "Run at Windows startup"; GroupDescription: "Startup Options:"; Flags: checkedonce

[Files]
Source: "target\companion-app-1.0.0.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "launcher\CompanionApp.exe"; DestDir: "{app}"; Flags: ignoreversion
; Note: If using jpackage, the entire runtime folder would be included here

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Registry]
Root: HKCU; Subkey: "Software\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "{#MyAppName}"; ValueData: """{app}\{#MyAppExeName}"""; Flags: uninsdeletevalue; Tasks: startupicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
Type: filesandordirs; Name: "{app}"
