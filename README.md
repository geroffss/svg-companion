# Companion App# Companion App# Companion App



A Windows desktop application with auto-start, login interface, and OTA (Over-The-Air) updates from GitLab.



## FeaturesA Windows desktop application with auto-start, login interface, and OTA (Over-The-Air) updates from GitLab.A Windows desktop application that provides a login interface and opens localhost:3000 in your default browser.



- 🚀 **Auto-start with Windows** - Automatically launches when Windows starts

- 🔐 **Login interface** - Secure login page with username/password

- 🌐 **Browser integration** - Opens localhost:3000 with a single click## Features## Features

- 🔄 **OTA Updates** - Automatic updates from GitLab releases

- 📦 **Easy installation** - Windows installer with Inno Setup



---- 🚀 **Auto-start with Windows** - Automatically launches when Windows starts- 🚀 **Auto-start with Windows** - Automatically launches when Windows starts



## 🏗️ Quick Start: Building the Installer- 🔐 **Login interface** - Secure login page with username/password- 🔐 **Login interface** - Secure login page with username/password



### Prerequisites- 🌐 **Browser integration** - Opens localhost:3000 with a single click- 🌐 **Browser integration** - Opens localhost:3000 with a single click

1. **Java 17+** - [Download OpenJDK](https://adoptium.net/)

2. **Inno Setup 6** - [Download](https://jrsoftware.org/isdl.php)- 🔄 **OTA Updates** - Automatic updates from GitLab releases- 📦 **Easy installation** - Windows installer included



### One-Command Build (EASIEST)- 📦 **Easy installation** - Windows installer with Inno Setup



Simply run this script - it does everything:## Prerequisites



```powershell---

.\build-all.bat

```- Java 17 or later



**What it does:**## 🏗️ Quick Start: Building the Installer- Maven 3.6+

1. ✅ Checks if Inno Setup is installed

2. ✅ Compiles all Java files- (Optional) Inno Setup for creating Windows installer

3. ✅ Creates the JAR file

4. ✅ Packages everything into Windows installer### Prerequisites

5. ✅ Output in `installer-output\` folder

1. **Java 17+** - [Download OpenJDK](https://adoptium.net/)## Building the Application

**That's it!** Your installer is ready to distribute.

2. **Inno Setup 6** - [Download](https://jrsoftware.org/isdl.php)

The installer will be: `installer-output\Servicegest-Setup-1.0.0.exe`

### 1. Build JAR file

---

### One-Command Build (EASIEST)

## 📦 Manual Build Steps (Alternative)

```powershell

If you prefer to build step-by-step:

Simply run this script - it does everything:mvn clean package

### Step 1: Compile the Java Code

```

```powershell

# Create output directories```powershell

if not exist "out\classes" mkdir out\classes

.\build-installer.batThis creates `companion-app-1.0.0.jar` in the `target` folder.

# Compile all Java files

javac -cp "lib\*" -d out\classes src\main\java\com\companion\app\*.java```

```

### 2. Run the application

### Step 2: Create the JAR File

**What it does:**

```powershell

cd out\classes1. ✅ Checks if Inno Setup is installed```powershell

jar --create --file ..\companion-app.jar --main-class com.companion.app.CompanionApp com\companion\app\*.class

cd ..\..2. ✅ Compiles all Java filesmvn javafx:run

```

3. ✅ Creates the JAR file```

### Step 3: Test the Application

4. ✅ Packages everything into `CompanionApp-Setup.exe`

```powershell

java -cp "out\companion-app.jar;lib\*" com.companion.app.CompanionApp5. ✅ Output in `installer-output\` folderOr run the JAR directly:

```



### Step 4: Create the Installer

**That's it!** Your installer is ready to distribute.```powershell

```powershell

"C:\Program Files (x86)\Inno Setup 6\iscc.exe" setup.issjava -jar target/companion-app-1.0.0.jar

```

---```

Your installer will be in `installer-output\Servicegest-Setup-1.0.0.exe`



---

## 📦 Manual Build Steps (Alternative)## Creating Windows Installer

## 🔄 OTA Updates from GitLab



### Setup GitLab for Updates

If you prefer to build step-by-step:### Option 1: Using Launch4j + Inno Setup (Recommended)

1. **Create a GitLab repository** (if not already done)



2. **Add GitLab remote:**

   ```powershell### Step 1: Compile the Java Code1. **Install Launch4j** from https://launch4j.sourceforge.net/

   git remote add gitlab https://gitlab.com/yourusername/companion-app.git

   ```2. **Install Inno Setup** from https://jrsoftware.org/isdl.php



3. **Create GitLab Release:**```powershell

   - Go to your GitLab repo → **Deploy** → **Releases**

   - Click **New Release**# Create output directories3. **Create Windows executable with Launch4j:**

   - Upload your `out\companion-app.jar` file

   - Copy the direct download URLif not exist "out\classes" mkdir out\classes   - Open Launch4j



4. **Update version.json** on your server or GitLab Pages:   - Set Output file: `launcher/CompanionApp.exe`

   ```json

   {# Compile all Java files   - Set Jar: `target/companion-app-1.0.0.jar`

     "version": "1.0.1",

     "releaseNotes": "Bug fixes and improvements",javac -d out\classes --module-path lib --add-modules javafx.controls src\main\java\com\companion\app\*.java   - Set Min JRE version: 17.0.0

     "downloadUrl": "https://gitlab.com/yourusername/companion-app/-/releases/v1.0.1/downloads/companion-app.jar",

     "mandatory": false```   - Save configuration and build

   }

   ```



5. **Configure the app** (see next section)### Step 2: Create the JAR File4. **Create installer with Inno Setup:**



### Configure OTA in the App   ```powershell



Edit `src\main\java\com\companion\app\UpdateManager.java`:```powershell   iscc installer.iss



```javacd out\classes   ```

// Change these URLs:

private static final String UPDATE_CHECK_URL = "https://your-domain.com/version.json";jar --create --file ..\companion-app.jar --main-class com.companion.app.CompanionApp com\companion\app\*.class

// OR for GitLab Pages:

private static final String UPDATE_CHECK_URL = "https://yourusername.gitlab.io/companion-app/version.json";cd ..\..The installer will be created in `target/installer/`.



private static final String CURRENT_VERSION = "1.0.1"; // Update this with each release```

```

### Option 2: Using JPackage (Java 14+)

### Release Process

### Step 3: Test the Application

1. **Update version** in `UpdateManager.java`

2. **Build new installer** with `build-all.bat````powershell

3. **Create Git tag:**

   ```powershell```powershell# Build the JAR first

   git tag -a v1.0.1 -m "Version 1.0.1"

   git push gitlab --tagsjava -cp "out\companion-app.jar;lib\*" --module-path lib --add-modules javafx.controls com.companion.app.CompanionAppmvn clean package

   ```

4. **Create GitLab Release** and upload JAR```

5. **Update version.json** on your server

6. **Users get notified automatically!**# Create Windows installer



See [OTA-UPDATE-GUIDE.md](OTA-UPDATE-GUIDE.md) for complete instructions.### Step 4: Create the Installerjpackage --input target `



---  --name CompanionApp `



## 🗂️ Project Structure```powershell  --main-jar companion-app-1.0.0.jar `



```"C:\Program Files (x86)\Inno Setup 6\iscc.exe" setup.iss  --main-class com.companion.app.CompanionApp `

companion2/

├── src/main/java/com/companion/app/```  --type exe `

│   ├── CompanionApp.java          # Main application & UI

│   ├── UpdateManager.java         # OTA update system  --win-menu `

│   ├── AutoStartManager.java      # Windows auto-start

│   └── BrowserLauncher.java       # Browser integrationYour installer will be in `installer-output\CompanionApp-Setup.exe`  --win-shortcut `

├── lib/                           # JavaFX & JNA dependencies

├── out/                           # Compiled output (ignored)  --win-dir-chooser

├── installer-output/              # Final installer (ignored)

├── build-all.bat                  # 🌟 ONE-CLICK BUILD SCRIPT---```

├── setup.iss                      # Inno Setup configuration

├── version.json                   # Current version info

├── README.md                      # This file

├── OTA-UPDATE-GUIDE.md            # Complete OTA documentation## 🔄 OTA Updates from GitLab## Project Structure

└── GITLAB-SETUP.md                # Quick GitLab setup guide

```



---### Setup GitLab for Updates```



## 🛠️ Developmentcompanion2/



### Run During Development1. **Create a GitLab repository** (if not already done)├── src/



```powershell│   └── main/

# Quick compile and run

javac -cp "lib\*" -d out\classes src\main\java\com\companion\app\*.java2. **Add GitLab remote:**│       ├── java/com/companion/app/

java -cp "out\classes;lib\*" com.companion.app.CompanionApp

```   ```powershell│       │   ├── CompanionApp.java          # Main application class



### Test Auto-Start   git remote add gitlab https://gitlab.com/yourusername/companion-app.git│       │   ├── AutoStartManager.java       # Windows auto-start handler



After installation, check Windows Registry:   ```│       │   └── BrowserLauncher.java        # Browser integration

```

HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run│       └── resources/

```

3. **Create GitLab Release:**│           └── icon.ico                    # Application icon

### Customize

   - Go to your GitLab repo → **Deploy** → **Releases**├── pom.xml                                 # Maven configuration

**Change login credentials:** Edit `authenticate()` in `CompanionApp.java`

   - Click **New Release**├── installer.iss                           # Inno Setup script

**Change target URL:** Edit `BrowserLauncher.openURL()` call in `CompanionApp.java`

   - Upload your `companion-app.jar` file└── README.md

**Change app icon:** Replace files in `src\main\resources\` and update `setup.iss`

   - Copy the direct download URL```

---



## 🐛 Troubleshooting

4. **Update version.json** on your server or GitLab Pages:## Auto-Start Configuration

### "Inno Setup not found"

- Download and install from https://jrsoftware.org/isdl.php   ```json

- Use default installation path

   {The application automatically configures itself to start with Windows by adding an entry to:

### "javac not recognized"

- Install Java JDK 17+     "version": "1.0.1",```

- Add to PATH: `C:\Program Files\Java\jdk-17\bin`

     "releaseNotes": "Bug fixes and improvements",HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run

### "Cannot find JavaFX/JNA libraries"

- Ensure `lib\` folder contains all required JARs:     "downloadUrl": "https://gitlab.com/yourusername/companion-app/-/releases/v1.0.1/downloads/companion-app.jar",```

  - javafx-base-21.jar

  - javafx-controls-21.jar     "mandatory": false

  - javafx-graphics-21.jar

  - jna-5.14.0.jar   }You can disable auto-start by removing the registry entry or through the installer options.

  - jna-platform-5.14.0.jar

- Download JavaFX from https://gluonhq.com/products/javafx/ if needed   ```

- Download JNA from https://repo1.maven.org/maven2/net/java/dev/jna/

## Customization

### Compilation errors with JNA

The build script uses `-cp "lib\*"` to include all dependencies on the classpath. This works with both modular and non-modular JARs.5. **Configure the app** (see next section)



### App doesn't update### Change Authentication Logic

- Check `version.json` URL is accessible

- Verify `downloadUrl` points to valid JAR### Configure OTA in the App

- Check firewall/antivirus settings

Edit `CompanionApp.java` - `authenticate()` method:

---

Edit `src\main\java\com\companion\app\UpdateManager.java`:

## 📤 Push to GitHub & GitLab

```java

```powershell

# Push to GitHub```javaprivate boolean authenticate(String username, String password) {

git add .

git commit -m "Update version"// Change these URLs:    // Add your authentication logic here

git push origin main

private static final String UPDATE_CHECK_URL = "https://your-domain.com/version.json";    // e.g., check against database, API call, etc.

# Push to GitLab (for OTA updates)

git push gitlab main --tags// OR for GitLab Pages:    return username.equals("admin") && password.equals("password");

```

private static final String UPDATE_CHECK_URL = "https://yourusername.gitlab.io/companion-app/version.json";}

---

```

## 📚 Additional Documentation

private static final String CURRENT_VERSION = "1.0.1"; // Update this with each release

- **[OTA-UPDATE-GUIDE.md](OTA-UPDATE-GUIDE.md)** - Complete guide for setting up OTA updates

- **[GITLAB-SETUP.md](GITLAB-SETUP.md)** - Quick GitLab setup reference```### Change Target URL

- **[README-SETUP.md](README-SETUP.md)** - Original setup documentation



---

### Release ProcessEdit `CompanionApp.java` - modify the URL in the button action:

## 📄 License



MIT License

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
