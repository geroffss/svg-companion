# GitHub OTA Update Setup Guide

## 🎯 Quick Setup Steps

Follow these steps to enable OTA updates for your CompanionApp using GitHub:

---

## Step 1: Create GitHub Release (v1.0.0)

1. **Go to your GitHub repository:**
   ```
   https://github.com/geroffss/svg-companion
   ```

2. **Click on "Releases"** (right sidebar)

3. **Click "Create a new release"**

4. **Fill in the details:**
   - **Tag**: `v1.0.0` (already pushed)
   - **Release title**: `Version 1.0.0`
   - **Description**: 
     ```
     Initial release of CompanionApp
     
     Features:
     - Login interface
     - Auto-start with Windows
     - Browser integration to localhost:3000
     - OTA update system
     ```

5. **Upload the JAR file:**
   - Click "Attach binaries"
   - Upload: `out\companion-app.jar`
   - **Important:** Rename it to just `companion-app.jar` (remove version number for consistency)

6. **Click "Publish release"**

7. **Copy the download URL:**
   - After publishing, right-click the `companion-app.jar` link
   - Select "Copy link address"
   - It should look like:
     ```
     https://github.com/geroffss/svg-companion/releases/download/v1.0.0/companion-app.jar
     ```

---

## Step 2: Enable GitHub Pages

1. **Go to repository Settings:**
   ```
   https://github.com/geroffss/svg-companion/settings/pages
   ```

2. **Under "Build and deployment":**
   - **Source**: Deploy from a branch
   - **Branch**: `main`
   - **Folder**: `/ (root)`

3. **Click "Save"**

4. **Wait a few minutes** for the site to deploy

5. **Your GitHub Pages URL will be:**
   ```
   https://geroffss.github.io/svg-companion/
   ```

6. **The version.json will be accessible at:**
   ```
   https://geroffss.github.io/svg-companion/version.json
   ```

---

## Step 3: Test the Setup

Once GitHub Pages is deployed, test if version.json is accessible:

```powershell
# Test in PowerShell:
Invoke-WebRequest https://geroffss.github.io/svg-companion/version.json
```

You should see the JSON content from your `version.json` file.

---

## Step 4: Release a New Version (Testing OTA)

Now let's create version 1.0.1 to test the update system:

### 4.1: Update the version number

Edit `src\main\java\com\companion\app\UpdateManager.java`:
```java
private static final String CURRENT_VERSION = "1.0.1";  // Change from 1.0.0 to 1.0.1
```

### 4.2: Build the new version

```powershell
.\build-all.bat
```

### 4.3: Commit and tag

```powershell
git add .
git commit -m "Release v1.0.1"
git tag -a v1.0.1 -m "Version 1.0.1 - Test OTA update"
git push origin main --tags
```

### 4.4: Create GitHub Release v1.0.1

1. Go to **Releases** → **Create a new release**
2. **Tag**: `v1.0.1`
3. **Title**: `Version 1.0.1`
4. **Upload**: `out\companion-app.jar`
5. **Publish release**
6. **Copy the new download URL**

### 4.5: Update version.json

Edit `version.json`:
```json
{
  "version": "1.0.1",
  "releaseNotes": "Test update\n• Testing OTA update system\n• Bug fixes",
  "downloadUrl": "https://github.com/geroffss/svg-companion/releases/download/v1.0.1/companion-app.jar",
  "mandatory": false,
  "minVersion": "1.0.0"
}
```

### 4.6: Push the updated version.json

```powershell
git add version.json
git commit -m "Update version.json to 1.0.1"
git push origin main
```

Wait ~1 minute for GitHub Pages to update.

---

## Step 5: Test the OTA Update!

1. **Install version 1.0.0** using the installer
2. **Run the app**
3. **The app should automatically detect v1.0.1** and prompt for update
4. **Click "OK"** to download and install
5. **App restarts** with new version!

---

## 🔄 Regular Release Process

For every new release:

1. Update `CURRENT_VERSION` in `UpdateManager.java`
2. Run `.\build-all.bat`
3. Commit: `git commit -m "Release vX.X.X"`
4. Tag: `git tag -a vX.X.X -m "Version X.X.X"`
5. Push: `git push origin main --tags`
6. Create GitHub Release and upload JAR
7. Update `version.json` with new version and download URL
8. Push `version.json`: `git push origin main`
9. Wait ~1 minute for GitHub Pages to update
10. Users automatically get notified!

---

## 📋 Current Status

- ✅ Code configured for GitHub releases
- ✅ Tag v1.0.0 created and pushed
- ⏳ **Next: Create GitHub release v1.0.0** (Step 1 above)
- ⏳ **Next: Enable GitHub Pages** (Step 2 above)

---

## 🐛 Troubleshooting

### Version.json not accessible
- Wait 2-3 minutes after pushing
- Check GitHub Pages is enabled
- Verify branch is set to `main`

### Update not detected
- Test version.json URL in browser
- Check app version in `UpdateManager.java`
- Ensure version.json has newer version

### Download fails
- Verify JAR was uploaded to release
- Check download URL in version.json
- Ensure URL is the direct download link

---

## 📞 Need Help?

Check these URLs:
- Repository: https://github.com/geroffss/svg-companion
- Releases: https://github.com/geroffss/svg-companion/releases
- GitHub Pages: https://geroffss.github.io/svg-companion/
- Version JSON: https://geroffss.github.io/svg-companion/version.json
