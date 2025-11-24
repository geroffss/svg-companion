# GitLab Setup for OTA Updates

## Quick Setup Commands

```powershell
# 1. Add GitLab remote
git remote add gitlab https://gitlab.com/YOUR-USERNAME/companion-app.git

# 2. Push to GitLab
git push gitlab main

# 3. When releasing a new version:
git tag -a v1.0.1 -m "Version 1.0.1"
git push gitlab main --tags
```

## Version.json Example for GitLab

Host this file on your web server or GitLab Pages:

```json
{
  "version": "1.0.1",
  "releaseNotes": "• Bug fixes\n• Performance improvements",
  "downloadUrl": "https://gitlab.com/YOUR-USERNAME/companion-app/-/releases/v1.0.1/downloads/companion-app.jar",
  "mandatory": false,
  "minVersion": "1.0.0"
}
```

## Steps to Release

1. Update `CURRENT_VERSION` in `UpdateManager.java`
2. Run `.\build-all.bat`
3. Commit and tag: `git tag -a v1.0.1 -m "Version 1.0.1"`
4. Push: `git push gitlab main --tags`
5. Go to GitLab → Deploy → Releases → New Release
6. Upload `out\companion-app.jar`
7. Update `version.json` on your server
8. Done! Users will auto-update

## Example UpdateManager.java Configuration

```java
// Point to your hosted version.json
private static final String UPDATE_CHECK_URL = "https://yourdomain.com/version.json";

// Or use GitLab Pages:
private static final String UPDATE_CHECK_URL = "https://YOUR-USERNAME.gitlab.io/companion-app/version.json";

// Current version - update this with each release
private static final String CURRENT_VERSION = "1.0.0";
```
