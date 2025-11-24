# OTA (Over-The-Air) Update System Documentation

## How It Works

The app checks `http://localhost:3000/api/version.json` for updates on startup and via system tray menu.

## Server Setup

### 1. Create version.json endpoint
```json
{
  "version": "1.0.1",
  "releaseNotes": "Bug fixes",
  "downloadUrl": "http://localhost:3000/api/download/companion-app-1.0.1.jar"
}
```

### 2. Host JAR files at /api/download/

### 3. Example Express Server
```javascript
app.get('/api/version.json', (req, res) => {
  res.json({ version: '1.0.1' });
});

app.get('/api/download/:filename', (req, res) => {
  res.download('./releases/' + req.params.filename);
});
```

## Update URLs

Change in `UpdateManager.java`:
```java
private static final String UPDATE_CHECK_URL = "https://yourdomain.com/api/version.json";
```

## Features

- ✅ Automatic check on startup
- ✅ Manual check via tray menu
- ✅ Downloads and installs automatically
- ✅ Auto-restart after update
- ✅ Backup of previous version

## Deployment

1. Build new version → Update version number in UpdateManager.java
2. Upload JAR to server
3. Update version.json
4. Users get notified automatically
