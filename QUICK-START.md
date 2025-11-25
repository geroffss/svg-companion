# Quick Start Guide - Servicegest Companion App

## What is This?

A Windows application that **monitors if api.servicegest.ro/health returns HTTP 200 status code**.

That's it. Simple. One purpose. Done.

## Installation & Running

### Easiest Way - Use Pre-Built JAR

```powershell
java -jar target\companion-app-all.jar
```

✓ Done! App is running.

### Easiest Way - Use Installer

1. Run `build-all.bat` (or download pre-built installer)
2. Double-click `installer-output\Servicegest-Companion-1.0.0.exe`
3. Follow prompts
4. Launch from Desktop or Start Menu

### From Source

```powershell
# Build
.\build-all.bat

# Run
java -jar target\companion-app-all.jar
```

## What You See

**When API is healthy (HTTP 200):**
```
✓ API is Healthy
Last check: 2025-11-25 13:45:23
Status: 200 OK
```

**When API is down or returns non-200:**
```
✗ API is Down
Last check: 2025-11-25 13:45:30
Status: Connection failed
```

## Features

- ✓ Automatic health checks every 10 seconds
- ✓ Manual "Check Now" button
- ✓ Adjustable check interval (5-60 seconds)
- ✓ Visual status indicator (green = healthy, red = down)
- ✓ Timestamp of last check
- ✓ Minimal, clean interface

## Requirements

- Java 17 or later (download from https://adoptium.net/)
- Windows 7 or later
- Internet connection

## Building

### Prerequisites
- Java 17 JDK
- Maven 3.6+ (optional for advanced builds)
- Inno Setup 6 (optional for installer creation)

### Build Steps

1. **Clean build:**
   ```powershell
   mvn clean package
   ```

2. **Create installer:**
   ```powershell
   "C:\Program Files (x86)\Inno Setup 6\iscc.exe" setup.iss
   ```

3. **Or use one-click script:**
   ```powershell
   .\build-all.bat
   ```

## Configuration

### Monitor Different Endpoint

Edit `src/main/java/com/servicegest/companion/app/HealthChecker.java`:

```java
// Line 10
private static final String API_ENDPOINT = "https://your-api.com/health";
```

Then rebuild with `mvn clean package`

### Change Check Interval

Edit `src/main/java/com/servicegest/companion/app/CompanionApp.java`:

```java
// Line 32
private static final long HEALTH_CHECK_INTERVAL = 10000; // milliseconds
```

Then rebuild with `mvn clean package`

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Java not found" | Download Java 17+ from https://adoptium.net/ |
| "JAR not found" | Run `build-all.bat` first |
| "Application won't start" | Check Java version: `java -version` |
| "API always shows down" | Check internet, verify api.servicegest.ro is accessible |
| "Build fails" | Ensure Maven is installed: https://maven.apache.org/ |

## File Descriptions

| File | Purpose |
|------|---------|
| `companion-app-all.jar` | Standalone app (9.04 MB, includes all dependencies) |
| `setup.iss` | Inno Setup installer script |
| `pom.xml` | Maven build configuration |
| `build-all.bat` | One-click build script |
| `run.bat` | Windows launcher |
| `run-app.ps1` | PowerShell launcher |
| `CompanionApp.java` | Main UI code |
| `HealthChecker.java` | API health check code |
| `README.md` | Full documentation |

## Uninstall

1. Open Windows Control Panel
2. Go to "Programs" → "Uninstall a program"
3. Find "Servicegest Companion App"
4. Click "Uninstall"

Or just delete the JAR file and run script.

## Source Code

**CompanionApp.java**
- Main application class
- JavaFX UI
- Health check scheduling

**HealthChecker.java**
- HTTP GET requests
- Status code checking (200 = healthy)
- Error handling

Total: ~250 lines of code

## How It Works

1. **Startup**: App loads, starts checking API
2. **Check Loop**: Every 10 seconds, sends GET to api.servicegest.ro/health
3. **Verify**: Checks if response status = 200
4. **Display**: Shows ✓ (green) if 200, ✗ (red) otherwise
5. **Update**: Shows timestamp of check
6. **Repeat**: Continue checking...

## Distribution

Share the installer:
```
Servicegest-Companion-1.0.0.exe
```

Users can:
1. Download the .exe
2. Double-click to install
3. Launch from Start Menu
4. Monitoring starts immediately

## Support

For issues:
1. Verify Java is installed: `java -version`
2. Check internet connection
3. Verify api.servicegest.ro is accessible
4. Check application logs

---

**Version**: 1.0.0  
**Last Updated**: November 2025  
**Status**: Ready to Deploy ✓
