@echo off
setlocal enabledelayedexpansion

echo ====================================
echo  Companion App Builder (JPackage)
echo ====================================
echo.

REM Check Java version
echo [1/6] Checking Java installation...
java -version 2>&1 | findstr /i "version" >nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found. Please install JDK 17 or later.
    pause
    exit /b 1
)
echo Java found!

REM Create necessary directories
echo.
echo [2/6] Creating directories...
if not exist "lib" mkdir lib
if not exist "out" mkdir out
if not exist "out\classes" mkdir out\classes
if not exist "out\installer" mkdir out\installer

REM Download JavaFX if not present
echo.
echo [3/6] Checking JavaFX libraries...
if not exist "lib\javafx-controls-21.jar" (
    echo Downloading JavaFX 21...
    echo Please download JavaFX SDK from: https://gluonhq.com/products/javafx/
    echo Extract it and copy all JAR files from the 'lib' folder to: %CD%\lib\
    echo.
    echo Required files:
    echo   - javafx-controls-21.jar
    echo   - javafx-graphics-21.jar
    echo   - javafx-base-21.jar
    echo   - javafx-fxml-21.jar
    echo.
    pause
)

REM Download JNA if not present
echo.
echo [4/6] Checking JNA library...
if not exist "lib\jna-5.14.0.jar" (
    echo Downloading JNA 5.14.0...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.14.0/jna-5.14.0.jar' -OutFile 'lib\jna-5.14.0.jar'"
)
if not exist "lib\jna-platform-5.14.0.jar" (
    echo Downloading JNA Platform 5.14.0...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/net/java/dev/jna/jna-platform/5.14.0/jna-platform-5.14.0.jar' -OutFile 'lib\jna-platform-5.14.0.jar'"
)

REM Compile Java sources
echo.
echo [5/6] Compiling Java sources...
set CLASSPATH=lib\*;out\classes
javac -d out\classes --module-path lib --add-modules javafx.controls src\main\java\com\companion\app\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)
echo Compilation successful!

REM Create JAR
echo.
echo [5.5/6] Creating JAR file...
cd out\classes
jar --create --file ..\companion-app.jar --main-class com.companion.app.CompanionApp com\companion\app\*.class
cd ..\..
echo JAR created: out\companion-app.jar

REM Create installer with jpackage
echo.
echo [6/6] Creating Windows installer with jpackage...
jpackage ^
  --input out ^
  --name "CompanionApp" ^
  --main-jar companion-app.jar ^
  --main-class com.companion.app.CompanionApp ^
  --type exe ^
  --dest out\installer ^
  --app-version 1.0.0 ^
  --vendor "Companion" ^
  --description "Companion App with auto-start" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --win-per-user-install ^
  --runtime-image "%JAVA_HOME%" ^
  --module-path lib ^
  --add-modules javafx.controls

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====================================
    echo  BUILD SUCCESSFUL!
    echo ====================================
    echo.
    echo Installer created in: out\installer\
    echo.
) else (
    echo.
    echo ERROR: jpackage failed. Make sure you have JDK 17+ with jpackage included.
    echo If JAVA_HOME is not set, jpackage might not include the runtime.
)

pause
