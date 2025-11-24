# Companion App - Complete Setup Guide

## ✅ Current Status

The JavaFX application is **fully built and working**!
- ✓ Java sources compiled
- ✓ JAR file created: `out\companion-app.jar`
- ✓ All dependencies downloaded
- ✓ App tested and running

## 🚀 Running the Application

### Option 1: Quick Run (Recommended for Testing)
```powershell
.\run.bat
```

### Option 2: Manual Run
```powershell
java -cp "out\companion-app.jar;lib\*" --module-path lib --add-modules javafx.controls com.companion.app.CompanionApp
```

## 📦 Creating Windows Installer with JPackage

### Prerequisites

JPackage requires **WiX Toolset** to create Windows installers (.exe or .msi).

### Install WiX Toolset:

1. Download WiX 3.14 from: https://github.com/wixtoolset/wix3/releases
2. Install it (typical installation)
3. Add WiX to your PATH:
   ```powershell
   # Add to PATH (adjust version if needed)
   $env:PATH += ";C:\Program Files (x86)\WiX Toolset v3.14\bin"
   # Or set permanently in System Environment Variables
   ```

### Build the Installer:

Once WiX is installed, run:

```powershell
# Create EXE installer
jpackage --input out `
  --name CompanionApp `
  --main-jar companion-app.jar `
  --main-class com.companion.app.CompanionApp `
  --type exe `
  --dest installer-output `
  --app-version 1.0.0 `
  --vendor "Companion" `
  --description "Companion App with auto-start" `
  --win-dir-chooser `
  --win-menu `
  --win-shortcut `
  --win-per-user-install
```

Or create MSI installer:
```powershell
# Create MSI installer
jpackage --input out `
  --name CompanionApp `
  --main-jar companion-app.jar `
  --main-class com.companion.app.CompanionApp `
  --type msi `
  --dest installer-output `
  --app-version 1.0.0 `
  --vendor "Companion" `
  --win-dir-chooser `
  --win-menu `
  --win-shortcut
```

The installer will be created in `installer-output\` folder.

## 🎯 Alternative: Portable Version (No Installer)

If you don't want to install WiX, you can create a portable application:

### Create Launch Script

Create `CompanionApp.bat` in the project root:

```batch
@echo off
cd /d "%~dp0"
start javaw -cp "out\companion-app.jar;lib\*" --module-path lib --add-modules javafx.controls com.companion.app.CompanionApp
```

Then distribute the entire folder with:
- `CompanionApp.bat`
- `out\companion-app.jar`
- `lib\` folder with all JARs

Users can double-click `CompanionApp.bat` to run.

## 🔧 Auto-Start Configuration

The app automatically registers itself to start with Windows on first launch using the Windows Registry:

**Registry Key:** `HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run`

To manually configure:
1. Run the app once
2. It will automatically add itself to Windows startup
3. Check Registry Editor to verify

## 📁 Project Structure

```
companion2/
├── src/main/java/com/companion/app/
│   ├── CompanionApp.java          # Main UI
│   ├── AutoStartManager.java      # Windows auto-start
│   └── BrowserLauncher.java       # Opens browser
├── lib/                           # Dependencies
│   ├── javafx-*.jar
│   ├── jna-5.14.0.jar
│   └── jna-platform-5.14.0.jar
├── out/
│   ├── companion-app.jar          # Built application
│   └── classes/                   # Compiled classes
├── run.bat                        # Quick run script
└── README-SETUP.md               # This file
```

## 🛠️ Troubleshooting

### "Can not find WiX tools"
- Install WiX Toolset from https://wixtoolset.org
- Add WiX bin folder to PATH

### App doesn't start with Windows
- Run the app at least once to register auto-start
- Check Registry Editor for the Run entry
- Ensure antivirus isn't blocking registry writes

### Browser doesn't open
- Make sure localhost:3000 is running
- Check Windows Firewall settings
- Try manually opening http://localhost:3000

## 🎨 Customization

### Change Login Logic
Edit `src\main\java\com\companion\app\CompanionApp.java`:
```java
private boolean authenticate(String username, String password) {
    // Add your auth logic here
    return username.equals("your-user") && password.equals("your-pass");
}
```

### Change Target URL
```java
openBrowserButton.setOnAction(e -> {
    BrowserLauncher.openURL("http://your-url:port");
});
```

Rebuild with:
```powershell
javac -cp "lib\*" --module-path lib --add-modules javafx.controls -d out\classes src\main\java\com\companion\app\*.java
cd out\classes
jar --create --file ..\companion-app.jar --main-class com.companion.app.CompanionApp com\companion\app\*.class
cd ..\..
```

## 📝 Next Steps

1. ✅ App is running - test the login functionality
2. 📦 Install WiX Toolset if you want a proper installer
3. 🎨 Customize authentication and URL as needed
4. 🚀 Build installer or create portable version
5. 📤 Distribute to users

---

**Need help?** Check the troubleshooting section or review the Java source files for implementation details.
