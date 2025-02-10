package xyz.tbvns.Others;

import com.sun.jna.platform.win32.Shell32Util;
import com.sun.jna.platform.win32.ShlObj;
import org.apache.commons.io.IOUtils;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StartMenuManager {

    /**
     * Adds a shortcut to the Start Menu/Applications menu.
     *
     * @param appName      Name of the application
     * @param javaPath     Path to java.exe (Windows) or java binary (Linux)
     * @param jarFilePath  Path to the JAR file
     * @param iconPath     Path to the icon file (.ico for Windows, .png for Linux)
     */
    public static void addToStartMenu(String appName, String javaPath, String jarFilePath, String iconPath) {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        String osName = os.toString().toLowerCase();

        if (osName.contains("win")) {
            addToStartMenuWindows(appName, javaPath, jarFilePath, iconPath);
        } else if (osName.contains("nix") || osName.contains("nux")) {
            addToStartMenuLinux(appName, javaPath, jarFilePath, iconPath);
        } else {
            System.out.println("Unsupported OS: " + osName);
        }
    }

    // ========================================================================
    // Windows Implementation
    // ========================================================================
    public static void addToStartMenuWindows(String appName, String javaPath, String jarFilePath, String iconPath) {
        try {
            // Get the Start Menu Programs folder (requires admin privileges for all users)
            String startMenuPath = Shell32Util.getFolderPath(ShlObj.CSIDL_COMMON_PROGRAMS);
            String shortcutPath = Paths.get(startMenuPath, appName + ".lnk").toString();

            // Fix apostrophes in paths (escape single quotes for PowerShell)
            shortcutPath = shortcutPath.replace("'", "''");
            javaPath = javaPath.replace("'", "''");
            jarFilePath = jarFilePath.replace("'", "''");

            // Create a temporary PowerShell script file
            File tempScript = File.createTempFile("create_shortcut", ".ps1");
            tempScript.deleteOnExit();

            // PowerShell script content
            StringBuilder psScript = new StringBuilder();
            psScript.append("$ws = New-Object -ComObject WScript.Shell;\n");
            psScript.append(String.format("$shortcut = $ws.CreateShortcut('%s');\n", shortcutPath));
            psScript.append(String.format("$shortcut.TargetPath = '%s';\n", javaPath));
            psScript.append(String.format("$shortcut.Arguments = '-jar \"%s\"';\n", jarFilePath));

            // Only set icon if a valid path is provided
            if (iconPath != null && !iconPath.isEmpty()) {
                iconPath = iconPath.replace("'", "''");
                psScript.append(String.format("$shortcut.IconLocation = '%s';\n", iconPath));
            }

            psScript.append("$shortcut.Save();\n");

            // Write to the temporary script file
            try (FileWriter writer = new FileWriter(tempScript)) {
                writer.write(psScript.toString());
            }

            // Run PowerShell script as admin
            String command = String.format(
                    "powershell -Command \"Start-Process powershell -ArgumentList '-NoProfile -ExecutionPolicy Bypass -File \"%s\"' -Verb RunAs\"",
                    tempScript.getAbsolutePath()
            );

            Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Start Menu shortcut created: " + shortcutPath);
            } else {
                System.err.println("Failed to create shortcut. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ========================================================================
    // Linux Implementation
    // ========================================================================
    private static void addToStartMenuLinux(String appName, String javaPath, String jarFilePath, String iconPath) {
        try {
            // Create .desktop file
            String desktopFileContent = String.format(
                    "[Desktop Entry]\n" +
                            "Name=%s\n" +
                            "Exec=%s -jar %s\n" +
                            "Icon=%s\n" +
                            "Terminal=false\n" +
                            "Type=Application\n" +
                            "Categories=Utility;",
                    appName, javaPath, jarFilePath, iconPath
            );

            // Save to ~/.local/share/applications
            Path desktopFilePath = Paths.get(System.getProperty("user.home"), ".local/share/applications", appName + ".desktop");
            Files.createDirectories(desktopFilePath.getParent());
            Files.write(desktopFilePath, desktopFileContent.getBytes());

            System.out.println("Application menu entry created: " + desktopFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========================================================================
    // Example Usage
    // ========================================================================
    public static void main(String[] args) {
        String appName = "TbvnsAppStore";
        String javaPath = "C:\\Users\\Tbvns\\tbvns\\bin\\java\\bin\\java.exe"; // Windows
        String jarFilePath = "C:\\Users\\Tbvns\\tbvns\\bin\\TbvnsAppStore.jar";
        String iconPath = "C:\\Users\\Tbvns\\OneDrive\\Images\\BigLogo.ico"; // Windows

        // For Linux:
        // String javaPath = "/usr/bin/java";
        // String iconPath = "/opt/tbvns/icon.png";

        addToStartMenu(appName, javaPath, jarFilePath, iconPath);
    }
}