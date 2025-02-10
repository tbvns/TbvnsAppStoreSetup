package xyz.tbvns.Others;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.WinUser;
import xyz.tbvns.Main;

import java.io.File;
import java.net.URI;

public class AdminUtils {

    public static boolean isAdmin() {
        return Advapi32Util.isCurrentProcessElevated();
    }

    public static boolean restartAsAdmin() {
        try {
            // Get JAR path
            URI jarUri = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            String jarPath = new File(jarUri).getAbsolutePath();

            // Verify we're running from a JAR
            if (!jarPath.toLowerCase().endsWith(".jar") || !new File(jarPath).isFile()) {
                System.err.println("Admin restart requires JAR execution");
                return false;
            }

            // Get Java executable path
            String javaPath = System.getProperty("java.home") +
                    File.separator + "bin" +
                    File.separator + "java.exe";

            // Verify Java exists
            if (!new File(javaPath).exists()) {
                System.err.println("Java executable not found: " + javaPath);
                return false;
            }

            // Get working directory
            String workingDir = new File(jarPath).getParent();

            // Execute with elevation
            Pointer result = Shell32.INSTANCE.ShellExecute(
                    null,
                    "runas",
                    javaPath,
                    "-jar \"" + jarPath + "\"",
                    workingDir,
                    WinUser.SW_NORMAL
            ).toPointer();

            // Check for ShellExecute success (HINSTANCE > 32)
            if (Pointer.nativeValue(result) <= 32) {
                System.err.println("Failed to elevate privileges. Error: " + Pointer.nativeValue(result));
                return false;
            }

            // Exit current instance
            System.exit(0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}