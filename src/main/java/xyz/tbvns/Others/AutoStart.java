package xyz.tbvns.Others;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.io.*;

public class AutoStart {

    // Method for Windows
    public static void addToStartupWindows(String appName, String javaPath, String jarFilePath) {
        // Build the command to run the JAR file
        String command = "\"" + javaPath.replace("/", "\\") + "\" -jar \"" + jarFilePath + "\" autostart";

        try {
            // Execute the `reg add` command to add/update the Registry entry
            Process process = Runtime.getRuntime().exec(
                    "reg add HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v " + appName +
                            " /t REG_SZ /d \"" + command + "\" /f"
            );

            // Wait for the command to complete
            process.waitFor();

            // Check if the command succeeded
            if (process.exitValue() == 0) {
                System.out.println("Added/Updated startup entry: " + appName);
            } else {
                System.out.println("Failed to add/update startup entry: " + appName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method for Linux
    public static void addToStartupLinux(String javaPath, String jarFilePath) {
        String autostartDir = System.getProperty("user.home") + "/.config/autostart/";
        new File(autostartDir).mkdirs();
        File desktopFile = new File(autostartDir, "TbvnsStoreApp.desktop");

        // Check if the entry already exists
        if (desktopFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(desktopFile))) {
                String line;
                boolean exists = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(javaPath + " -jar " + jarFilePath + " autostart")) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    createLinuxStartupFile(desktopFile, javaPath, jarFilePath);
                } else {
                    System.out.println("Already in startup: " + desktopFile.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            createLinuxStartupFile(desktopFile, javaPath, jarFilePath);
        }
    }

    private static void createLinuxStartupFile(File desktopFile, String javaPath, String jarFilePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(desktopFile))) {
            writer.println("[Desktop Entry]");
            writer.println("Name=MyApp");
            writer.println("Exec=" + javaPath + " -jar " + jarFilePath + " autostart");
            writer.println("Type=Application");
            writer.println("X-GNOME-Autostart-enabled=true");
            System.out.println("Added to startup: " + desktopFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to detect the operating system and call the appropriate function
    public static void addToStartup(String javaPath, String jarFilePath) {
        // Using OSHI to get the current operating system
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        String osName = os.toString().toLowerCase();

        if (osName.contains("win")) {
            addToStartupWindows("TbvnsAppStore", javaPath, jarFilePath);
        } else if (osName.contains("nix") || osName.contains("nux")) {
            addToStartupLinux(javaPath, jarFilePath);
        } else {
            System.out.println("Unsupported OS: " + osName);
        }
    }
}