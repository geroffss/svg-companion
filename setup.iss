; Servicegest Companion App - Inno Setup Installer Script
; Creates a Windows installer for the API Health Monitor

#define MyAppName "Servicegest Companion App"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "Servicegest"
#define MyAppDescription "API Health Monitor - Monitors api.servicegest.ro/health"

[Setup]
AppId={{8F7D9A2E-5B3C-4E1F-9D8A-7C6B5A4E3D2F}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppVerName={#MyAppName} {#MyAppVersion}
AppPublisherURL=https://servicegest.ro
AppSupportURL=https://servicegest.ro
AppUpdatesURL=https://servicegest.ro
DefaultDirName={autopf}\Servicegest\Companion
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
OutputDir=installer-output
OutputBaseFilename=Servicegest-Companion-{#MyAppVersion}
SetupIconFile=
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=lowest
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64
DisableProgramGroupPage=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Create Desktop Icon"; GroupDescription: "Additional Icons"; Flags: unchecked
Name: "autostart"; Description: "Start with Windows"; GroupDescription: "Startup Options"

[Files]
Source: "target\companion-app.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "target\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "run-launcher.bat"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\run-launcher.bat"; WorkingDir: "{app}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\run-launcher.bat"; WorkingDir: "{app}"; Tasks: desktopicon

[Run]
Filename: "{app}\run-launcher.bat"; Description: "Launch {#MyAppName}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: "HKCU"; Subkey: "Software\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "ServicegestCompanion"; ValueData: "{app}\run-launcher.bat"; Tasks: autostart; Flags: uninsdeletevalue

[Code]
procedure CurPageChanged(CurPageID: Integer);
begin
  if CurPageID = wpSelectTasks then
  begin
    MsgBox('Servicegest Companion App will monitor api.servicegest.ro/health endpoint.' + #13 + #13 +
           'Java 17 or later must be installed and available in your PATH.' + #13 + #13 +
           'Download Java from: https://adoptium.net/', mbInformation, MB_OK);
  end;
end;


