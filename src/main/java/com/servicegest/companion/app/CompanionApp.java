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
            checkNowButton,
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
        showItem.addActionListener(e -> Platform.runLater(this::showWindow));
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
        
        // Add double-click listener to show window
        trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Platform.runLater(CompanionApp.this::showWindow);
                }
            }
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
            primaryStage.setIconified(true);
        });
    }
    
    /**
     * Show window from system tray
     */
    private void showWindow() {
        Platform.runLater(() -> {
            primaryStage.show();
            primaryStage.setIconified(false);
            primaryStage.toFront();
            primaryStage.requestFocus();
        });
    }
    
    /**
     * Create a simple tray icon image (green circle)
     */
    private Image createTrayImage() {
        // Create a 16x16 image with a green circle
        int size = 16;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);
        
        // Green circle
        g2d.setColor(new Color(16, 185, 129));
        g2d.fillOval(2, 2, size - 4, size - 4);
        
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
