package com.servicegest.companion.app;

/**
 * Simple test class to verify GitHub release checking works
 */
public class TestUpdateCheck {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Auto-Update Feature ===\n");
        
        // Check current version
        String currentVersion = AutoUpdater.getCurrentVersion();
        System.out.println("Current version (from version.json): " + currentVersion);
        
        // Check GitHub for latest release
        System.out.println("\nFetching latest release from GitHub...");
        GitHubReleaseChecker.ReleaseInfo release = GitHubReleaseChecker.getLatestRelease();
        
        if (release == null) {
            System.out.println("\nERROR: Could not fetch release info from GitHub!");
            System.out.println("Possible causes:");
            System.out.println("  - No internet connection");
            System.out.println("  - No releases published on GitHub");
            System.out.println("  - Repository not accessible");
            return;
        }
        
        System.out.println("\n=== GitHub Release Found ===");
        System.out.println("Tag: " + release.tagName);
        System.out.println("Version: " + release.version);
        System.out.println("Download URL: " + release.downloadUrl);
        System.out.println("Release Notes: " + (release.releaseNotes != null ? release.releaseNotes : "N/A"));
        
        // Compare versions
        int comparison = GitHubReleaseChecker.compareVersions(release.version, currentVersion);
        
        System.out.println("\n=== Version Comparison ===");
        System.out.println("Current: " + currentVersion);
        System.out.println("Latest:  " + release.version);
        
        if (comparison > 0) {
            System.out.println("\n✓ UPDATE AVAILABLE! (GitHub version is newer)");
        } else if (comparison == 0) {
            System.out.println("\n✓ You are running the latest version.");
        } else {
            System.out.println("\n? Local version is newer than GitHub release.");
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
