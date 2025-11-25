package com.servicegest.companion.app;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

/**
 * Simple HTTP health checker that verifies API response status is 200
 */
public class HealthChecker {
    
    private static final String API_ENDPOINT = "https://api.servicegest.ro/health";
    private static final int TIMEOUT_MS = 5000; // 5 second timeout
    
    /**
     * Check if API is healthy by verifying 200 response status
     * @return true if API responds with 200 status code, false otherwise
     */
    public static boolean isAPIHealthy() {
        try {
            URL url = new URL(API_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            
            // Disable automatic redirects to check exact response code
            connection.setInstanceFollowRedirects(false);
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            System.out.println("[HealthChecker] API response code: " + responseCode);
            return responseCode == 200;
            
        } catch (IOException e) {
            System.err.println("[HealthChecker] Error checking API health: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get detailed health status
     * @return status message
     */
    public static String getHealthStatus() {
        try {
            URL url = new URL(API_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200) {
                connection.disconnect();
                return "✓ API is healthy (200 OK)";
            } else {
                connection.disconnect();
                return "✗ API returned status code: " + responseCode;
            }
            
        } catch (IOException e) {
            return "✗ Cannot connect to API: " + e.getMessage();
        }
    }
}
