# Quick Reference: How Auto-Update Works

## Simple Explanation

**The app checks GitHub for new versions and can download + install updates automatically.**

Think of it like:
1. App says: "What's the latest version on GitHub?"
2. GitHub responds: "The latest is v1.0.1, here's the link to download"
3. App compares: "I have v1.0.0, so v1.0.1 is newer"
4. App asks you: "Update now?"
5. You click "Yes"
6. App downloads `.exe` from GitHub
7. Installer runs automatically
8. App restarts with new version

## What You Need to Do

### To Make Auto-Update Work

1. **Push your code to GitHub** (you already did this ✓)

2. **Create a GitHub Release:**
   - Go to: https://github.com/geroffss/svg-companion/releases/new
   - Tag: v1.0.1 (or v2.0.0, etc.)
   - Title: Version 1.0.1
   - Description: Write what's new
   - **Upload the .exe file** from `installer-output/`
   - Click "Publish release"

3. **Update version.json** (in your repo root):
   ```json
   {
     "version": "1.0.1",
     "releaseNotes": "Bug fixes and improvements"
   }
   ```

4. **That's it!** Now users with v1.0.0 will see "Update available"

### When You Release a New Version

```
1. Bump version in version.json (1.0.0 → 1.0.1)
2. Build: mvn clean package
3. Create installer: iscc.exe setup.iss
4. Go to GitHub → New Release
5. Tag: v1.0.1
6. Upload .exe from installer-output/
7. Publish
```

Users will be notified automatically within minutes!

## What Happens in the App

### When User Starts App
- Automatically checks GitHub (after 2 seconds)
- If new version found: "Update available" dialog appears
- User can click "Update Now" or "Later"

### When User Clicks "Check for Updates"
- Shows current version
- Fetches latest from GitHub
- If new: shows release notes
- User chooses to update or skip

### When User Updates
- Downloads .exe to temp folder
- Runs installer
- App restarts with new version

## Files Involved

**New files created:**
- `GitHubReleaseChecker.java` - Talks to GitHub API
- `AutoUpdater.java` - Downloads and installs
- `AUTO-UPDATE-GUIDE.md` - This documentation

**Modified files:**
- `CompanionApp.java` - Added update button and checks
- `version.json` - Current version (you update this)

## How It Uses GitHub

The app makes ONE HTTP GET request to GitHub API:
```
https://api.github.com/repos/geroffss/svg-companion/releases/latest
```

It gets back JSON with:
- Latest version number
- Release notes
- Download link to .exe file

**No authentication needed** - public repository!

## Testing It

### Test Update Check (Without Real Update)
1. Run app
2. Click "Check for Updates" button
3. Should say "You're running latest version" (since no new release yet)

### Test Real Update
1. Create a v1.0.1 release on GitHub (with new .exe)
2. Run app with v1.0.0
3. Should see "Update available" dialog
4. Click "Update Now"
5. Should download and restart

## FAQ

**Q: Do I need to do anything special to enable auto-update?**
A: No! It's already built in. Just create GitHub releases normally.

**Q: Will users automatically update?**
A: No. They'll be notified and can choose "Update Now" or "Later".

**Q: Can I force users to update?**
A: Not yet. The feature supports "mandatory" flag but it's not enforced.

**Q: What if download fails?**
A: User sees error message. They can try again later.

**Q: Does the app need to restart?**
A: Yes, installer needs to replace the .exe file.

**Q: Will it work on old versions?**
A: Only versions with this code (v1.0.0 with auto-update feature).

## What GitHub API It Calls

The app calls the GitHub REST API **once per startup**:

```
GET /repos/geroffss/svg-companion/releases/latest
```

Response includes:
```json
{
  "tag_name": "v1.0.1",
  "body": "Release notes here",
  "assets": [
    {
      "name": "Servicegest-Companion-1.0.1.exe",
      "browser_download_url": "https://github.com/.../releases/download/v1.0.1/..."
    }
  ]
}
```

The app extracts:
- Version from `tag_name` (removes "v" prefix)
- Release notes from `body`
- Download URL from `assets[0].browser_download_url`

## Next Steps

1. ✓ Code written and built
2. ✓ Installer updated with auto-update feature
3. **Next:** Create a GitHub Release with v1.0.1
4. **Then:** Users with v1.0.0 will see update notification

That's all there is to it!
