# OTA (Over-The-Air) Update System Documentation

## 🔄 How It Works

The app automatically checks for updates on startup and via the system tray menu. When a new version is found, users are prompted to download and install it automatically.

---

## 🚀 Quick Setup with GitLab

### Option 1: GitLab Releases (Recommended)

This is the easiest way to host your updates using GitLab's built-in release management.

#### Step 1: Create GitLab Repository

```powershell
# Add GitLab remote (if not already added)
git remote add gitlab https://gitlab.com/yourusername/companion-app.git

# Push your code
git push gitlab main
```

#### Step 2: Configure Update Manager

Edit `src\main\java\com\companion\app\UpdateManager.java`:

```java
// Change these constants:
private static final String UPDATE_CHECK_URL = "https://yourdomain.com/version.json";
private static final String CURRENT_VERSION = "1.0.1"; // Your current version
```

**For GitLab Pages hosting:**
```java
private static final String UPDATE_CHECK_URL = "https://yourusername.gitlab.io/companion-app/version.json";
```

#### Step 3: Create version.json

Create a `version.json` file and host it on your server or GitLab Pages:

```json
{
  "version": "1.0.1",
  "releaseNotes": "Bug fixes and performance improvements",
  "downloadUrl": "https://gitlab.com/yourusername/companion-app/-/releases/v1.0.1/downloads/companion-app.jar",
  "mandatory": false,
  "minVersion": "1.0.0"
}
```

#### Step 4: Release Process

When you want to release a new version:

1. **Update version in code:**
   ```java
   // In UpdateManager.java
   private static final String CURRENT_VERSION = "1.0.1";
   ```

2. **Build the new JAR:**
   ```powershell
   .\build-all.bat
   ```

3. **Create Git tag:**
   ```powershell
   git add .
   git commit -m "Release v1.0.1"
   git tag -a v1.0.1 -m "Version 1.0.1 - Bug fixes"
   git push gitlab main --tags
   ```

4. **Create GitLab Release:**
   - Go to your GitLab repo
   - Navigate to **Deploy** → **Releases**
   - Click **New Release**
   - Select your tag (v1.0.1)
   - Upload `out\companion-app.jar`
   - Right-click the uploaded JAR and copy the download URL

5. **Update version.json:**
   ```json
   {
     "version": "1.0.1",
     "releaseNotes": "• Fixed login bug\n• Improved performance\n• Updated UI",
     "downloadUrl": "https://gitlab.com/yourusername/companion-app/-/releases/v1.0.1/downloads/companion-app.jar",
     "mandatory": false
   }
   ```

6. **Upload version.json to your server or GitLab Pages**

---

## 🌐 Option 2: Custom Server

### Express.js Server Example

```javascript
const express = require('express');
const app = express();
const path = require('path');

// Serve version.json
app.get('/api/version.json', (req, res) => {
  res.json({
    version: '1.0.1',
    releaseNotes: 'Bug fixes and improvements',
    downloadUrl: 'https://yourdomain.com/api/download/companion-app-1.0.1.jar',
    mandatory: false
  });
});

// Serve JAR files
app.get('/api/download/:filename', (req, res) => {
  const file = path.join(__dirname, 'releases', req.params.filename);
  res.download(file);
});

app.listen(3000, () => {
  console.log('Update server running on port 3000');
});
```

### PHP Server Example

```php
<?php
// version.php
header('Content-Type: application/json');
echo json_encode([
    'version' => '1.0.1',
    'releaseNotes' => 'Bug fixes and improvements',
    'downloadUrl' => 'https://yourdomain.com/releases/companion-app-1.0.1.jar',
    'mandatory' => false
]);
?>
```

---

## 🛠️ Advanced Configuration

### Version Format

The system supports semantic versioning (X.Y.Z):
- **X** = Major version (breaking changes)
- **Y** = Minor version (new features)
- **Z** = Patch version (bug fixes)

Examples:
- `1.0.0` → `1.0.1` (patch)
- `1.0.1` → `1.1.0` (minor)
- `1.1.0` → `2.0.0` (major)

### Mandatory Updates

Force users to update by setting `mandatory: true`:

```json
{
  "version": "2.0.0",
  "releaseNotes": "Critical security update - Required",
  "downloadUrl": "...",
  "mandatory": true,
  "minVersion": "1.0.0"
}
```

### Minimum Version

Prevent old clients from updating if they need intermediate updates:

```json
{
  "version": "3.0.0",
  "minVersion": "2.5.0",
  "releaseNotes": "Please update to 2.5.0 first"
}
```

---

## ✨ Features

- ✅ **Automatic check on startup**
- ✅ **Manual check via system tray menu**
- ✅ **Downloads and installs automatically**
- ✅ **Auto-restart after update**
- ✅ **Backup of previous version** (`.backup` file)
- ✅ **Progress notifications**
- ✅ **Rollback capability** (keeps backup)
- ✅ **Semantic version comparison**

---

## 🐛 Troubleshooting

### Updates Not Detected

1. **Check URL is accessible:**
   ```powershell
   curl https://yourdomain.com/version.json
   ```

2. **Verify JSON format:**
   - Use [JSONLint](https://jsonlint.com/) to validate

3. **Check version comparison:**
   - App version: Check `UpdateManager.CURRENT_VERSION`
   - Server version: Check `version.json`

### Download Fails

1. **Verify JAR URL is direct download link:**
   ```powershell
   curl -O https://your-jar-download-url.jar
   ```

2. **Check firewall/antivirus** settings

3. **Ensure HTTPS URLs** (HTTP might be blocked)

### App Doesn't Restart

- Check that the JAR path is correct
- Verify Java is in system PATH
- Look for error messages in console

---

## 📋 Checklist for Each Release

- [ ] Update version number in `UpdateManager.java`
- [ ] Build new JAR with `build-all.bat`
- [ ] Test the installer locally
- [ ] Create Git tag with version number
- [ ] Push to GitLab with tags
- [ ] Create GitLab Release
- [ ] Upload JAR to release
- [ ] Copy JAR download URL
- [ ] Update `version.json` with new version and URL
- [ ] Upload `version.json` to server
- [ ] Test update from previous version
- [ ] Announce release to users

---

## 📁 File Structure for Updates

```
your-server/
├── version.json              # Current version info
└── releases/
    ├── companion-app-1.0.0.jar
    ├── companion-app-1.0.1.jar
    └── companion-app-1.0.2.jar
```

---

## 🔐 Security Considerations

1. **Use HTTPS** for all update URLs
2. **Verify JAR signatures** (optional but recommended)
3. **Rate limit** update check endpoint
4. **Backup before updating** (automatic in current implementation)
5. **Log update attempts** for monitoring

---

## 📊 Monitoring Updates

Track update adoption by logging requests to `version.json`:

```javascript
// Express.js example
app.get('/api/version.json', (req, res) => {
  const clientVersion = req.headers['x-app-version'] || 'unknown';
  console.log(`Version check from: ${clientVersion}`);
  
  // Log to database, analytics, etc.
  
  res.json({ version: '1.0.1', ... });
});
```

---

## 🎯 Best Practices

1. **Test updates thoroughly** before releasing
2. **Keep release notes clear** and user-friendly
3. **Version increment rules:**
   - Bug fixes: Patch (1.0.X)
   - New features: Minor (1.X.0)
   - Breaking changes: Major (X.0.0)
4. **Always keep a rollback plan**
5. **Announce major updates** to users in advance
6. **Monitor update success rate**

---

## 📞 Support

If users experience update issues:
1. Check server logs
2. Verify `version.json` is accessible
3. Test JAR download manually
4. Check client logs/console output
5. Provide manual download link as fallback
