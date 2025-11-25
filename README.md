# Servicegest Companion App - API Health Monitor

A lightweight Windows desktop application that monitors the health status of the Servicegest API (`api.servicegest.ro/health`) endpoint.

## Features

✅ **API Health Monitoring** - Checks if `api.servicegest.ro/health` returns HTTP 200 status code  
✅ **Real-time Status Display** - Shows current API status with visual indicators  
✅ **Automatic Health Checks** - Periodic checks every 10 seconds (configurable)  
✅ **Manual Check** - Perform immediate health check on demand  
✅ **Timestamp Logging** - See when each health check was performed  
✅ **Windows Installer** - Easy installation with Inno Setup  

## Quick Start

### Prerequisites

- **Java 17 or later** - Download from [Adoptium](https://adoptium.net/)
- **Maven 3.6+** (optional, for building from source)
- **Inno Setup 6** (optional, for creating installer) - Download from [jrsoftware.org](https://jrsoftware.org/isdl.php)

### Installation

1. Download the installer: `Servicegest-Companion-1.0.0.exe` from releases
2. Run the installer and follow the prompts
3. The application will be installed to `C:\Program Files\Servicegest\Companion`
4. Launch from Desktop shortcut or Start Menu

### Running the Application

**Option 1: Direct JAR Execution**
```powershell
java -jar target\companion-app-all.jar
```

**Option 2: Using Windows Installer**
- Install using `Servicegest-Companion-1.0.0.exe`
- Launch from Desktop shortcut or Start Menu

## Building from Source

### Prerequisites
- Java 17 JDK
- Maven 3.6+
- Inno Setup 6 (for installer creation)

### Build Steps

1. **Clone or download the repository**
   ```powershell
   cd companion2
   ```

2. **Build with Maven**
   ```powershell
   mvn clean package -DskipTests
   ```

   This creates:
   - `target\companion-app-all.jar` - Standalone JAR file
   - `target\lib\` - Dependencies folder

3. **Create Windows Installer**
   ```powershell
   "C:\Program Files (x86)\Inno Setup 6\iscc.exe" setup.iss
   ```

   The installer will be created in `installer-output\Servicegest-Companion-1.0.0.exe`

### Using the Build Script

For convenience, use the build script:
```powershell
.\build-all.bat
```

This script automatically:
1. Checks Maven and Java availability
2. Cleans previous builds
3. Builds the JAR with Maven
4. Creates the Windows installer (if Inno Setup is installed)

## Application Usage

### Main Screen

The application displays:
- **Status Indicator** - Shows if API is healthy (✓) or down (✗)
- **API Endpoint** - Monitored URL: `https://api.servicegest.ro/health`
- **Last Check Time** - Timestamp of the most recent health check
- **Manual Check Button** - Immediately trigger a health check
- **Interval Selector** - Set automatic check interval (5-60 seconds)
- **Details Area** - View detailed check results

### Status Meanings

- ✓ **API is Healthy** (Green) - API responds with HTTP 200 status code
- ✗ **API is Down** (Red) - API not responding or returns non-200 status
- **Checking...** (Orange) - Health check in progress

## Configuration

### Health Check Endpoint

The application checks: `https://api.servicegest.ro/health`

To modify, edit `src/main/java/com/servicegest/companion/app/HealthChecker.java`:

```java
private static final String API_ENDPOINT = "https://api.servicegest.ro/health";
```

### Check Timeout

Default timeout is 5 seconds. Modify in `HealthChecker.java`:

```java
private static final int TIMEOUT_MS = 5000; // milliseconds
```

### Default Check Interval

Default interval is 10 seconds. Modify in `CompanionApp.java`:

```java
private static final long HEALTH_CHECK_INTERVAL = 10000; // milliseconds
```

## Project Structure

```
companion2/
├── src/main/java/com/servicegest/companion/app/
│   ├── CompanionApp.java        # Main application & UI
│   └── HealthChecker.java       # API health check utility
├── src/main/resources/
│   └── (application resources)
├── target/
│   ├── companion-app-all.jar    # Standalone executable JAR
│   └── lib/                     # Dependencies
├── pom.xml                      # Maven configuration
├── setup.iss                    # Inno Setup installer script
├── build-all.bat               # Build script
└── README.md                   # This file
```

## Troubleshooting

### "Java not found"
- Ensure Java 17+ is installed: https://adoptium.net/
- Add Java to PATH or set `JAVA_HOME` environment variable

### "Maven not found"
- Install Maven: https://maven.apache.org/
- Add Maven bin folder to PATH

### "Inno Setup not found"
- Download and install from: https://jrsoftware.org/isdl.php
- The JAR will still work without installer

### Application won't start
- Check Java version: `java -version`
- Try running with console output:
  ```powershell
  java -jar target\companion-app-all.jar
  ```

### API always shows as down
- Verify internet connectivity
- Check if `api.servicegest.ro` is accessible
- Verify firewall/proxy settings aren't blocking the connection

## Development

### Building for Development

```powershell
# Compile
mvn compile

# Run directly
mvn javafx:run

# Build executable
mvn package
```

### Modifying the Application

1. Edit source files in `src/main/java/com/servicegest/companion/app/`
2. Rebuild: `mvn clean package`
3. Test: `java -jar target\companion-app-all.jar`

## Technical Details

### Technologies Used
- **JavaFX 21** - UI Framework
- **Maven** - Build tool
- **Java HttpURLConnection** - HTTP requests
- **Inno Setup 6** - Windows installer creation

### Key Components

**CompanionApp.java**
- Main application class
- JavaFX UI management
- Health check scheduling
- Timer management

**HealthChecker.java**
- HTTP GET requests to API endpoint
- Response code verification
- Timeout handling
- Error reporting

### API Check Logic

```
1. Send GET request to https://api.servicegest.ro/health
2. Wait up to 5 seconds for response
3. Check if response status code is 200
4. Return: true (healthy) or false (down)
```

## License

MIT License - See LICENSE file for details

## Support

For issues or questions:
1. Check the Troubleshooting section
2. Review application logs in console
3. Verify API endpoint accessibility
4. Check Java and Maven versions

---

**Version:** 1.0.0  
**Last Updated:** November 2025
