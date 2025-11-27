# Servicegest Companion App

A Windows desktop application that monitors the Servicegest API health status with system tray integration and automatic updates.

## Features

- **API Health Monitoring** - Monitors `api.servicegest.ro/health` endpoint
- **System Tray Integration** - Runs minimized in system tray with status indicator
- **Auto-Update** - Automatically checks for and installs updates from GitHub
- **USB Device Detection** - Detects connected USB devices
- **Windows Installer** - Easy installation with bundled Java runtime

## Installation

### For Users

1. Download the latest installer from [GitHub Releases](https://github.com/geroffss/svg-companion/releases)
2. Run `ServicegestCompanion-Setup-x.x.x.exe`
3. Follow the installation prompts
4. The app will start automatically after installation

**Note:** The installer includes a bundled Java runtime - no need to install Java separately.

### Options during installation

- **Desktop shortcut** - Creates a shortcut on your desktop
- **Run at Windows startup** - Automatically starts the app when Windows boots

## Building from Source

### Prerequisites

- Java 17+ JDK (e.g., [Eclipse Adoptium](https://adoptium.net/))
- Maven 3.6+
- [Inno Setup 6](https://jrsoftware.org/isdl.php) (for creating installer)

### Build Steps

```powershell
# Clone the repository
git clone https://github.com/geroffss/svg-companion.git
cd svg-companion

# Build and create installer
.\build-and-install.bat
```

Or manually:

```powershell
# Build JAR
mvn clean package -DskipTests

# Create installer (requires Inno Setup)
"C:\Program Files (x86)\Inno Setup 6\ISCC.exe" installer.iss
```

The installer will be created as `ServicegestCompanion-Setup-x.x.x.exe`

## Development

```powershell
# Run directly with Maven (for development)
mvn clean compile javafx:run
```

## Releasing a New Version

1. Update version in `version.json`
2. Update version in `installer.iss` (`#define MyAppVersion`)
3. Run `build-and-install.bat`
4. Create a new GitHub release with tag `vX.X.X`
5. Upload the installer `.exe` file to the release

Users with older versions will automatically be prompted to update.

## Project Structure

```
companion2/
├── src/main/java/com/servicegest/companion/app/
│   ├── CompanionApp.java      # Main application
│   ├── HealthChecker.java     # API health checking
│   ├── GitHubReleaseChecker.java  # Auto-update logic
│   ├── UpdateWindow.java      # Update progress UI
│   └── USBDeviceDetector.java # USB detection
├── src/main/resources/
│   └── icon.png               # Application icon
├── build-and-install.bat      # Build script
├── installer.iss              # Inno Setup installer config
├── ServicegestCompanion.bat   # Launcher script (included in installer)
├── version.json               # Version info for auto-updates
├── pom.xml                    # Maven configuration
└── README.md
```

## Configuration

The application monitors: `https://api.servicegest.ro/health`

Health checks run every 10 seconds by default.

## License

MIT License

---

**Current Version:** 1.2.3  
**Last Updated:** November 2025
