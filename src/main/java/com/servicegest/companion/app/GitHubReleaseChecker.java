package com.servicegest.companion.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GitHub Release Checker - Fetches latest release info from GitHub API
 */
public class GitHubReleaseChecker {
    
    private static final String GITHUB_API_URL = 
        "https://api.github.com/repos/geroffss/svg-companion/releases/latest";
    private static final int TIMEOUT_MS = 5000;
    
    /**
     * GitHub release information
     */
    public static class ReleaseInfo {
        public String tagName;
        public String version;
        public String downloadUrl;
        public String releaseNotes;
        
        public ReleaseInfo(String tagName, String downloadUrl, String releaseNotes) {
            this.tagName = tagName;
            this.version = tagName.startsWith("v") ? tagName.substring(1) : tagName;
            this.downloadUrl = downloadUrl;
            this.releaseNotes = releaseNotes;
        }
    }
    
    /**
     * Fetch latest release information from GitHub
     * @return ReleaseInfo or null if unable to fetch
     */
    public static ReleaseInfo getLatestRelease() {
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setInstanceFollowRedirects(true);
            
            int responseCode = conn.getResponseCode();
            System.out.println("[GitHubReleaseChecker] HTTP Response: " + responseCode);
            
            if (responseCode == 403) {
                System.out.println("[GitHubReleaseChecker] Rate limited by GitHub. Try again in a few minutes.");
                return null;
            }
            
            if (responseCode == 404) {
                System.out.println("[GitHubReleaseChecker] No releases found on GitHub repository.");
                return null;
            }
            
            if (responseCode != 200) {
                System.out.println("[GitHubReleaseChecker] Failed to fetch releases: HTTP " + responseCode);
                // Print response body for debugging
                try (BufferedReader errReader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream()))) {
                    String errLine;
                    while ((errLine = errReader.readLine()) != null) {
                        System.out.println("[GitHubReleaseChecker] Error: " + errLine);
                    }
                } catch (Exception e) {
                    // Ignore error reading error stream
                }
                return null;
            }
            
            // Read response
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            
            // Parse JSON response manually
            String jsonResponse = response.toString();
            System.out.println("[GitHubReleaseChecker] Response received, parsing...");
            
            // Extract tag_name
            String tagName = extractJsonField(jsonResponse, "tag_name");
            if (tagName == null) {
                System.out.println("[GitHubReleaseChecker] Could not find tag_name in response");
                System.out.println("[GitHubReleaseChecker] Response preview: " + 
                    jsonResponse.substring(0, Math.min(200, jsonResponse.length())));
                return null;
            }
            
            // Extract download URL (browser_download_url for .exe)
            String downloadUrl = extractDownloadUrl(jsonResponse);
            if (downloadUrl == null) {
                System.out.println("[GitHubReleaseChecker] Could not find .exe download URL");
                System.out.println("[GitHubReleaseChecker] Available assets in response");
                return null;
            }
            
            // Extract release notes (body)
            String releaseNotes = extractJsonField(jsonResponse, "body");
            if (releaseNotes == null) {
                releaseNotes = "New version available";
            }
            
            System.out.println("[GitHubReleaseChecker] Found release: " + tagName);
            return new ReleaseInfo(tagName, downloadUrl, releaseNotes);
            
        } catch (java.net.UnknownHostException e) {
            System.out.println("[GitHubReleaseChecker] Cannot reach GitHub - check internet connection: " + e.getMessage());
            return null;
        } catch (javax.net.ssl.SSLException e) {
            System.out.println("[GitHubReleaseChecker] SSL/TLS error connecting to GitHub: " + e.getMessage());
            return null;
        } catch (java.net.ConnectException e) {
            System.out.println("[GitHubReleaseChecker] Connection refused - check firewall/internet: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("[GitHubReleaseChecker] Error fetching release: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Extract a JSON string field value
     */
    private static String extractJsonField(String json, String fieldName) {
        // Match: "fieldName": "value"
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String value = matcher.group(1);
            // Unescape JSON string
            value = value.replace("\\\"", "\"")
                         .replace("\\n", "\n")
                         .replace("\\r", "\r")
                         .replace("\\\\", "\\");
            return value;
        }
        return null;
    }
    
    /**
     * Extract .exe download URL from release assets
     */
    private static String extractDownloadUrl(String json) {
        // Look for browser_download_url containing .exe
        Pattern pattern = Pattern.compile("\"browser_download_url\"\\s*:\\s*\"([^\"]*\\.exe[^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * Compare two version strings (e.g., "1.0.0" vs "1.0.1")
     * Returns: positive if version1 > version2, negative if version1 < version2, 0 if equal
     */
    public static int compareVersions(String version1, String version2) {
        String[] v1parts = version1.split("\\.");
        String[] v2parts = version2.split("\\.");
        
        int length = Math.max(v1parts.length, v2parts.length);
        for (int i = 0; i < length; i++) {
            int v1num = i < v1parts.length ? 
                Integer.parseInt(v1parts[i].replaceAll("[^0-9]", "")) : 0;
            int v2num = i < v2parts.length ? 
                Integer.parseInt(v2parts[i].replaceAll("[^0-9]", "")) : 0;
            
            if (v1num > v2num) return 1;
            if (v1num < v2num) return -1;
        }
        return 0;
    }
}
