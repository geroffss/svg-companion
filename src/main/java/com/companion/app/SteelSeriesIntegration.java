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
            System.out.println("SteelSeries config: " + propsContent);
            
            // Extract address (format: "address":"127.0.0.1:62776")
            String addressWithPort = extractJsonValue(propsContent, "address");
            
            if (addressWithPort != null && addressWithPort.contains(":")) {
                String[] parts = addressWithPort.split(":");
                this.serverAddress = parts[0];
                this.serverPort = Integer.parseInt(parts[1]);
            } else {
                System.err.println("Invalid address format: " + addressWithPort);
                return false;
            }
            
            System.out.println("SteelSeries Engine found at: " + serverAddress + ":" + serverPort);
            
            // Test connection
            if (!testConnection()) {
                System.err.println("Connection test failed");
                return false;
            }
            
            connected = true;
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to initialize SteelSeries connection: " + e.getMessage());
            e.printStackTrace();
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
                "  \"developer\": \"Companion App\"," +
                "  \"deinitialize_timer_length_ms\": 300000" +
                "}", 
                gameName, displayName
            );
            
            System.out.println("Registering game: " + payload);
            boolean result = sendPostRequest(endpoint, payload);
            System.out.println("Registration result: " + result);
            
            // Bind DPI event
            if (result) {
                bindEvent(gameName, "DPI_CHANGE", 1, 100);
                bindEvent(gameName, "COLOR", 1, 100);
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Failed to register game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Bind an event to display on device
     */
    private boolean bindEvent(String gameName, String eventName, int minValue, int maxValue) {
        try {
            String endpoint = "http://" + serverAddress + ":" + serverPort + "/bind_game_event";
            
            String payload = String.format(
                "{" +
                "  \"game\": \"%s\"," +
                "  \"event\": \"%s\"," +
                "  \"min_value\": %d," +
                "  \"max_value\": %d," +
                "  \"icon_id\": 1," +
                "  \"handlers\": []" +
                "}",
                gameName, eventName, minValue, maxValue
            );
            
            System.out.println("Binding event: " + payload);
            return sendPostRequest(endpoint, payload);
            
        } catch (Exception e) {
            System.err.println("Failed to bind event: " + e.getMessage());
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
            System.out.println("POST to: " + urlString);
            System.out.println("Payload: " + payload);
            
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println("Response code: " + responseCode);
            
            // Read response body for debugging
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response: " + response.toString());
                }
            } else {
                // Read error stream
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.err.println("Error response: " + response.toString());
                }
            }
            
            conn.disconnect();
            
            return responseCode >= 200 && responseCode < 300;
            
        } catch (Exception e) {
            System.err.println("HTTP request failed: " + e.getMessage());
            e.printStackTrace();
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
