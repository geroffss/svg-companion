package com.companion.app;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;

public class AutoStartManager {
    
    private static final String APP_NAME = "Servicegest";
    private static final String RUN_KEY = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
    
    public static void enableAutoStart() {
        if (!isWindows()) {
            System.out.println("Auto-start is only supported on Windows");
            return;
        }
        
        try {
            String appPath = getApplicationPath();
            
            if (appPath != null && !appPath.isEmpty()) {
                // Add to Windows Registry for auto-start
                Advapi32Util.registrySetStringValue(
                    WinReg.HKEY_CURRENT_USER, 
                    RUN_KEY, 
                    APP_NAME, 
                    "\"" + appPath + "\""
                );
                System.out.println("Auto-start enabled: " + appPath);
            } else {
                System.out.println("Could not determine application path for auto-start");
            }
        } catch (Exception e) {
            System.err.println("Failed to enable auto-start: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void disableAutoStart() {
        if (!isWindows()) {
            return;
        }
        
        try {
            if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME)) {
                Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME);
                System.out.println("Auto-start disabled");
            }
        } catch (Exception e) {
            System.err.println("Failed to disable auto-start: " + e.getMessage());
        }
    }
    
    public static boolean isAutoStartEnabled() {
        if (!isWindows()) {
            return false;
        }
        
        try {
            return Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME);
        } catch (Exception e) {
            return false;
        }
    }
    
    private static String getApplicationPath() {
        try {
            // Get the path of the running JAR or executable
            String path = AutoStartManager.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
            
            // Remove leading slash on Windows
            if (path.startsWith("/") && path.contains(":")) {
                path = path.substring(1);
            }
            
            // If running from JAR, use javaw to launch it
            if (path.endsWith(".jar")) {
                String javaHome = System.getProperty("java.home");
                String javawPath = javaHome + File.separator + "bin" + File.separator + "javaw.exe";
                return javawPath + " -jar \"" + path + "\"";
            }
            
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
