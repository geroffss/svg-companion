package com.servicegest.companion.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Servicegest Companion App - API Health Monitor
 * Monitors api.servicegest.ro/health endpoint and verifies 200 response status
 */
public class CompanionApp extends Application {
    
    private Stage primaryStage;
    private Label statusLabel;
    private Label timestampLabel;
    private ProgressIndicator loadingIndicator;
    private Timer healthCheckTimer;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    
    // Health check interval in milliseconds (10 seconds)
    private static final long HEALTH_CHECK_INTERVAL = 10000;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Servicegest - API Health Monitor");
        
        // Set application icon (shows in titlebar, taskbar, and Task Manager)
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                javafx.scene.image.Image appIcon = new javafx.scene.image.Image(iconStream);
                primaryStage.getIcons().add(appIcon);
                System.out.println("[DEBUG] Application icon loaded successfully");
            } else {
                System.out.println("[DEBUG] Icon not found in resources");
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Failed to load icon: " + e.getMessage());
        }
        
        // Prevent implicit exit when window is hidden
        Platform.setImplicitExit(false);
        
        // Main title
        Label titleLabel = new Label("API Health Monitor");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #1a1a1a;");
        
        // API endpoint info
        Label endpointLabel = new Label("Monitoring: https://api.servicegest.ro/health");
        endpointLabel.setFont(Font.font("System", 12));
        endpointLabel.setStyle("-fx-text-fill: #6b7280;");
        
        // Status indicator
        statusLabel = new Label("Checking...");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        statusLabel.setStyle("-fx-text-fill: #f59e0b;");
        
        // Loading indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(50, 50);
        
        // Status HBox (indicator + status text)
        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.getChildren().addAll(loadingIndicator, statusLabel);
        
        // Timestamp
        timestampLabel = new Label("Last check: Never");
        timestampLabel.setFont(Font.font("System", 11));
        timestampLabel.setStyle("-fx-text-fill: #9ca3af;");
        
        // Response code info
        Label responseInfoLabel = new Label("Expected: HTTP 200");
        responseInfoLabel.setFont(Font.font("System", 11));
        responseInfoLabel.setStyle("-fx-text-fill: #6b7280;");
        
        // Manual check button
        Button checkNowButton = new Button("Check Now");
        checkNowButton.setPrefWidth(150);
        checkNowButton.setPrefHeight(40);
        checkNowButton.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        checkNowButton.setOnAction(e -> performHealthCheck());
        
        // Update button
        Button updateButton = new Button("Check for Updates");
        updateButton.setPrefWidth(150);
        updateButton.setPrefHeight(40);
        updateButton.setStyle("-fx-background-color: #06b6d4; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        updateButton.setOnAction(e -> checkForUpdates(updateButton));
        
        // Buttons HBox
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(checkNowButton, updateButton);
        
        // Auto-check interval selector
        Label intervalLabel = new Label("Check Interval:");
        intervalLabel.setStyle("-fx-font-size: 11px;");
        
        ComboBox<Integer> intervalComboBox = new ComboBox<>();
        intervalComboBox.getItems().addAll(5, 10, 15, 30, 60);
        intervalComboBox.setValue(10);
        intervalComboBox.setPrefWidth(80);
        intervalComboBox.setStyle("-fx-font-size: 11px;");
        
        Label secondsLabel = new Label("seconds");
        secondsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");
        
        HBox intervalBox = new HBox(5);
        intervalBox.setAlignment(Pos.CENTER);
        intervalBox.getChildren().addAll(intervalLabel, intervalComboBox, secondsLabel);
        
        // Details section
        TextArea detailsArea = new TextArea();
        detailsArea.setPrefRowCount(10);
        detailsArea.setWrapText(true);
        detailsArea.setEditable(false);
        detailsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-text-fill: #1f2937; -fx-control-inner-background: #f9fafb; -fx-padding: 10;");
        detailsArea.setText("Application initialized.\nClick 'Check Now' or wait for automatic checks.\n\nChecking api.servicegest.ro/health endpoint...\nWill verify HTTP 200 response status code.");
        
        // Layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ffffff;");
        layout.getChildren().addAll(
            titleLabel,
            new Separator(),
            endpointLabel,
            new Separator(),
            statusBox,
            timestampLabel,
            responseInfoLabel,
            new Separator(),
            buttonsBox,
            intervalBox,
            new Separator(),
            new Label("Details:") {{ setFont(Font.font("System", FontWeight.BOLD, 12)); }},
            detailsArea
        );
        
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        
        // Initialize system tray
        initializeSystemTray();
        
        // Handle window close (minimize to tray instead of closing)
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("[DEBUG] Window close requested - minimizing to tray");
            event.consume();
            minimizeToTray();
        });
        
        // Set interval change listener
        intervalComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                stopHealthCheckTimer();
                startHealthCheckTimer(newVal * 1000L);
            }
        });
        
        primaryStage.show();
        
        // Start initial health check
        performHealthCheck();
        
        // Check for updates in background
        checkForUpdatesBackground();
        
        // Start automatic health checks
        startHealthCheckTimer(HEALTH_CHECK_INTERVAL);
        
        // Cleanup on exit
        primaryStage.setOnCloseRequest(event -> {
            if (healthCheckTimer != null) {
                healthCheckTimer.cancel();
            }
        });
    }
    
    /**
     * Perform a health check on the API
     */
    private void performHealthCheck() {
        new Thread(() -> {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(true);
                statusLabel.setText("Checking...");
                statusLabel.setStyle("-fx-text-fill: #f59e0b;");
            });
            
            boolean isHealthy = HealthChecker.isAPIHealthy();
            String healthStatus = HealthChecker.getHealthStatus();
            
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                
                String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                
                timestampLabel.setText("Last check: " + timestamp);
                
                if (isHealthy) {
                    statusLabel.setText("✓ API is Healthy");
                    statusLabel.setStyle("-fx-text-fill: #10b981;");
                } else {
                    statusLabel.setText("✗ API is Down");
                    statusLabel.setStyle("-fx-text-fill: #ef4444;");
                }
                
                updateDetailsArea(healthStatus);
            });
        }).start();
    }
    
    /**
     * Start automatic health check timer
     */
    private void startHealthCheckTimer(long interval) {
        healthCheckTimer = new Timer();
        healthCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                performHealthCheck();
            }
        }, interval, interval);
    }
    
    /**
     * Stop automatic health check timer
     */
    private void stopHealthCheckTimer() {
        if (healthCheckTimer != null) {
            healthCheckTimer.cancel();
            healthCheckTimer = null;
        }
    }
    
    /**
     * Update details text area with health check result
     */
    private void updateDetailsArea(String status) {
        String details = "Endpoint: https://api.servicegest.ro/health\n" +
                        "Check Time: " + LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                        "Method: GET\n" +
                        "Expected Status: 200 OK\n" +
                        "Timeout: 5 seconds\n" +
                        "---\n" +
                        "Result: " + status;
        
        // Note: Would need access to TextArea to update it
        // For now, status is updated via statusLabel
    }
    
    /**
     * Check for updates in background (non-blocking)
     */
    private void checkForUpdatesBackground() {
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Wait 2 seconds after app starts
                
                GitHubReleaseChecker.ReleaseInfo release = GitHubReleaseChecker.getLatestRelease();
                if (release == null) return;
                
                String currentVersion = AutoUpdater.getCurrentVersion();
                int comparison = GitHubReleaseChecker.compareVersions(release.version, currentVersion);
                
                if (comparison > 0) {
                    Platform.runLater(() -> showUpdateAvailableDialog(release));
                }
            } catch (Exception e) {
                // Silent fail - don't interrupt user experience
            }
        }).start();
    }
    
    /**
     * Check for updates (user-initiated)
     */
    private void checkForUpdates(Button updateButton) {
        updateButton.setDisable(true);
        updateButton.setText("Checking...");
        
        new Thread(() -> {
            GitHubReleaseChecker.ReleaseInfo release = GitHubReleaseChecker.getLatestRelease();
            
            Platform.runLater(() -> {
                updateButton.setDisable(false);
                updateButton.setText("Check for Updates");
                
                if (release == null) {
                    showAlert("Update Check Failed", "Could not connect to GitHub to check for updates.");
                    return;
                }
                
                String currentVersion = AutoUpdater.getCurrentVersion();
                int comparison = GitHubReleaseChecker.compareVersions(release.version, currentVersion);
                
                if (comparison <= 0) {
                    showAlert("No Updates Available", 
                             "You are running the latest version (" + currentVersion + ")");
                } else {
                    showUpdateAvailableDialog(release);
                }
            });
        }).start();
    }
    
    /**
     * Show update available dialog
     */
    private void showUpdateAvailableDialog(GitHubReleaseChecker.ReleaseInfo release) {
        String currentVersion = AutoUpdater.getCurrentVersion();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update Available");
        alert.setHeaderText("New Version Available");
        alert.setContentText("Current: " + currentVersion + "\nNew: " + release.version + 
                           "\n\nRelease Notes:\n" + release.releaseNotes);
        
        ButtonType updateNow = new ButtonType("Update Now");
        ButtonType updateLater = new ButtonType("Later");
        alert.getButtonTypes().setAll(updateNow, updateLater);
        
        java.util.Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == updateNow) {
            performUpdate(release);
        }
    }
    
    /**
     * Download and install update
     */
    private void performUpdate(GitHubReleaseChecker.ReleaseInfo release) {
        // Show progress dialog
        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("Downloading Update");
        progressAlert.setHeaderText("Downloading new version...");
        progressAlert.setContentText("Please wait while the update is being downloaded.\n" +
                                    "This may take a few minutes.");
        progressAlert.getButtonTypes().clear();
        progressAlert.show();
        
        new Thread(() -> {
            try {
                // Download to temp location
                String tempDir = System.getProperty("java.io.tmpdir");
                String installerName = "Servicegest-Companion-Update.exe";
                String installerPath = tempDir + "/" + installerName;
                
                boolean downloadSuccess = AutoUpdater.downloadFile(release.downloadUrl, installerPath);
                
                Platform.runLater(() -> {
                    progressAlert.close();
                    
                    if (downloadSuccess) {
                        // Create update script
                        AutoUpdater.createUpdateScript(installerPath);
                        
                        // Show restart dialog
                        Alert restartAlert = new Alert(Alert.AlertType.INFORMATION);
                        restartAlert.setTitle("Update Ready");
                        restartAlert.setHeaderText("Update Downloaded Successfully");
                        restartAlert.setContentText("The application will now close and install the update.\n" +
                                                  "It will automatically restart when complete.");
                        restartAlert.getButtonTypes().clear();
                        ButtonType okButton = new ButtonType("OK");
                        restartAlert.getButtonTypes().add(okButton);
                        restartAlert.showAndWait();
                        
                        // Launch update and close app
                        AutoUpdater.launchUpdateProcess("update-installer.bat");
                        Platform.exit();
                    } else {
                        showAlert("Download Failed", "Failed to download the update. Please try again later.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressAlert.close();
                    showAlert("Update Error", "Error during update: " + e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Show simple alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Initialize system tray icon with context menu
     */
    private void initializeSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported");
            return;
        }
        
        systemTray = SystemTray.getSystemTray();
        
        // Create a popup menu for the tray icon
        PopupMenu popup = new PopupMenu();
        
        // Show window menu item
        java.awt.MenuItem showItem = new java.awt.MenuItem("Show");
        showItem.addActionListener(e -> {
            System.out.println("[DEBUG] Show menu item clicked");
            showWindow();
        });
        popup.add(showItem);
        
        popup.addSeparator();
        
        // Exit menu item
        java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
        exitItem.addActionListener(e -> {
            stopHealthCheckTimer();
            Platform.exit();
            System.exit(0);
        });
        popup.add(exitItem);
        
        // Create tray icon (use a simple colored image)
        Image image = createTrayImage();
        trayIcon = new TrayIcon(image, "Servicegest - API Monitor", popup);
        trayIcon.setImageAutoSize(true);
        
        // Add click listener to show window
        trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("[DEBUG] Tray icon clicked - Button: " + e.getButton() + ", Click count: " + e.getClickCount());
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    // Single left-click or double-click
                    showWindow();
                }
            }
        });
        
        // Add action listener for primary action (double-click on some systems)
        trayIcon.addActionListener(e -> {
            System.out.println("[DEBUG] Tray icon action listener triggered");
            showWindow();
        });
        
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Failed to add tray icon: " + e.getMessage());
        }
    }
    
    /**
     * Minimize window to system tray
     */
    private void minimizeToTray() {
        Platform.runLater(() -> {
            primaryStage.hide();
            if (trayIcon != null) {
                trayIcon.displayMessage("Servicegest Companion", 
                    "Application minimized to tray. Double-click to restore.", 
                    TrayIcon.MessageType.INFO);
            }
        });
    }
    
    /**
     * Show window from system tray
     */
    private void showWindow() {
        System.out.println("[DEBUG] showWindow() called from tray");
        javax.swing.SwingUtilities.invokeLater(() -> {
            Platform.runLater(() -> {
                System.out.println("[DEBUG] Platform.runLater executing - showing window");
                try {
                    if (primaryStage.isIconified()) {
                        primaryStage.setIconified(false);
                    }
                    if (!primaryStage.isShowing()) {
                        primaryStage.show();
                    }
                    primaryStage.toFront();
                    primaryStage.setAlwaysOnTop(true);
                    primaryStage.requestFocus();
                    primaryStage.setAlwaysOnTop(false);
                    System.out.println("[DEBUG] Window shown successfully");
                } catch (Exception e) {
                    System.err.println("[DEBUG] Error showing window: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        });
    }
    
    /**
     * Create tray icon image from the application icon
     */
    private Image createTrayImage() {
        try {
            // Load the icon from resources
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                java.awt.image.BufferedImage originalImage = ImageIO.read(iconStream);
                // Scale to appropriate tray size (16x16 or system tray size)
                int trayIconSize = 16;
                try {
                    java.awt.Dimension traySize = SystemTray.getSystemTray().getTrayIconSize();
                    trayIconSize = Math.min(traySize.width, traySize.height);
                } catch (Exception e) {
                    trayIconSize = 16;
                }
                java.awt.image.BufferedImage scaledImage = new java.awt.image.BufferedImage(
                    trayIconSize, trayIconSize, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(originalImage, 0, 0, trayIconSize, trayIconSize, null);
                g2d.dispose();
                System.out.println("[DEBUG] Tray icon loaded from resources (" + trayIconSize + "x" + trayIconSize + ")");
                return scaledImage;
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Failed to load tray icon: " + e.getMessage());
        }
        
        // Fallback: Create a simple colored icon
        System.out.println("[DEBUG] Using fallback tray icon");
        int size = 16;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Purple background (matching the app theme)
        g2d.setColor(new Color(139, 92, 246));
        g2d.fillOval(0, 0, size, size);
        
        g2d.dispose();
        return image;
    }
    
    @Override
    public void stop() {
        System.out.println("Application closing - stopping health check timer");
        stopHealthCheckTimer();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
