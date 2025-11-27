package com.servicegest.companion.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to detect all USB devices connected to the system
 * Including peripherals like mice, keyboards, dongles, etc.
 */
public class USBDeviceDetector {
    
    /**
     * Represents a USB device with its properties and driver information
     */
    public static class USBDevice {
        public String name;
        public String deviceId;
        public String description;
        public String manufacturer;
        public String driverVersion;
        public String driverDate;
        public String driverProvider;
        public String status;
        public String deviceClass;
        public String hardwareId;
        
        public USBDevice() {
            this.name = "Unknown Device";
            this.deviceId = "";
            this.description = "";
            this.manufacturer = "Unknown";
            this.driverVersion = "";
            this.driverDate = "";
            this.driverProvider = "";
            this.status = "Unknown";
            this.deviceClass = "";
            this.hardwareId = "";
        }
        
        public String getDisplayName() {
            if (name != null && !name.isEmpty() && !name.equals("Unknown Device")) {
                return name;
            }
            if (description != null && !description.isEmpty()) {
                return description;
            }
            return "USB Device";
        }
        
        @Override
        public String toString() {
            return getDisplayName();
        }
    }
    
    /**
     * Format bytes to human readable format
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Detect all USB devices connected to the system (main method)
     */
    public static List<USBDevice> detectUSBDevices() {
        List<USBDevice> devices = new ArrayList<>();
        
        try {
            // Use PowerShell with Get-PnpDevice to get all USB devices
            String[] cmd = {
                "powershell.exe",
                "-Command",
                "Get-PnpDevice | Where-Object { $_.InstanceId -like 'USB*' -and $_.Status -eq 'OK' } | ForEach-Object { " +
                "  $device = $_; " +
                "  $props = Get-PnpDeviceProperty -InstanceId $device.InstanceId -KeyName 'DEVPKEY_Device_DriverVersion','DEVPKEY_Device_DriverDate','DEVPKEY_Device_DriverProvider','DEVPKEY_Device_Manufacturer' -ErrorAction SilentlyContinue; " +
                "  [PSCustomObject]@{ " +
                "    Name = $device.FriendlyName; " +
                "    Description = $device.Description; " +
                "    DeviceId = $device.InstanceId; " +
                "    Class = $device.Class; " +
                "    Status = $device.Status; " +
                "    Manufacturer = ($props | Where-Object KeyName -eq 'DEVPKEY_Device_Manufacturer').Data; " +
                "    DriverVersion = ($props | Where-Object KeyName -eq 'DEVPKEY_Device_DriverVersion').Data; " +
                "    DriverProvider = ($props | Where-Object KeyName -eq 'DEVPKEY_Device_DriverProvider').Data; " +
                "  } " +
                "} | ConvertTo-Json -Depth 3"
            };
            
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            // Read error stream for debugging
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.out.println("[USBDetector] Error: " + line);
            }
            
            process.waitFor();
            
            String json = output.toString().trim();
            System.out.println("[USBDetector] Found JSON data: " + (json.length() > 0 ? "yes" : "no"));
            
            if (!json.isEmpty() && !json.equals("null")) {
                devices = parseDevicesJson(json);
            }
            
        } catch (Exception e) {
            System.out.println("[USBDetector] Detection failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Fallback to WMI if PnP method failed
        if (devices.isEmpty()) {
            System.out.println("[USBDetector] Using WMI fallback...");
            devices = detectUSBDevicesWMI();
        }
        
        return devices;
    }
    
    /**
     * Fallback: Detect USB devices using WMI
     */
    private static List<USBDevice> detectUSBDevicesWMI() {
        List<USBDevice> devices = new ArrayList<>();
        
        try {
            String[] cmd = {
                "powershell.exe",
                "-Command",
                "Get-WmiObject Win32_PnPEntity | Where-Object { $_.DeviceID -like 'USB*' -and $_.Status -eq 'OK' } | " +
                "Select-Object Name, Description, DeviceID, Manufacturer, Status, PNPClass | ConvertTo-Json"
            };
            
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();
            
            String json = output.toString().trim();
            if (!json.isEmpty() && !json.equals("null")) {
                devices = parseDevicesJson(json);
            }
            
        } catch (Exception e) {
            System.out.println("[USBDetector] WMI fallback failed: " + e.getMessage());
        }
        
        return devices;
    }
    
    /**
     * Parse JSON output from device query
     */
    private static List<USBDevice> parseDevicesJson(String json) {
        List<USBDevice> devices = new ArrayList<>();
        
        try {
            if (json.startsWith("[")) {
                // Multiple devices
                json = json.substring(1, json.length() - 1);
                List<String> objects = splitJsonObjects(json);
                
                for (String obj : objects) {
                    USBDevice device = parseDeviceObject(obj);
                    if (device != null && isValidDevice(device)) {
                        devices.add(device);
                    }
                }
            } else if (json.startsWith("{")) {
                // Single device
                USBDevice device = parseDeviceObject(json);
                if (device != null && isValidDevice(device)) {
                    devices.add(device);
                }
            }
        } catch (Exception e) {
            System.out.println("[USBDetector] JSON parse error: " + e.getMessage());
        }
        
        return devices;
    }
    
    /**
     * Split JSON array into individual objects
     */
    private static List<String> splitJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(json.substring(start, i + 1).trim());
                    start = i + 1;
                    while (start < json.length() && (json.charAt(start) == ',' || Character.isWhitespace(json.charAt(start)))) {
                        start++;
                    }
                    i = start - 1;
                }
            }
        }
        
        return objects;
    }
    
    /**
     * Parse a single device JSON object
     */
    private static USBDevice parseDeviceObject(String json) {
        try {
            USBDevice device = new USBDevice();
            
            device.name = extractJsonString(json, "Name");
            device.description = extractJsonString(json, "Description");
            device.deviceId = extractJsonString(json, "DeviceId");
            if (device.deviceId == null) {
                device.deviceId = extractJsonString(json, "DeviceID"); // WMI uses uppercase
            }
            device.deviceClass = extractJsonString(json, "Class");
            if (device.deviceClass == null) {
                device.deviceClass = extractJsonString(json, "PNPClass");
            }
            device.status = extractJsonString(json, "Status");
            device.manufacturer = extractJsonString(json, "Manufacturer");
            device.driverVersion = extractJsonString(json, "DriverVersion");
            device.driverProvider = extractJsonString(json, "DriverProvider");
            
            return device;
        } catch (Exception e) {
            System.out.println("[USBDetector] Device parse error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Check if device is valid (filter out hubs and generic entries)
     */
    private static boolean isValidDevice(USBDevice device) {
        String name = device.getDisplayName().toLowerCase();
        String desc = (device.description != null ? device.description : "").toLowerCase();
        
        // Filter out root hubs (not interesting to users)
        if (name.contains("root hub") || desc.contains("root hub")) {
            return false;
        }
        
        // Filter out generic USB composite devices (parent devices)
        if (name.equals("usb composite device") || desc.equals("usb composite device")) {
            return false;
        }
        
        // Keep everything else including USB hubs (they might have devices under them)
        return device.name != null || device.description != null;
    }
    
    /**
     * Extract string value from JSON
     */
    private static String extractJsonString(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) {
                // Try case variations
                searchKey = "\"" + key.toLowerCase() + "\":";
                keyIndex = json.toLowerCase().indexOf(searchKey);
                if (keyIndex == -1) return null;
            }
            
            int valueStart = json.indexOf(":", keyIndex) + 1;
            
            while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
                valueStart++;
            }
            
            if (valueStart >= json.length()) return null;
            
            char firstChar = json.charAt(valueStart);
            
            if (firstChar == '"') {
                int valueEnd = valueStart + 1;
                while (valueEnd < json.length()) {
                    if (json.charAt(valueEnd) == '"' && json.charAt(valueEnd - 1) != '\\') {
                        break;
                    }
                    valueEnd++;
                }
                return json.substring(valueStart + 1, valueEnd);
            } else if (firstChar == 'n' && json.substring(valueStart).startsWith("null")) {
                return null;
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
        return null;
    }
    
    /**
     * Get detailed driver information for a specific device
     */
    public static String getDeviceDriverDetails(String deviceId) {
        StringBuilder details = new StringBuilder();
        
        try {
            String escapedId = deviceId.replace("\\", "\\\\").replace("'", "''");
            
            String[] cmd = {
                "powershell.exe",
                "-Command",
                "Get-PnpDeviceProperty -InstanceId '" + escapedId + "' | " +
                "Where-Object { $_.Data -ne $null -and $_.Data -ne '' } | " +
                "Select-Object KeyName, Data | " +
                "ForEach-Object { $_.KeyName.Replace('DEVPKEY_','') + ': ' + $_.Data }"
            };
            
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                details.append(line).append("\n");
            }
            process.waitFor();
            
        } catch (Exception e) {
            details.append("Error getting driver details: ").append(e.getMessage());
        }
        
        return details.toString();
    }
    
    /**
     * Open Device Manager
     */
    public static void openDeviceManager() {
        try {
            Runtime.getRuntime().exec("devmgmt.msc");
        } catch (Exception e) {
            System.out.println("[USBDetector] Failed to open Device Manager: " + e.getMessage());
        }
    }
}
