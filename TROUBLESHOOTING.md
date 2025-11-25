# Servicegest Companion App - Installation Troubleshooting

## Problem: App Won't Launch After Installation

### Reason
The most common issue is that **Java is not available in the system PATH**, or the installer ran with insufficient permissions.

## Solutions (Try in Order)

### Solution 1: Verify Java is Installed (Quick Check)

1. Press `Windows Key + R`
2. Type: `cmd`
3. Type: `java -version`

**If you see a version number**, Java is installed ✓

**If you see "not recognized"**, Java is NOT in PATH ✗

### Solution 2: Install Java

1. Download Java 17 or later from: **https://adoptium.net/**
2. Run the installer
3. Accept default settings
4. **Important**: When prompted, choose "Add to PATH" or "Set JAVA_HOME"
5. Restart your computer
6. Try launching the app again

### Solution 3: Manual Launch from Command Line

1. Open Command Prompt:
   - Press `Windows Key + R`
   - Type: `cmd`
   - Press Enter

2. Navigate to installation folder:
   ```
   cd "C:\Program Files\Servicegest\Companion"
   ```

3. Launch the app:
   ```
   java -jar companion-app-all.jar
   ```

If this works, Java is correctly installed. The installer shortcut may need adjustment.

### Solution 4: Run the Troubleshooting Script

If you have the source files:

1. Extract or navigate to the companion2 folder
2. Double-click: `troubleshoot.bat`
3. This will:
   - Verify Java is installed
   - Check the JAR file exists
   - Attempt to launch the app
   - Show detailed error messages if there are issues

### Solution 5: Run with Console Output

1. In installation folder, create a new file: `launch-debug.bat`
2. Paste:
   ```batch
   @echo off
   cd /d "%~dp0"
   java -jar companion-app-all.jar
   pause
   ```
3. Save and double-click to run
4. This will show any error messages

## Common Error Messages

### "java: command not found"
- Java is not in your system PATH
- Solution: Reinstall Java and select "Add to PATH"

### "companion-app-all.jar not found"
- Wrong installation directory or corrupted installation
- Solution: Uninstall and reinstall the app

### "Exception in Application start()"
- JavaFX rendering issue
- Solution: Update Java to latest version, try running with console to see full error

### Application launches but shows blank window
- Rendering issue
- Solution: 
  1. Close the app
  2. Right-click desktop → Properties → Display settings
  3. Check if scaling is at 100% (or try different scale)
  4. Try running again

## Alternative: Portable Version

If the installer doesn't work, you can use the portable JAR directly:

1. Download: `companion-app-all.jar` (9.04 MB)
2. Create a shortcut to:
   ```
   java -jar companion-app-all.jar
   ```
3. Place the JAR file in an easy location like Desktop or Documents
4. Double-click the shortcut to launch

## Still Not Working?

If you've tried all solutions above:

1. **Verify Java installation:**
   - Open Command Prompt
   - Type: `java -version`
   - You should see: "openjdk version "17+" or "21+"

2. **Check system requirements:**
   - Windows 7 or later
   - 512 MB RAM minimum
   - Internet connection (for API checks)

3. **Try portable version:**
   - Use `companion-app-all.jar` directly
   - Run: `java -jar companion-app-all.jar`

4. **Check Windows registry:**
   - Press `Windows Key + R`
   - Type: `regedit`
   - Navigate to: `HKEY_LOCAL_MACHINE\Software\JavaSoft`
   - Verify Java is registered

## For Developers

If debugging, run with more verbose output:

```batch
java -Djavafx.verbose=true -jar companion-app-all.jar
```

Or capture full error log:

```batch
java -jar companion-app-all.jar > error.log 2>&1
```

Then check `error.log` file for details.

---

**Version**: 1.0.0  
**Last Updated**: November 2025

### Need More Help?

1. Check `README.md` for full documentation
2. Try manual Java command as shown in Solution 3
3. Use portable JAR version instead of installer
