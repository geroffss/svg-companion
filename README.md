# Companion App# Companion App



A Windows desktop application with auto-start, login interface, and OTA (Over-The-Air) updates from GitLab.A Windows desktop application that provides a login interface and opens localhost:3000 in your default browser.



## Features## Features



- 🚀 **Auto-start with Windows** - Automatically launches when Windows starts- 🚀 **Auto-start with Windows** - Automatically launches when Windows starts

- 🔐 **Login interface** - Secure login page with username/password- 🔐 **Login interface** - Secure login page with username/password

- 🌐 **Browser integration** - Opens localhost:3000 with a single click- 🌐 **Browser integration** - Opens localhost:3000 with a single click

- 🔄 **OTA Updates** - Automatic updates from GitLab releases- 📦 **Easy installation** - Windows installer included

- 📦 **Easy installation** - Windows installer with Inno Setup

## Prerequisites

---

- Java 17 or later

## 🏗️ Quick Start: Building the Installer- Maven 3.6+

- (Optional) Inno Setup for creating Windows installer

### Prerequisites

1. **Java 17+** - [Download OpenJDK](https://adoptium.net/)## Building the Application

2. **Inno Setup 6** - [Download](https://jrsoftware.org/isdl.php)

### 1. Build JAR file

### One-Command Build (EASIEST)

```powershell

Simply run this script - it does everything:mvn clean package

```

```powershell

.\build-installer.batThis creates `companion-app-1.0.0.jar` in the `target` folder.

```

### 2. Run the application

**What it does:**

1. ✅ Checks if Inno Setup is installed```powershell

2. ✅ Compiles all Java filesmvn javafx:run

3. ✅ Creates the JAR file```

4. ✅ Packages everything into `CompanionApp-Setup.exe`

5. ✅ Output in `installer-output\` folderOr run the JAR directly:



**That's it!** Your installer is ready to distribute.```powershell

java -jar target/companion-app-1.0.0.jar

---```



## 📦 Manual Build Steps (Alternative)## Creating Windows Installer



If you prefer to build step-by-step:### Option 1: Using Launch4j + Inno Setup (Recommended)



### Step 1: Compile the Java Code1. **Install Launch4j** from https://launch4j.sourceforge.net/

2. **Install Inno Setup** from https://jrsoftware.org/isdl.php

```powershell

# Create output directories3. **Create Windows executable with Launch4j:**

if not exist "out\classes" mkdir out\classes   - Open Launch4j

   - Set Output file: `launcher/CompanionApp.exe`

# Compile all Java files   - Set Jar: `target/companion-app-1.0.0.jar`

javac -d out\classes --module-path lib --add-modules javafx.controls src\main\java\com\companion\app\*.java   - Set Min JRE version: 17.0.0

```   - Save configuration and build



### Step 2: Create the JAR File4. **Create installer with Inno Setup:**

   ```powershell

```powershell   iscc installer.iss

cd out\classes   ```

jar --create --file ..\companion-app.jar --main-class com.companion.app.CompanionApp com\companion\app\*.class

cd ..\..The installer will be created in `target/installer/`.

```

### Option 2: Using JPackage (Java 14+)

### Step 3: Test the Application

```powershell

```powershell# Build the JAR first

java -cp "out\companion-app.jar;lib\*" --module-path lib --add-modules javafx.controls com.companion.app.CompanionAppmvn clean package

```

# Create Windows installer

### Step 4: Create the Installerjpackage --input target `

  --name CompanionApp `

```powershell  --main-jar companion-app-1.0.0.jar `

"C:\Program Files (x86)\Inno Setup 6\iscc.exe" setup.iss  --main-class com.companion.app.CompanionApp `

```  --type exe `

  --win-menu `

Your installer will be in `installer-output\CompanionApp-Setup.exe`  --win-shortcut `

  --win-dir-chooser

---```



## 🔄 OTA Updates from GitLab## Project Structure



### Setup GitLab for Updates```

companion2/

1. **Create a GitLab repository** (if not already done)├── src/

│   └── main/

2. **Add GitLab remote:**│       ├── java/com/companion/app/

   ```powershell│       │   ├── CompanionApp.java          # Main application class

   git remote add gitlab https://gitlab.com/yourusername/companion-app.git│       │   ├── AutoStartManager.java       # Windows auto-start handler

   ```│       │   └── BrowserLauncher.java        # Browser integration

│       └── resources/

3. **Create GitLab Release:**│           └── icon.ico                    # Application icon

   - Go to your GitLab repo → **Deploy** → **Releases**├── pom.xml                                 # Maven configuration

   - Click **New Release**├── installer.iss                           # Inno Setup script

   - Upload your `companion-app.jar` file└── README.md

   - Copy the direct download URL```



4. **Update version.json** on your server or GitLab Pages:## Auto-Start Configuration

   ```json

   {The application automatically configures itself to start with Windows by adding an entry to:

     "version": "1.0.1",```

     "releaseNotes": "Bug fixes and improvements",HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run

     "downloadUrl": "https://gitlab.com/yourusername/companion-app/-/releases/v1.0.1/downloads/companion-app.jar",```

     "mandatory": false

   }You can disable auto-start by removing the registry entry or through the installer options.

   ```

## Customization

5. **Configure the app** (see next section)

### Change Authentication Logic

### Configure OTA in the App

Edit `CompanionApp.java` - `authenticate()` method:

Edit `src\main\java\com\companion\app\UpdateManager.java`:

```java

```javaprivate boolean authenticate(String username, String password) {

// Change these URLs:    // Add your authentication logic here

private static final String UPDATE_CHECK_URL = "https://your-domain.com/version.json";    // e.g., check against database, API call, etc.

// OR for GitLab Pages:    return username.equals("admin") && password.equals("password");

private static final String UPDATE_CHECK_URL = "https://yourusername.gitlab.io/companion-app/version.json";}

```

private static final String CURRENT_VERSION = "1.0.1"; // Update this with each release

```### Change Target URL



### Release ProcessEdit `CompanionApp.java` - modify the URL in the button action:



1. **Update version** in `UpdateManager.java````java

2. **Build new installer** with `build-installer.bat`openBrowserButton.setOnAction(e -> {

3. **Create Git tag:**    BrowserLauncher.openURL("http://your-url-here:port");

   ```powershell});

   git tag -a v1.0.1 -m "Version 1.0.1"```

   git push gitlab --tags

   ```## Troubleshooting

4. **Create GitLab Release** and upload JAR

5. **Update version.json** on your server### Application doesn't start automatically

6. **Users get notified automatically!**- Check Windows Registry for the entry in `Run` key

- Ensure the executable path is correct

---- Run as Administrator during installation



## 🗂️ Project Structure### Browser doesn't open

- Ensure localhost:3000 is running

```- Check firewall settings

companion2/- Verify Desktop API is supported on your system

├── src/main/java/com/companion/app/

│   ├── CompanionApp.java          # Main application & UI## License

│   ├── UpdateManager.java         # OTA update system

│   ├── AutoStartManager.java      # Windows auto-startMIT License

│   └── BrowserLauncher.java       # Browser integration
├── lib/                           # JavaFX & dependencies
├── out/                           # Compiled output (ignored)
├── installer-output/              # Final installer (ignored)
├── build-installer.bat            # 🌟 ONE-CLICK BUILD SCRIPT
├── setup.iss                      # Inno Setup configuration
├── version.json                   # Current version info
└── README.md
```

---

## 🛠️ Development

### Run During Development

```powershell
# Quick compile and run
javac -d out\classes --module-path lib --add-modules javafx.controls src\main\java\com\companion\app\*.java
java -cp "out\classes;lib\*" --module-path lib --add-modules javafx.controls com.companion.app.CompanionApp
```

### Test Auto-Start

After installation, check Windows Registry:
```
HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run
```

### Customize

**Change login credentials:** Edit `authenticate()` in `CompanionApp.java`

**Change target URL:** Edit `BrowserLauncher.openURL()` call in `CompanionApp.java`

**Change app icon:** Replace files in `src\main\resources\` and update `setup.iss`

---

## 🐛 Troubleshooting

### "Inno Setup not found"
- Download and install from https://jrsoftware.org/isdl.php
- Use default installation path

### "javac not recognized"
- Install Java JDK 17+
- Add to PATH: `C:\Program Files\Java\jdk-17\bin`

### "JavaFX not found"
- Ensure `lib\` folder contains JavaFX JARs
- Download from https://gluonhq.com/products/javafx/ if needed

### App doesn't update
- Check `version.json` URL is accessible
- Verify `downloadUrl` points to valid JAR
- Check firewall/antivirus settings

---

## 📤 Push to GitHub & GitLab

```powershell
# Push to GitHub
git add .
git commit -m "Update version"
git push origin main

# Push to GitLab
git push gitlab main --tags
```

---

## 📄 License

MIT License
