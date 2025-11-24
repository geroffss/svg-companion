package com.companion.app;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Properties;

public class UpdateManager {
    
    // GitHub Pages URL for version.json - Update this to your GitHub Pages URL
    // Format: https://YOUR-USERNAME.github.io/svg-companion/version.json
    private static final String UPDATE_CHECK_URL = "https://geroffss.github.io/svg-companion/version.json";
    private static final String CURRENT_VERSION = "1.0.0";
    
    // Note: UPDATE_DOWNLOAD_URL is now read from version.json
    // GitHub Release format: https://github.com/geroffss/svg-companion/releases/download/v1.0.1/companion-app.jar
    
    public static void checkForUpdates(boolean silent) {
        new Thread(() -> {
            try {
                String latestVersion = getLatestVersion();
                
                if (latestVersion != null && isNewerVersion(CURRENT_VERSION, latestVersion)) {
                    Platform.runLater(() -> promptUpdate(latestVersion, silent));
                } else if (!silent) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("No Updates");
                        alert.setHeaderText("You're up to date!");
                        alert.setContentText("Current version: " + CURRENT_VERSION);
                        alert.showAndWait();
                    });
                }
            } catch (Exception e) {
                if (!silent) {
                    System.err.println("Failed to check for updates: " + e.getMessage());
                }
            }
        }).start();
    }
    
    private static String getLatestVersion() {
        try {
            URL url = new URL(UPDATE_CHECK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                // Parse JSON manually (simple parsing)
                String json = response.toString();
                String versionKey = "\"version\":\"";
                int start = json.indexOf(versionKey);
                if (start != -1) {
                    start += versionKey.length();
                    int end = json.indexOf("\"", start);
                    if (end != -1) {
                        return json.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking version: " + e.getMessage());
        }
        return null;
    }
    
    private static boolean isNewerVersion(String current, String latest) {
        String[] currentParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");
        
        int length = Math.max(currentParts.length, latestParts.length);
        for (int i = 0; i < length; i++) {
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            
            if (latestPart > currentPart) {
                return true;
            } else if (latestPart < currentPart) {
                return false;
            }
        }
        return false;
    }
    
    private static String getDownloadUrl() {
        try {
            URL url = new URL(UPDATE_CHECK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                // Parse JSON manually to get downloadUrl
                String json = response.toString();
                String urlKey = "\"downloadUrl\":\"";
                int start = json.indexOf(urlKey);
                if (start != -1) {
                    start += urlKey.length();
                    int end = json.indexOf("\"", start);
                    if (end != -1) {
                        return json.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting download URL: " + e.getMessage());
        }
        return null;
    }
    
    private static void promptUpdate(String newVersion, boolean silent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Available");
        alert.setHeaderText("New version available: " + newVersion);
        alert.setContentText("Current version: " + CURRENT_VERSION + "\n\n" +
                           "Would you like to download and install the update?\n" +
                           "The application will restart after the update.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            downloadAndInstallUpdate(newVersion);
        }
    }
    
    private static void downloadAndInstallUpdate(String version) {
        Platform.runLater(() -> {
            Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
            progressAlert.setTitle("Updating");
            progressAlert.setHeaderText("Downloading update...");
            progressAlert.setContentText("Please wait while the update is downloaded.");
            progressAlert.show();
        });
        
        new Thread(() -> {
            try {
                // First, get the download URL from version.json
                String downloadUrl = getDownloadUrl();
                if (downloadUrl == null) {
                    throw new Exception("Could not retrieve download URL");
                }
                
                // Get the current JAR location
                String jarPath = UpdateManager.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
                
                if (jarPath.startsWith("/")) {
                    jarPath = jarPath.substring(1); // Remove leading slash on Windows
                }
                
                Path currentJar = Paths.get(jarPath).getParent().resolve("companion-app.jar");
                Path backupJar = Paths.get(jarPath).getParent().resolve("companion-app.jar.backup");
                Path tempJar = Paths.get(jarPath).getParent().resolve("companion-app.jar.tmp");
                
                // Download new version from the URL in version.json
                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setInstanceFollowRedirects(true); // Important for GitHub redirects
                
                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(tempJar.toFile())) {
                    
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                
                // Backup current version
                Files.copy(currentJar, backupJar, StandardCopyOption.REPLACE_EXISTING);
                
                // Replace with new version
                Files.move(tempJar, currentJar, StandardCopyOption.REPLACE_EXISTING);
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Update Complete");
                    alert.setHeaderText("Update installed successfully!");
                    alert.setContentText("The application will now restart.");
                    alert.showAndWait();
                    
                    // Restart application
                    restartApplication();
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update Failed");
                    alert.setHeaderText("Failed to install update");
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private static void restartApplication() {
        try {
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            String currentJar = UpdateManager.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
            
            if (currentJar.startsWith("/")) {
                currentJar = currentJar.substring(1);
            }
            
            ProcessBuilder builder = new ProcessBuilder(javaBin, "-jar", currentJar);
            builder.start();
            
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getCurrentVersion() {
        return CURRENT_VERSION;
    }
}
