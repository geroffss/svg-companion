# Companion App

A Windows desktop application that provides a login interface and opens localhost:3000 in your default browser.

## Features

- 🚀 **Auto-start with Windows** - Automatically launches when Windows starts
- 🔐 **Login interface** - Secure login page with username/password
- 🌐 **Browser integration** - Opens localhost:3000 with a single click
- 📦 **Easy installation** - Windows installer included

## Prerequisites

- Java 17 or later
- Maven 3.6+
- (Optional) Inno Setup for creating Windows installer

## Building the Application

### 1. Build JAR file

```powershell
mvn clean package
```

This creates `companion-app-1.0.0.jar` in the `target` folder.

### 2. Run the application

```powershell
mvn javafx:run
```

Or run the JAR directly:

```powershell
java -jar target/companion-app-1.0.0.jar
```

## Creating Windows Installer

### Option 1: Using Launch4j + Inno Setup (Recommended)

1. **Install Launch4j** from https://launch4j.sourceforge.net/
2. **Install Inno Setup** from https://jrsoftware.org/isdl.php

3. **Create Windows executable with Launch4j:**
   - Open Launch4j
   - Set Output file: `launcher/CompanionApp.exe`
   - Set Jar: `target/companion-app-1.0.0.jar`
   - Set Min JRE version: 17.0.0
   - Save configuration and build

4. **Create installer with Inno Setup:**
   ```powershell
   iscc installer.iss
   ```

The installer will be created in `target/installer/`.

### Option 2: Using JPackage (Java 14+)

```powershell
# Build the JAR first
mvn clean package

# Create Windows installer
jpackage --input target `
  --name CompanionApp `
  --main-jar companion-app-1.0.0.jar `
  --main-class com.companion.app.CompanionApp `
  --type exe `
  --win-menu `
  --win-shortcut `
  --win-dir-chooser
```

## Project Structure

```
companion2/
├── src/
│   └── main/
│       ├── java/com/companion/app/
│       │   ├── CompanionApp.java          # Main application class
│       │   ├── AutoStartManager.java       # Windows auto-start handler
│       │   └── BrowserLauncher.java        # Browser integration
│       └── resources/
│           └── icon.ico                    # Application icon
├── pom.xml                                 # Maven configuration
├── installer.iss                           # Inno Setup script
└── README.md
```

## Auto-Start Configuration

The application automatically configures itself to start with Windows by adding an entry to:
```
HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run
```

You can disable auto-start by removing the registry entry or through the installer options.

## Customization

### Change Authentication Logic

Edit `CompanionApp.java` - `authenticate()` method:

```java
private boolean authenticate(String username, String password) {
    // Add your authentication logic here
    // e.g., check against database, API call, etc.
    return username.equals("admin") && password.equals("password");
}
```

### Change Target URL

Edit `CompanionApp.java` - modify the URL in the button action:

```java
openBrowserButton.setOnAction(e -> {
    BrowserLauncher.openURL("http://your-url-here:port");
});
```

## Troubleshooting

### Application doesn't start automatically
- Check Windows Registry for the entry in `Run` key
- Ensure the executable path is correct
- Run as Administrator during installation

### Browser doesn't open
- Ensure localhost:3000 is running
- Check firewall settings
- Verify Desktop API is supported on your system

## License

MIT License
