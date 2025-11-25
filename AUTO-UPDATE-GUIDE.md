# Auto-Update Feature Guide

## Overview

The Servicegest Companion App now includes automatic update functionality. The app can:
- Check for new releases on GitHub
- Notify you when updates are available
- Download and install updates automatically
- Restart with the new version

## How It Works

### 1. Version Management
- Current version is stored in `version.json`
- App compares local version with latest GitHub release
- If GitHub version is newer, update is available

### 2. Update Check Process
```
App Starts
    ↓
Check version.json (e.g., 1.0.0)
    ↓
Call GitHub API: /repos/geroffss/svg-companion/releases/latest
    ↓
Compare versions
    ↓
If new version available → Show notification
    ↓
User can click "Check for Updates" or "Update Now"
    ↓
Download new .exe installer from GitHub release
    ↓
Run installer and restart app
```

### 3. GitHub Release Requirements

For auto-update to work, you must:
1. Create a release on GitHub (https://github.com/geroffss/svg-companion/releases/new)
2. Upload the `.exe` file to the release assets
3. Tag version must match `version.json` (e.g., v1.0.1 for version 1.0.1)

**Example:**
```
Release Name: v1.0.1
Tag: v1.0.1
Assets: Servicegest-Companion-1.0.0.exe
Release Notes: Bug fixes and improvements
```

## User Features

### Automatic Update Check on Startup
- App checks for updates 2 seconds after launching
- If update available, shows notification dialog
- User can choose "Update Now" or "Later"

### Manual Update Check
- Click "Check for Updates" button in the app
- Shows current and latest version
- Displays release notes
- Option to update or skip

### Update Installation Process
1. User clicks "Update Now"
2. App downloads new installer to temp folder
3. Progress dialog shows download status
4. New installer runs automatically
5. App restarts with new version

## For Developers

### Creating a New Release

1. **Update version.json:**
```json
{
  "version": "1.0.1",
  "releaseNotes": "• Bug fixes\n• Performance improvements",
  "downloadUrl": "https://github.com/geroffss/svg-companion/releases/download/v1.0.1/Servicegest-Companion-1.0.1.exe",
  "mandatory": false,
  "minVersion": "1.0.0"
}
```

2. **Build new installer:**
```bash
mvn clean package
iscc.exe setup.iss
```

3. **Create GitHub Release:**
   - Go to: https://github.com/geroffss/svg-companion/releases/new
   - Tag: v1.0.1
   - Title: Version 1.0.1
   - Description: Release notes
   - Upload: `installer-output/Servicegest-Companion-1.0.1.exe`
   - Click "Publish release"

### Classes Involved

**GitHubReleaseChecker.java**
- Fetches latest release info from GitHub API
- Compares version strings
- Extracts download URL and release notes

**AutoUpdater.java**
- Downloads files from GitHub
- Reads current version from version.json
- Creates update batch script
- Manages update process

**CompanionApp.java**
- Shows "Check for Updates" button
- Handles automatic update check on startup
- Shows dialogs for update notifications
- Manages update installation flow

## Troubleshooting

### Update Check Fails
- Verify internet connection
- Check if GitHub is accessible
- Look at console output for error messages

### App Doesn't Show Update Available
- Verify new release is published on GitHub
- Check tag matches version format (e.g., v1.0.1)
- Verify .exe file is in release assets
- May take time to reflect in API response

### Update Download Fails
- Check download URL in release
- Verify file hasn't been deleted
- Check disk space for download
- Verify internet connection

### App Doesn't Restart After Update
- Check Windows Task Manager - may still be running
- Run installer manually from temp folder
- Check installer log for errors

## API Reference

### GitHub API Endpoint
```
GET https://api.github.com/repos/geroffss/svg-companion/releases/latest
```

Returns JSON with:
- `tag_name` - Release version (e.g., "v1.0.1")
- `body` - Release notes
- `assets` - Array of downloadable files
  - `browser_download_url` - Download link for .exe

### Version Comparison
```
compareVersions("1.0.1", "1.0.0") → returns 1 (first is newer)
compareVersions("1.0.0", "1.0.1") → returns -1 (first is older)
compareVersions("1.0.0", "1.0.0") → returns 0 (same version)
```

## Configuration

### Check on Startup
Edit `CompanionApp.java` line ~170:
```java
// Delay before checking for updates (milliseconds)
Thread.sleep(2000);
```

### Update Check Button
The "Check for Updates" button appears next to "Check Now" button in the UI

### Timeout Settings
- GitHub API timeout: 5 seconds
- File download timeout: 30 seconds

## Technical Details

- Uses Java HttpURLConnection (no external dependencies)
- Manual JSON parsing (no JSON library required)
- GitHub API v3 (JSON format)
- Batch script for installation restart
- Version file: `version.json` (root directory)

## Security

- Downloads only from official GitHub releases
- Uses HTTPS for API calls and downloads
- Backup of old installer maintained
- No automatic restart - user must confirm

## Future Enhancements

- Scheduled update checks (e.g., daily)
- Mandatory vs optional updates
- Staged rollout (percentage of users)
- Delta updates (patch only what changed)
- Update history log
