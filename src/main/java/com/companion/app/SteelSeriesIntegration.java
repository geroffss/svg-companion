package com.companion.app;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * SteelSeries GG Integration
 * Communicates with SteelSeries Engine to control mouse settings
 */
public class SteelSeriesIntegration {
    
    private static final String STEELSERIES_PROPS_PATH = System.getenv("PROGRAMDATA") + 
        "\\SteelSeries\\SteelSeries Engine 3\\coreProps.json";
    
    private String serverAddress;
    private int serverPort;
    private boolean connected = false;
    
    /**
     * Initialize connection to SteelSeries Engine
     */
    public boolean initialize() {
        try {
            // Read SteelSeries Engine connection info
            Path propsPath = Paths.get(STEELSERIES_PROPS_PATH);
            
            if (!Files.exists(propsPath)) {
                System.err.println("SteelSeries Engine not found at: " + STEELSERIES_PROPS_PATH);
                return false;
            }
            
            String propsContent = new String(Files.readAllBytes(propsPath));
            
            // Simple JSON parsing without library
            this.serverAddress = extractJsonValue(propsContent, "address");
            String portStr = extractJsonValue(propsContent, "port");
            this.serverPort = Integer.parseInt(portStr);
            
            System.out.println("SteelSeries Engine found at: " + serverAddress + ":" + serverPort);
            connected = true;
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to initialize SteelSeries connection: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Simple JSON value extractor
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex += searchKey.length();
        // Skip whitespace and quotes
        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '"')) {
            startIndex++;
        }
        
        int endIndex = startIndex;
        while (endIndex < json.length() && json.charAt(endIndex) != ',' && json.charAt(endIndex) != '"' && json.charAt(endIndex) != '}') {
            endIndex++;
        }
        
        return json.substring(startIndex, endIndex).trim();
    }
    
    /**
     * Register the game/app with SteelSeries Engine
     */
    public boolean registerGame(String gameName, String displayName) {
        if (!connected) {
            System.err.println("Not connected to SteelSeries Engine");
            return false;
        }
        
        try {
            String endpoint = "http://" + serverAddress + ":" + serverPort + "/game_metadata";
            
            String payload = String.format(
                "{" +
                "  \"game\": \"%s\"," +
                "  \"game_display_name\": \"%s\"," +
                "  \"developer\": \"Companion App\"" +
                "}", 
                gameName, displayName
            );
            
            return sendPostRequest(endpoint, payload);
            
        } catch (Exception e) {
            System.err.println("Failed to register game: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Set mouse DPI
     */
    public boolean setMouseDPI(int dpi) {
        if (!connected) return false;
        
        try {
            String endpoint = "http://" + serverAddress + ":" + serverPort + "/game_event";
            
            String payload = String.format(
                "{" +
                "  \"game\": \"COMPANION_APP\"," +
                "  \"event\": \"DPI_CHANGE\"," +
                "  \"data\": {" +
                "    \"value\": %d" +
                "  }" +
                "}", 
                dpi
            );
            
            System.out.println("Setting DPI to: " + dpi);
            return sendPostRequest(endpoint, payload);
            
        } catch (Exception e) {
            System.err.println("Failed to set DPI: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Set mouse polling rate (Hz)
     */
    public boolean setPollingRate(int rate) {
        if (!connected) return false;
        
        try {
            String endpoint = "http://" + serverAddress + ":" + serverPort + "/game_event";
            
            String payload = String.format(
                "{" +
                "  \"game\": \"COMPANION_APP\"," +
                "  \"event\": \"POLLING_RATE\"," +
                "  \"data\": {" +
                "    \"value\": %d" +
                "  }" +
                "}", 
                rate
            );
            
            System.out.println("Setting polling rate to: " + rate + "Hz");
            return sendPostRequest(endpoint, payload);
            
        } catch (Exception e) {
            System.err.println("Failed to set polling rate: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Activate a specific profile by name
     */
    public boolean activateProfile(String profileName) {
        if (!connected) return false;
        
        try {
            String endpoint = "http://" + serverAddress + ":" + serverPort + "/game_event";
            
            String payload = String.format(
                "{" +
                "  \"game\": \"COMPANION_APP\"," +
                "  \"event\": \"PROFILE_SWITCH\"," +
                "  \"data\": {" +
                "    \"profile\": \"%s\"" +
                "  }" +
                "}", 
                profileName
            );
            
            System.out.println("Activating profile: " + profileName);
            return sendPostRequest(endpoint, payload);
            
        } catch (Exception e) {
            System.err.println("Failed to activate profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Send illumination event (if supported by device)
     */
    public boolean setIllumination(int red, int green, int blue) {
        if (!connected) return false;
        
        try {
            String endpoint = "http://" + serverAddress + ":" + serverPort + "/game_event";
            
            String payload = String.format(
                "{" +
                "  \"game\": \"COMPANION_APP\"," +
                "  \"event\": \"COLOR\"," +
                "  \"data\": {" +
                "    \"value\": {" +
                "      \"red\": %d," +
                "      \"green\": %d," +
                "      \"blue\": %d" +
                "    }" +
                "  }" +
                "}", 
                red, green, blue
            );
            
            return sendPostRequest(endpoint, payload);
            
        } catch (Exception e) {
            System.err.println("Failed to set illumination: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if SteelSeries Engine is installed and running
     */
    public static boolean isSteelSeriesInstalled() {
        Path propsPath = Paths.get(System.getenv("PROGRAMDATA") + 
            "\\SteelSeries\\SteelSeries Engine 3\\coreProps.json");
        return Files.exists(propsPath);
    }
    
    /**
     * Send HTTP POST request to SteelSeries Engine
     */
    private boolean sendPostRequest(String urlString, String payload) {
        try {
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode >= 200 && responseCode < 300;
            
        } catch (Exception e) {
            System.err.println("HTTP request failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Test connection to SteelSeries Engine
     */
    public boolean testConnection() {
        if (!connected) {
            return initialize();
        }
        
        try {
            String endpoint = "http://" + serverAddress + ":" + serverPort + "/";
            java.net.URL url = new java.net.URL(endpoint);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
            
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
