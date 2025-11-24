package com.companion.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Toolkit;
import java.awt.PopupMenu;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CompanionApp extends Application {
    
    private Stage primaryStage;
    private TrayIcon trayIcon;
    private boolean isLoggedIn = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Servicegest - Autentificare v" + UpdateManager.getCurrentVersion());
        
        // Create UI components with Servicegest branding
        Label titleLabel = new Label("Conectează-te la contul tău");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: #1a1a1a;");
        
        // Email field
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        emailLabel.setStyle("-fx-text-fill: #374151;");
        
        TextField emailField = new TextField();
        emailField.setPromptText("g@g.com");
        emailField.setPrefWidth(380);
        emailField.setPrefHeight(45);
        emailField.setStyle("-fx-background-color: #eff6ff; -fx-border-color: transparent; -fx-background-radius: 8px; -fx-padding: 10px; -fx-font-size: 14px;");
        
        // Password field
        Label passwordLabel = new Label("Parolă");
        passwordLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        passwordLabel.setStyle("-fx-text-fill: #374151;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        passwordField.setPrefWidth(380);
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-background-color: #eff6ff; -fx-border-color: transparent; -fx-background-radius: 8px; -fx-padding: 10px; -fx-font-size: 14px;");
        
        // Login button
        Button loginButton = new Button("Autentificare →");
        loginButton.setPrefWidth(380);
        loginButton.setPrefHeight(45);
        loginButton.setDefaultButton(true);
        loginButton.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        
        // Open browser button (hidden until logged in)
        Button openBrowserButton = new Button("Deschide Dashboard");
        openBrowserButton.setPrefWidth(380);
        openBrowserButton.setPrefHeight(45);
        openBrowserButton.setVisible(false);
        openBrowserButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        
        // Login button action
        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            
            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Vă rugăm completați email și parola");
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }
            
            // Simple authentication (replace with real authentication)
            if (authenticate(email, password)) {
                statusLabel.setText("✓ Autentificare reușită!");
                statusLabel.setStyle("-fx-text-fill: #10b981;");
                openBrowserButton.setVisible(true);
                loginButton.setDisable(true);
                isLoggedIn = true;
            } else {
                statusLabel.setText("✗ Email sau parolă incorectă");
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
            }
        });
        
        // Open browser button action
        openBrowserButton.setOnAction(e -> {
            BrowserLauncher.openURL("http://localhost:3000");
            hideToTray();
        });
        
        // Layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(40, 30, 40, 30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white;");
        layout.getChildren().addAll(
            titleLabel,
            emailLabel,
            emailField,
            passwordLabel,
            passwordField,
            loginButton,
            statusLabel,
            openBrowserButton
        );
        
        Scene scene = new Scene(layout, 420, 480);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        
        // Setup system tray
        Platform.setImplicitExit(false);
        setupSystemTray();
        
        // Handle close button - minimize to tray instead of exit
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            hideToTray();
        });
        
        // Enable auto-start on first run
        AutoStartManager.enableAutoStart();
        
        // Check for updates on startup (silently)
        UpdateManager.checkForUpdates(true);
        
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.requestFocus();
        
        // Disable always on top after showing
        Platform.runLater(() -> primaryStage.setAlwaysOnTop(false));
    }
    
    private boolean authenticate(String username, String password) {
        // TODO: Implement real authentication logic
        // For demo purposes, accept any non-empty credentials
        return !username.isEmpty() && !password.isEmpty();
    }
    
    private void setupSystemTray() {
        // Check if system tray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported");
            return;
        }
        
        SystemTray tray = SystemTray.getSystemTray();
        
        // Create tray icon image - always use the default purple icon
        Image image = createDefaultTrayImage();
        
        // Create popup menu
        PopupMenu popup = new PopupMenu();
        
        java.awt.MenuItem openItem = new java.awt.MenuItem("Deschide");
        openItem.addActionListener(e -> Platform.runLater(this::showWindow));
        
        java.awt.MenuItem checkUpdateItem = new java.awt.MenuItem("Verifică Actualizări");
        checkUpdateItem.addActionListener(e -> UpdateManager.checkForUpdates(false));
        
        java.awt.MenuItem openBrowserItem = new java.awt.MenuItem("Deschide Dashboard");
        openBrowserItem.addActionListener(e -> {
            if (isLoggedIn) {
                BrowserLauncher.openURL("http://localhost:3000");
            } else {
                Platform.runLater(this::showWindow);
            }
        });
        
        java.awt.MenuItem exitItem = new java.awt.MenuItem("Ieșire");
        exitItem.addActionListener(e -> {
            Platform.exit();
            System.exit(0);
        });
        
        popup.add(openItem);
        popup.add(checkUpdateItem);
        popup.add(openBrowserItem);
        popup.addSeparator();
        popup.add(exitItem);
        
        // Create tray icon
        trayIcon = new TrayIcon(image, "Servicegest", popup);
        trayIcon.setImageAutoSize(true);
        
        // Add double-click listener to show window
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Platform.runLater(() -> showWindow());
                }
            }
        });
        
        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("Servicegest", "Rulează în system tray", TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added: " + e.getMessage());
        }
    }
    
    private Image createDefaultTrayImage() {
        // Create a simple 16x16 purple icon with "S" for Servicegest
        int size = 16;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Enable anti-aliasing for smoother rendering
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw purple circle background
        g.setColor(new Color(139, 92, 246)); // Servicegest purple #8b5cf6
        g.fillOval(0, 0, size, size);
        
        // Draw white "S" letter
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        g.drawString("S", 4, 13);
        
        g.dispose();
        return img;
    }
    
    private void hideToTray() {
        Platform.runLater(() -> {
            primaryStage.hide();
            if (trayIcon != null) {
                trayIcon.displayMessage("Servicegest", "Minimizat în system tray", TrayIcon.MessageType.INFO);
            }
        });
    }
    
    private void showWindow() {
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.requestFocus();
    }
    
    @Override
    public void stop() {
        System.out.println("Application is closing");
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
