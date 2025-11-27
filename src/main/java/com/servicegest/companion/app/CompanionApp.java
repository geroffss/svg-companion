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
import java.util.List;
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
    
    // USB Device UI components
    private ComboBox<USBDeviceDetector.USBDevice> usbDeviceComboBox;
    private Label usbStatusLabel;
    private TextArea usbDetailsArea;
    
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
        detailsArea.setPrefRowCount(6);
        detailsArea.setWrapText(true);
        detailsArea.setEditable(false);
        detailsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-text-fill: #1f2937; -fx-control-inner-background: #f9fafb; -fx-padding: 10;");
        detailsArea.setText("Application initialized.\nClick 'Check Now' or wait for automatic checks.\n\nChecking api.servicegest.ro/health endpoint...\nWill verify HTTP 200 response status code.");
        
        // ========== USB DEVICE SECTION ==========
        Label usbSectionTitle = new Label("USB Device Selection");
        usbSectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        usbSectionTitle.setStyle("-fx-text-fill: #1a1a1a;");
        
        // USB status label
        usbStatusLabel = new Label("Scanning for USB devices...");
        usbStatusLabel.setFont(Font.font("System", 12));
        usbStatusLabel.setStyle("-fx-text-fill: #6b7280;");
        
        // USB device combo box
        usbDeviceComboBox = new ComboBox<>();
        usbDeviceComboBox.setPrefWidth(300);
        usbDeviceComboBox.setPromptText("Select a USB device...");
        usbDeviceComboBox.setStyle("-fx-font-size: 13px;");
        
        // Refresh USB button
        Button refreshUsbButton = new Button("Refresh");
        refreshUsbButton.setPrefHeight(30);
        refreshUsbButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
        refreshUsbButton.setOnAction(e -> refreshUSBDevices());
        
        // Open Device Manager button
    
        HBox usbSelectBox = new HBox(10);
        usbSelectBox.setAlignment(Pos.CENTER);
        usbSelectBox.getChildren().addAll(usbDeviceComboBox, refreshUsbButton);
        
        // USB device details area
        usbDetailsArea = new TextArea();
        usbDetailsArea.setPrefRowCount(6);
        usbDetailsArea.setWrapText(true);
        usbDetailsArea.setEditable(false);
        usbDetailsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-text-fill: #1f2937; -fx-control-inner-background: #f0fdf4; -fx-padding: 10;");
        usbDetailsArea.setText("No USB device selected.\nClick 'Refresh' to scan for connected USB drives.");
        
        // USB device selection listener
        usbDeviceComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayUSBDeviceDetails(newVal);
            }
        });
        
        // ========== END USB DEVICE SECTION ==========
        
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
            // new Label("API Details:") {{ setFont(Font.font("System", FontWeight.BOLD, 12)); }},
            // detailsArea,
            // new Separator(),
            usbSectionTitle,
            usbStatusLabel,
            usbSelectBox,
            new Label("Device Details:") {{ setFont(Font.font("System", FontWeight.BOLD, 12)); }},
            usbDetailsArea
        );
        
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane, 650, 850);
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
        
        // Initial USB device scan
        refreshUSBDevices();
        
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
     * Refresh the list of USB devices
     */
    private void refreshUSBDevices() {
        usbStatusLabel.setText("Scanning for USB devices...");
        usbStatusLabel.setStyle("-fx-text-fill: #f59e0b;");
        
        new Thread(() -> {
            List<USBDeviceDetector.USBDevice> devices = USBDeviceDetector.detectUSBDevices();
            
            Platform.runLater(() -> {
                usbDeviceComboBox.getItems().clear();
                
                if (devices.isEmpty()) {
                    usbStatusLabel.setText("No USB devices found");
                    usbStatusLabel.setStyle("-fx-text-fill: #ef4444;");
                    usbDetailsArea.setText("No USB storage devices detected.\n\nPlease insert a USB drive and click 'Refresh'.");
                } else {
                    usbDeviceComboBox.getItems().addAll(devices);
                    usbStatusLabel.setText("Found " + devices.size() + " USB device(s)");
                    usbStatusLabel.setStyle("-fx-text-fill: #10b981;");
                    
                    // Auto-select first device
                    if (!devices.isEmpty()) {
                        usbDeviceComboBox.getSelectionModel().selectFirst();
                    }
                }
            });
        }).start();
    }
    
    /**
     * Display details of the selected USB device
     */
    private void displayUSBDeviceDetails(USBDeviceDetector.USBDevice device) {
        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════\n");
        details.append("  USB DEVICE INFORMATION\n");
        details.append("═══════════════════════════════════════\n\n");
        
        details.append("🔌 Device Name:     ").append(device.getDisplayName()).append("\n");
        if (device.description != null && !device.description.isEmpty()) {
            details.append("📋 Description:     ").append(device.description).append("\n");
        }
        if (device.manufacturer != null && !device.manufacturer.isEmpty()) {
            details.append("🏭 Manufacturer:    ").append(device.manufacturer).append("\n");
        }
        if (device.deviceClass != null && !device.deviceClass.isEmpty()) {
            details.append("📁 Device Class:    ").append(device.deviceClass).append("\n");
        }
        details.append("✅ Status:          ").append(device.status != null ? device.status : "Unknown").append("\n");
        
        details.append("\n───────────────────────────────────────\n");
        details.append("  DRIVER INFORMATION\n");
        details.append("───────────────────────────────────────\n\n");
        
        if (device.driverProvider != null && !device.driverProvider.isEmpty()) {
            details.append("👤 Driver Provider: ").append(device.driverProvider).append("\n");
        }
        if (device.driverVersion != null && !device.driverVersion.isEmpty()) {
            details.append("📦 Driver Version:  ").append(device.driverVersion).append("\n");
        }
        
        details.append("\n───────────────────────────────────────\n");
        details.append("  DEVICE ID\n");
        details.append("───────────────────────────────────────\n\n");
        
        if (device.deviceId != null && !device.deviceId.isEmpty()) {
            // Split long device ID for readability
            String id = device.deviceId;
            if (id.length() > 40) {
                details.append(id.substring(0, 40)).append("\n");
                details.append(id.substring(40)).append("\n");
            } else {
                details.append(id).append("\n");
            }
        }
        
        usbDetailsArea.setText(details.toString());
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
        
        // Set dialog icon
        setAlertIcon(alert);
        
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
        // Create the update progress window
        UpdateWindow updateWindow = new UpdateWindow();
        updateWindow.show();
        updateWindow.setStatus("Downloading update...");
        updateWindow.setDetail("Downloading v" + release.version + " from GitHub");
        
        new Thread(() -> {
            try {
                // Download to temp location
                String tempDir = System.getProperty("java.io.tmpdir");
                String installerName = "Servicegest-Companion-Update.exe";
                String installerPath = tempDir + "/" + installerName;
                
                // Download with progress updates
                boolean downloadSuccess = downloadWithProgress(release.downloadUrl, installerPath, updateWindow);
                
                if (downloadSuccess) {
                    // Hide main window
                    Platform.runLater(() -> primaryStage.hide());
                    
                    // Run installation with visual feedback
                    updateWindow.runUpdateInstallation(installerPath, () -> {
                        // Exit the application after update
                        Platform.exit();
                        System.exit(0);
                    });
                } else {
                    Platform.runLater(() -> {
                        updateWindow.close();
                        showAlert("Download Failed", "Failed to download the update. Please try again later.");
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    updateWindow.close();
                    showAlert("Update Error", "Error during update: " + e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Download file with progress reporting to UpdateWindow
     */
    private boolean downloadWithProgress(String urlString, String destinationPath, UpdateWindow updateWindow) {
        try {
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            
            // Handle redirects
            int responseCode = conn.getResponseCode();
            if (responseCode == java.net.HttpURLConnection.HTTP_MOVED_PERM || 
                responseCode == java.net.HttpURLConnection.HTTP_MOVED_TEMP) {
                String redirectUrl = conn.getHeaderField("Location");
                conn = (java.net.HttpURLConnection) new java.net.URL(redirectUrl).openConnection();
            }
            
            if (conn.getResponseCode() != 200) {
                return false;
            }
            
            long fileSize = conn.getContentLengthLong();
            
            try (java.io.InputStream input = conn.getInputStream();
                 java.io.FileOutputStream output = new java.io.FileOutputStream(destinationPath)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalRead = 0;
                
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    
                    if (fileSize > 0) {
                        double progress = (double) totalRead / fileSize;
                        long totalMB = totalRead / (1024 * 1024);
                        long sizeMB = fileSize / (1024 * 1024);
                        
                        final double p = progress;
                        final String detail = String.format("Downloaded %d MB of %d MB", totalMB, sizeMB);
                        
                        Platform.runLater(() -> {
                            updateWindow.setProgress(p);
                            updateWindow.setDetail(detail);
                        });
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            System.out.println("[Download] Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Show simple alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        setAlertIcon(alert);
        alert.showAndWait();
    }
    
    /**
     * Set application icon on alert dialogs
     */
    private void setAlertIcon(Alert alert) {
        try {
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                alertStage.getIcons().add(new javafx.scene.image.Image(iconStream));
            }
        } catch (Exception e) {
            // Icon not critical, ignore errors
        }
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
        
        // Create a popup menu for the tray icon - Windows 11 style
        PopupMenu popup = new PopupMenu();
        
        // Set font for menu items (Windows 11 uses Segoe UI Variable)
        java.awt.Font menuFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12);
        java.awt.Font menuFontBold = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        
        // === Status Section ===
        java.awt.MenuItem statusItem = new java.awt.MenuItem("Status: Checking...");
        statusItem.setFont(menuFont);
        statusItem.setEnabled(false);
        popup.add(statusItem);
        
        popup.addSeparator();
        
        // === Open App ===
        java.awt.MenuItem openItem = new java.awt.MenuItem("Open Servicegest Companion");
        openItem.setFont(menuFontBold);
        openItem.addActionListener(e -> showWindow());
        popup.add(openItem);
        
        popup.addSeparator();
        
        // === Actions Section ===
        java.awt.MenuItem checkHealthItem = new java.awt.MenuItem("Check API Health Now");
        checkHealthItem.setFont(menuFont);
        checkHealthItem.addActionListener(e -> {
            performHealthCheck();
            if (trayIcon != null) {
                trayIcon.displayMessage("Health Check", "Checking API health...", TrayIcon.MessageType.INFO);
            }
        });
        popup.add(checkHealthItem);
        
        java.awt.MenuItem checkUpdatesItem = new java.awt.MenuItem("Check for Updates");
        checkUpdatesItem.setFont(menuFont);
        checkUpdatesItem.addActionListener(e -> checkForUpdatesFromTray());
        popup.add(checkUpdatesItem);
        
        popup.addSeparator();
        
        // === Settings Submenu ===
        java.awt.Menu intervalMenu = new java.awt.Menu("Check Interval");
        intervalMenu.setFont(menuFont);
        
        java.awt.CheckboxMenuItem interval5 = new java.awt.CheckboxMenuItem("5 seconds");
        java.awt.CheckboxMenuItem interval10 = new java.awt.CheckboxMenuItem("10 seconds", true);
        java.awt.CheckboxMenuItem interval15 = new java.awt.CheckboxMenuItem("15 seconds");
        java.awt.CheckboxMenuItem interval30 = new java.awt.CheckboxMenuItem("30 seconds");
        java.awt.CheckboxMenuItem interval60 = new java.awt.CheckboxMenuItem("60 seconds");
        
        java.awt.CheckboxMenuItem[] intervals = {interval5, interval10, interval15, interval30, interval60};
        int[] intervalValues = {5, 10, 15, 30, 60};
        
        for (int i = 0; i < intervals.length; i++) {
            final int index = i;
            intervals[i].setFont(menuFont);
            intervals[i].addItemListener(ev -> {
                if (intervals[index].getState()) {
                    for (int j = 0; j < intervals.length; j++) {
                        if (j != index) intervals[j].setState(false);
                    }
                    stopHealthCheckTimer();
                    startHealthCheckTimer(intervalValues[index] * 1000L);
                }
            });
            intervalMenu.add(intervals[i]);
        }
        popup.add(intervalMenu);
        
        popup.addSeparator();
        
        // === About ===
        java.awt.MenuItem aboutItem = new java.awt.MenuItem("About");
        aboutItem.setFont(menuFont);
        aboutItem.addActionListener(e -> {
            Platform.runLater(() -> {
                showAlert("About Servicegest Companion", 
                    "Servicegest Companion v" + AutoUpdater.getCurrentVersion() + "\n\n" +
                    "API Health Monitor Application\n" +
                    "Monitors api.servicegest.ro/health");
            });
        });
        popup.add(aboutItem);
        
        popup.addSeparator();
        
        // === Exit ===
        java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
        exitItem.setFont(menuFont);
        exitItem.addActionListener(e -> {
            stopHealthCheckTimer();
            if (systemTray != null && trayIcon != null) {
                systemTray.remove(trayIcon);
            }
            Platform.exit();
            System.exit(0);
        });
        popup.add(exitItem);
        
        // Create tray icon
        Image image = createTrayImage();
        trayIcon = new TrayIcon(image, "Servicegest Companion - API Monitor", popup);
        trayIcon.setImageAutoSize(true);
        
        // Add click listener to show window on left-click
        trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    showWindow();
                }
            }
        });
        
        // Add action listener for primary action (double-click)
        trayIcon.addActionListener(e -> showWindow());
        
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Failed to add tray icon: " + e.getMessage());
        }
    }
    
    /**
     * Check for updates from system tray
     */
    private void checkForUpdatesFromTray() {
        if (trayIcon != null) {
            trayIcon.displayMessage("Update Check", "Checking for updates...", TrayIcon.MessageType.INFO);
        }
        
        new Thread(() -> {
            GitHubReleaseChecker.ReleaseInfo release = GitHubReleaseChecker.getLatestRelease();
            
            Platform.runLater(() -> {
                if (release == null) {
                    if (trayIcon != null) {
                        trayIcon.displayMessage("Update Check Failed", 
                            "Could not connect to GitHub.", TrayIcon.MessageType.WARNING);
                    }
                    return;
                }
                
                String currentVersion = AutoUpdater.getCurrentVersion();
                int comparison = GitHubReleaseChecker.compareVersions(release.version, currentVersion);
                
                if (comparison <= 0) {
                    if (trayIcon != null) {
                        trayIcon.displayMessage("No Updates", 
                            "You're running the latest version (" + currentVersion + ")", 
                            TrayIcon.MessageType.INFO);
                    }
                } else {
                    if (trayIcon != null) {
                        trayIcon.displayMessage("Update Available", 
                            "New version " + release.version + " is available!", 
                            TrayIcon.MessageType.INFO);
                    }
                    showWindow();
                    showUpdateAvailableDialog(release);
                }
            });
        }).start();
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
