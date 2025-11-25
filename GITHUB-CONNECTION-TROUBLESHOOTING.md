# GitHub Connection Troubleshooting

## Understanding GitHub API Responses

When you click "Check for Updates", the app shows you different messages based on what GitHub responds:

### Success: Release Found
```
Version 1.0.1 available
New version can be downloaded
```

### 404: No Releases Found (Expected at First)
```
HTTP Response: 404
No releases found on GitHub repository
```
**This is NORMAL!** It means:
- ✅ App can connect to GitHub
- ✅ GitHub API is responding
- ❌ No releases exist yet
- **Fix:** Create your first GitHub release

### Connection Issues

#### "Cannot reach GitHub - check internet connection"
- App cannot resolve dns.github.com or api.github.com
- **Fix:** Check your internet connection

#### "Connection refused"
- Firewall is blocking GitHub API
- **Fix:** Add exception for Java in Windows Firewall

#### "SSL/TLS error"
- Certificate validation failed
- **Fix:** Update Java certificates or disable TLS 1.0/1.1

#### Rate Limited (403 Forbidden)
```
HTTP Response: 403
Rate limited by GitHub
```
- Too many API requests (60 per hour for anonymous)
- **Fix:** Wait a few minutes and try again

## How to Enable Update Checks

### Step 1: Create GitHub Release

1. Go to: https://github.com/geroffss/svg-companion/releases/new
2. Click "Draft a new release"
3. **Tag version:** `v1.0.0` (must match version.json)
4. **Title:** "Version 1.0.0"
5. **Description:** Write release notes
6. **Upload file:** Drag `Servicegest-Companion-1.0.0.exe` into assets
7. Click "Publish release"

### Step 2: Verify Release Created

1. Go to https://github.com/geroffss/svg-companion/releases
2. Should see your release listed
3. Should see .exe file in assets

### Step 3: App Will Find It

Next time app checks for updates:
- HTTP Response: 200 (Success!)
- Release found
- Shows new version available

## Testing Update Check

### Manual Test in App
1. Start the app
2. Click "Check for Updates"
3. Should say "No releases found" OR show available update

### Console Output
Open Windows PowerShell in app folder:
```powershell
cd portable
java -cp "companion-app.jar" --module-path "lib" --add-modules javafx.controls,javafx.fxml com.servicegest.companion.app.CompanionApp 2>&1 | Select-Object -First 20
```

Look for output like:
```
[GitHubReleaseChecker] HTTP Response: 404
[GitHubReleaseChecker] No releases found on GitHub repository.
```

### Test with Real Release

1. Create release on GitHub with .exe file
2. Run app again
3. Should see "Update available" message

## Debug Steps

### If App Says "Could not connect to GitHub"

1. **Check internet:**
   ```powershell
   Invoke-WebRequest "https://api.github.com/repos/geroffss/svg-companion/releases/latest" -UseBasicParsing
   ```

2. **Check firewall:**
   - Windows Defender → Firewall & network protection
   - App has Java → Check if blocked
   - Allow Java for private and public networks

3. **Check GitHub status:**
   - Go to https://www.githubstatus.com/
   - Should be all green

4. **Restart app:**
   - Close and reopen app
   - Try "Check for Updates" again

### If App Shows 404 (Expected)

1. **This is normal** if no releases exist
2. **Create a release** to fix it
3. See "Step 1: Create GitHub Release" above

## Common Messages Explained

| Message | Meaning | Action |
|---------|---------|--------|
| "No releases found" (HTTP 404) | No GitHub release exists yet | Create release on GitHub |
| "Update available" | New version on GitHub | Click "Update Now" |
| "You're running latest" | Current version is newest | No update needed |
| "Could not connect" | Network/firewall issue | Check internet connection |
| "Rate limited" | Too many API requests | Wait a few minutes |
| "Release notes: New version available" | Generic message if no notes provided | Normal behavior |

## What Happens Behind the Scenes

```
App Starts
    ↓
[After 2 seconds]
    ↓
Call GitHub API:
  GET https://api.github.com/repos/geroffss/svg-companion/releases/latest
    ↓
GitHub responds with:
  - tag_name: "v1.0.1"
  - body: "Release notes here"
  - assets: [{download_url: "https://github.com/.../releases/download/..."}]
    ↓
App compares versions:
  Local: 1.0.0
  GitHub: 1.0.1
  Result: Update available!
    ↓
Show notification to user
```

## File Locations

**Current version:** `version.json` (root folder)
```json
{
  "version": "1.0.0"
}
```

**Update script:** `update-installer.bat` (created during update)

**Downloaded installer:** `%TEMP%\Servicegest-Companion-Update.exe`

## For Developers

### Enable Debug Mode

Edit `GitHubReleaseChecker.java` and uncomment:
```java
System.out.println("[GitHubReleaseChecker] Response preview: " + 
    jsonResponse.substring(0, Math.min(500, jsonResponse.length())));
```

This shows full API response in console.

### Test GitHub API Directly

```powershell
$response = Invoke-WebRequest -Uri "https://api.github.com/repos/geroffss/svg-companion/releases/latest" -UseBasicParsing
$response.Content | ConvertFrom-Json | Select-Object tag_name, body
```

Should return:
```
tag_name body
-------- ----
v1.0.0   Version 1.0.0 - Initial release
```
