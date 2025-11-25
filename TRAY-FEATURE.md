# System Tray Feature

## Overview
The Servicegest Companion App now includes system tray integration. When you close the application window (click the X button), it minimizes to the system tray instead of completely closing.

## Features

### System Tray Icon
- Green circle icon appears in your Windows system tray (bottom-right of taskbar)
- Indicates the app is still running and monitoring the API
- Health checks continue running in the background

### Right-Click Context Menu
Right-click the tray icon to access:
- **Show** - Restore the application window
- **Exit** - Completely close the application and stop monitoring

### Double-Click to Restore
Double-click the tray icon to quickly restore the application window

## Usage

### Close Window to System Tray
1. Click the X button on the app window
2. The window closes and app minimizes to system tray
3. API health checks continue automatically

### Restore from System Tray
**Option 1 - Double-click tray icon:**
- Double-click the green circle icon in the system tray

**Option 2 - Right-click menu:**
- Right-click the tray icon
- Click "Show"

### Completely Exit the Application
1. Right-click the tray icon in the system tray
2. Click "Exit"
3. Application stops and health checks stop
4. App removes from system tray

## Status Indication

The tray icon shows:
- **Green circle** - App is running, monitoring API health
- **In system tray** - App is minimized but active

The Windows taskbar does NOT show the app window when minimized to tray, keeping your desktop clean while the app monitors in the background.

## Interval Configuration

When the app is minimized to tray, you can still:
- Access it anytime by double-clicking the tray icon
- Check the status
- Change the health check interval
- Perform manual checks

All settings are maintained when minimized.

## Troubleshooting

### Tray Icon Not Visible
- Check Windows system tray (bottom-right of taskbar)
- On Windows 10/11, tray icons may be hidden - click the up arrow to show hidden icons
- The app is still running - you can always click the app in Task Manager to restore it

### App Not Monitoring While Minimized
- The app continues monitoring API health while in system tray
- Health checks occur automatically every 10 seconds (or configured interval)
- Restore the window to see health check status in real-time

### Can't Find the Tray Icon
- Look in the system tray area (clock/date area, bottom-right)
- If system tray icons are hidden, click the up arrow "Show hidden icons"
- The icon appears as a green circle

## Technical Details

- **Tray Icon**: 16x16 pixel green circle
- **Background Monitoring**: Yes - health checks continue while minimized
- **Health Check Interval**: Continues with configured interval (default 10 seconds)
- **System Requirements**: Windows 7 or later with system tray support
- **AWT Integration**: Uses Java AWT SystemTray and TrayIcon classes

## Default Behavior Summary

| Action | Before | Now |
|--------|--------|-----|
| Click X button | App closes | App minimizes to tray |
| Close from tray menu | N/A | App closes completely |
| Double-click tray icon | N/A | Window restores |
| Health checks while hidden | N/A | Continue running |
| API monitoring | Stops on close | Continues in tray |

## System Tray Navigation (Windows 10/11)

1. **Show tray**: Click the up arrow in the bottom-right corner of taskbar
2. **Find green icon**: Look for Servicegest green circle icon
3. **Right-click for menu**: Access Show/Exit options
4. **Double-click to restore**: Quickly bring app back

## Notes

- The application keeps running in the background indefinitely
- Health checks automatically continue at configured interval
- Use "Exit" from tray menu to stop monitoring completely
- Closing the window DOES NOT close the app - minimize to tray only
