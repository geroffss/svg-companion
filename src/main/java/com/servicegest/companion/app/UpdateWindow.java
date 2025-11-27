package com.servicegest.companion.app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.InputStream;

/**
 * Modern update progress window - replaces CMD window during updates
 */
public class UpdateWindow {
    
    private Stage stage;
    private Label statusLabel;
    private Label detailLabel;
    private ProgressBar progressBar;
    private ProgressIndicator spinner;
    
    // Windows 11 colors
    private static final String BG_COLOR = "#f5f5f5";
    private static final String CARD_BG = "#ffffff";
    private static final String ACCENT_COLOR = "#0078d4";
    private static final String TEXT_PRIMARY = "#1a1a1a";
    private static final String TEXT_SECONDARY = "#5c5c5c";
    private static final String BORDER_COLOR = "#e5e5e5";
    
    public UpdateWindow() {
        createWindow();
    }
    
    private void createWindow() {
        stage = new Stage();
        stage.setTitle("Updating Servicegest Companion");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        
        // Try to set icon
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                stage.getIcons().add(new javafx.scene.image.Image(iconStream));
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Header with app name
        Label titleLabel = new Label("Servicegest Companion");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        // Status text
        statusLabel = new Label("Preparing update...");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        statusLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        // Detail text
        detailLabel = new Label("Please wait");
        detailLabel.setFont(Font.font("Segoe UI", 12));
        detailLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");
        
        // Progress bar
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(350);
        progressBar.setPrefHeight(6);
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressBar.setStyle("-fx-accent: " + ACCENT_COLOR + ";");
        
        // Spinner
        spinner = new ProgressIndicator();
        spinner.setPrefSize(24, 24);
        spinner.setStyle("-fx-progress-color: " + ACCENT_COLOR + ";");
        
        // Status row with spinner
        HBox statusRow = new HBox(12);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        statusRow.getChildren().addAll(spinner, statusLabel);
        
        // Main layout
        VBox content = new VBox(16);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color: " + CARD_BG + "; " +
                        "-fx-border-color: " + BORDER_COLOR + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);");
        content.getChildren().addAll(titleLabel, statusRow, progressBar, detailLabel);
        
        Scene scene = new Scene(content, 420, 160);
        stage.setScene(scene);
        
        // Center on screen
        stage.centerOnScreen();
    }
    
    public void show() {
        Platform.runLater(() -> stage.show());
    }
    
    public void close() {
        Platform.runLater(() -> stage.close());
    }
    
    public void setStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }
    
    public void setDetail(String detail) {
        Platform.runLater(() -> detailLabel.setText(detail));
    }
    
    public void setProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }
    
    /**
     * Run the update installation process with visual feedback
     */
    public void runUpdateInstallation(String installerPath, Runnable onComplete) {
        new Thread(() -> {
            try {
                // Step 1: Preparing
                setStatus("Preparing installation...");
                setDetail("Closing application components");
                setProgress(-1); // Indeterminate
                Thread.sleep(1000);
                
                // Normalize path
                String installerPathNormalized = installerPath.replace("/", "\\");
                File installerFile = new File(installerPathNormalized);
                
                System.out.println("[UpdateWindow] Installer path: " + installerPathNormalized);
                System.out.println("[UpdateWindow] Installer exists: " + installerFile.exists());
                System.out.println("[UpdateWindow] Installer size: " + installerFile.length() + " bytes");
                
                if (!installerFile.exists()) {
                    setStatus("Installation failed");
                    setDetail("Installer file not found");
                    Thread.sleep(3000);
                    Platform.runLater(() -> close());
                    return;
                }
                
                // Step 2: Running installer
                setStatus("Installing update...");
                setDetail("Running installer (this may take a moment)");
                setProgress(-1);
                
                // Run the installer - use /SILENT instead of /VERYSILENT to see any errors
                // Also add /LOG to create a log file
                String logFile = System.getProperty("java.io.tmpdir") + "\\servicegest-update.log";
                ProcessBuilder pb = new ProcessBuilder(
                    installerPathNormalized, 
                    "/SILENT",      // Silent but shows progress
                    "/NORESTART",
                    "/LOG=" + logFile
                );
                pb.redirectErrorStream(true);
                
                System.out.println("[UpdateWindow] Starting installer process...");
                Process process = pb.start();
                
                // Read any output from the process
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Installer] " + line);
                }
                
                // Wait for installer to complete
                int exitCode = process.waitFor();
                System.out.println("[UpdateWindow] Installer exit code: " + exitCode);
                
                if (exitCode == 0) {
                    // Step 3: Finishing up
                    setStatus("Update complete!");
                    setDetail("The application will restart automatically...");
                    setProgress(1.0);
                    Thread.sleep(2000);
                    
                    // The installer handles launching the app (postinstall in installer.iss)
                    // Just close this window and exit
                    Platform.runLater(() -> {
                        close();
                        if (onComplete != null) onComplete.run();
                    });
                    
                } else {
                    setStatus("Installation may have failed");
                    setDetail("Exit code: " + exitCode + " - Check log: " + logFile);
                    System.out.println("[UpdateWindow] Installation failed. Check log at: " + logFile);
                    Thread.sleep(5000);
                    Platform.runLater(() -> close());
                }
                
                // Clean up installer file
                try {
                    new File(installerPath).delete();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
                
            } catch (Exception e) {
                System.out.println("[UpdateWindow] Exception during update: " + e.getMessage());
                e.printStackTrace();
                
                Platform.runLater(() -> {
                    setStatus("Update failed");
                    setDetail("Error: " + e.getMessage());
                });
                
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    // Ignore
                }
                
                Platform.runLater(() -> close());
            }
        }).start();
    }
    
    /**
     * Find and launch the updated application
     * @return true if successfully launched
     */
    private boolean launchUpdatedApp() {
        try {
            String localAppData = System.getenv("LOCALAPPDATA");
            String programFiles = System.getenv("ProgramFiles");
            String appDir = System.getProperty("user.dir");
            String userProfile = System.getenv("USERPROFILE");
            
            // List of possible installation paths
            String[] possiblePaths = {
                // Primary installation path
                localAppData + "\\ServicegestCompanion\\ServicegestCompanion.bat",
                
                // Alternative paths
                localAppData + "\\Programs\\Servicegest\\Companion\\run-launcher.bat",
                programFiles + "\\Servicegest\\Companion\\run-launcher.bat",
                
                // Current directory (dev mode)
                appDir + "\\run-launcher.bat",
                appDir + "\\ServicegestCompanion.bat",
                
                // Start Menu shortcuts (fallback)
                userProfile + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\ServicegestCompanion\\ServicegestCompanion.lnk",
                userProfile + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\ServicegestCompanion\\Servicegest - Companion.lnk"
            };
            
            System.out.println("[UpdateWindow] Looking for updated application...");
            
            for (String path : possiblePaths) {
                if (path == null) continue;
                
                File file = new File(path);
                System.out.println("[UpdateWindow] Checking: " + path + " -> " + file.exists());
                
                if (file.exists()) {
                    // Use ProcessBuilder with proper arguments for detached process
                    ProcessBuilder pb = new ProcessBuilder(
                        "cmd", "/c", "start", "\"\"", "/D", file.getParent(), "\"" + path + "\""
                    );
                    
                    System.out.println("[UpdateWindow] Launching from: " + file.getParent());
                    pb.start();
                    
                    // Give it time to start
                    Thread.sleep(2000);
                    
                    System.out.println("[UpdateWindow] Successfully launched: " + path);
                    return true;
                }
            }
            
            System.out.println("[UpdateWindow] Could not find updated application to launch");
            return false;
            
        } catch (Exception e) {
            System.out.println("[UpdateWindow] Error launching updated app: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
