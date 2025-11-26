package com.servicegest.companion.app;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Auto-Updater - Handles downloading and installing updates
 */
public class AutoUpdater {
    
    /**
     * Download file from URL to destination
     * @return true if successful
     */
    public static boolean downloadFile(String urlString, String destinationPath) {
        try {
            System.out.println("[AutoUpdater] Downloading from: " + urlString);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            
            // Handle redirects (GitHub uses redirects for release downloads)
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || 
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String redirectUrl = conn.getHeaderField("Location");
                conn = (HttpURLConnection) new URL(redirectUrl).openConnection();
            }
            
            if (conn.getResponseCode() != 200) {
                System.out.println("[AutoUpdater] Download failed: HTTP " + conn.getResponseCode());
                return false;
            }
            
            long fileSize = conn.getContentLengthLong();
            System.out.println("[AutoUpdater] File size: " + (fileSize / 1024 / 1024) + " MB");
            
            // Download file
            try (InputStream input = conn.getInputStream();
                 FileOutputStream output = new FileOutputStream(destinationPath)) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;
                
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    
                    // Print progress
                    if (fileSize > 0) {
                        int progress = (int) ((totalRead * 100) / fileSize);
                        if (progress % 10 == 0) {
                            System.out.println("[AutoUpdater] Download progress: " + progress + "%");
                        }
                    }
                }
            }
            
            System.out.println("[AutoUpdater] Download complete: " + destinationPath);
            return true;
            
        } catch (Exception e) {
            System.out.println("[AutoUpdater] Download failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current application version from version.json
     */
    public static String getCurrentVersion() {
        try {
            String versionFile = "version.json";
            if (!Files.exists(Paths.get(versionFile))) {
                return "0.0.0";
            }
            
            String content = new String(Files.readAllBytes(Paths.get(versionFile)));
            // Extract version field: "version": "1.0.0"
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"version\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher matcher = pattern.matcher(content);
            
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.out.println("[AutoUpdater] Error reading version: " + e.getMessage());
        }
        return "0.0.0";
    }
    
    /**
     * Check if an update is available
     */
    public static boolean isUpdateAvailable() {
        String currentVersion = getCurrentVersion();
        GitHubReleaseChecker.ReleaseInfo release = GitHubReleaseChecker.getLatestRelease();
        
        if (release == null) {
            System.out.println("[AutoUpdater] Could not check for updates");
            return false;
        }
        
        int comparison = GitHubReleaseChecker.compareVersions(release.version, currentVersion);
        boolean available = comparison > 0;
        
        System.out.println("[AutoUpdater] Current: " + currentVersion + ", Latest: " + release.version + 
                         ", Update available: " + available);
        
        return available;
    }
    
    /**
     * Create batch script to install update
     */
    public static void createUpdateScript(String newInstallerPath) {
        try {
            // Use temp directory for the update script (ensure we have write access)
            String tempDir = System.getProperty("java.io.tmpdir");
            String scriptPath = tempDir + File.separator + "servicegest-update-installer.bat";
            
            // Normalize path separators for Windows
            String installerPathNormalized = newInstallerPath.replace("/", "\\");
            
            // Get the current app directory (where the JAR is running from)
            String appDir = System.getProperty("user.dir");
            String appDirNormalized = appDir.replace("/", "\\");
            
            String batchContent = "@echo off\r\n" +
                "REM Update installer script\r\n" +
                "setlocal enabledelayedexpansion\r\n" +
                "\r\n" +
                "echo Waiting for application to close...\r\n" +
                "timeout /t 3 /nobreak > nul\r\n" +
                "\r\n" +
                "echo Running installer...\r\n" +
                "\"" + installerPathNormalized + "\" /VERYSILENT /NORESTART\r\n" +
                "\r\n" +
                "echo Launching updated application...\r\n" +
                "\r\n" +
                "REM Try the original app directory first\r\n" +
                "if exist \"" + appDirNormalized + "\\run-launcher.bat\" (\r\n" +
                "    cd /d \"" + appDirNormalized + "\"\r\n" +
                "    call run-launcher.bat\r\n" +
                "    goto :cleanup\r\n" +
                ")\r\n" +
                "if exist \"" + appDirNormalized + "\\ServicegestCompanion.bat\" (\r\n" +
                "    cd /d \"" + appDirNormalized + "\"\r\n" +
                "    call ServicegestCompanion.bat\r\n" +
                "    goto :cleanup\r\n" +
                ")\r\n" +
                "\r\n" +
                "REM Try LocalAppData\\ServicegestCompanion path (old installer)\r\n" +
                "if exist \"%LocalAppData%\\ServicegestCompanion\\ServicegestCompanion.bat\" (\r\n" +
                "    cd /d \"%LocalAppData%\\ServicegestCompanion\"\r\n" +
                "    call ServicegestCompanion.bat\r\n" +
                "    goto :cleanup\r\n" +
                ")\r\n" +
                "\r\n" +
                "REM Try LocalAppData\\Programs path (new installer non-admin)\r\n" +
                "if exist \"%LocalAppData%\\Programs\\Servicegest\\Companion\\run-launcher.bat\" (\r\n" +
                "    cd /d \"%LocalAppData%\\Programs\\Servicegest\\Companion\"\r\n" +
                "    call run-launcher.bat\r\n" +
                "    goto :cleanup\r\n" +
                ")\r\n" +
                "\r\n" +
                "REM Try Program Files path (admin install)\r\n" +
                "if exist \"%ProgramFiles%\\Servicegest\\Companion\\run-launcher.bat\" (\r\n" +
                "    cd /d \"%ProgramFiles%\\Servicegest\\Companion\"\r\n" +
                "    call run-launcher.bat\r\n" +
                "    goto :cleanup\r\n" +
                ")\r\n" +
                "\r\n" +
                "echo Error: Could not find updated application\r\n" +
                "pause\r\n" +
                "\r\n" +
                ":cleanup\r\n" +
                "REM Clean up installer\r\n" +
                "del /f /q \"" + installerPathNormalized + "\" 2>nul\r\n";
            
            Files.write(Paths.get(scriptPath), batchContent.getBytes());
            System.out.println("[AutoUpdater] Update script created: " + scriptPath);
            System.out.println("[AutoUpdater] App directory: " + appDirNormalized);
            
        } catch (Exception e) {
            System.out.println("[AutoUpdater] Error creating update script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Launch update process in background
     */
    public static void launchUpdateProcess(String updateScriptPath) {
        try {
            // Use the script from temp directory
            String tempDir = System.getProperty("java.io.tmpdir");
            String scriptPath = tempDir + File.separator + "servicegest-update-installer.bat";
            
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "\"Updating...\"", scriptPath);
            pb.start();
            System.out.println("[AutoUpdater] Update process launched: " + scriptPath);
        } catch (Exception e) {
            System.out.println("[AutoUpdater] Error launching update: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
