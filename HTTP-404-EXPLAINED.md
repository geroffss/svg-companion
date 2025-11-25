# What the "404" Error Means

## TL;DR

The app shows `HTTP Response: 404` and "No releases found on GitHub repository" because:

**There are no releases on GitHub yet. This is EXPECTED and NORMAL.**

## What's Actually Happening

When you click "Check for Updates", the app does this:

```
1. App calls: api.github.com/repos/geroffss/svg-companion/releases/latest
2. GitHub responds: "I don't have any releases for that repo" (404)
3. App says: "No releases found"
```

**This is the correct behavior!** The app is working perfectly.

## What 404 Means

| Code | Meaning | Status |
|------|---------|--------|
| 200 | Update found, here's the data | ✅ App working, release exists |
| 404 | No releases exist | ✅ App working, no releases yet |
| 403 | Rate limited | ⚠️ Too many requests, wait |
| Network error | Can't reach GitHub | ❌ Internet/firewall issue |

## How to Fix It (Enable Auto-Updates)

### Create Your First Release

1. Go to: https://github.com/geroffss/svg-companion/releases/new

2. Fill in:
   - **Tag version:** `v1.0.0`
   - **Release title:** `Version 1.0.0`
   - **Description:** `Initial release with API health monitoring`

3. **Upload the installer:**
   - Drag-and-drop or click "Attach binaries"
   - Select: `installer-output/Servicegest-Companion-1.0.0.exe`

4. Click **"Publish release"**

### Now Test It

1. Close the app
2. Start the app again
3. Click "Check for Updates"

**You should now see:**
```
Version 1.0.0 is available
(instead of "No releases found")
```

## Why 404 is Actually Good

✅ App connected to GitHub successfully  
✅ GitHub is responding  
✅ App parsed the response correctly  
✅ App understood "no releases"  

The 404 proves everything is working! You just need to create a release.

## Future Versions

When you want to release v1.0.1:

1. Update `version.json`: `"version": "1.0.1"`
2. Build: `mvn clean package`
3. Create installer
4. Go to GitHub → Create release v1.0.1
5. Upload new .exe file
6. Publish

Then users with v1.0.0 will see:
```
Version 1.0.1 is available
Update now?
```

## Diagram

```
No Release on GitHub:
app.github.com/releases/latest
        ↓
      404 (Not Found)
        ↓
    App says: "No releases found"
        ↓
    This is CORRECT ✅

After You Create Release:
app.github.com/releases/latest
        ↓
      200 (Success)
      {version: "1.0.0", ...}
        ↓
    App says: "Version 1.0.0 found"
        ↓
    This is ALSO CORRECT ✅
```

## Bottom Line

**The 404 error is not an error. It's the app correctly detecting that no releases exist yet.**

Once you create a GitHub release, the 404 will disappear and users will see update notifications.
