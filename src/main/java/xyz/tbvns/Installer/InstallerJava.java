package xyz.tbvns.Installer;

import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import oshi.SystemInfo;
import xyz.tbvns.Constant;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributeView;

public class InstallerJava {
    public static String downloadJava(JProgressBar bar, String dlPath) {
        SystemInfo info = new SystemInfo();
        String arch = getArchitecture();
        String os = getOs(info.getOperatingSystem().getFamily().toLowerCase());
        String url = generateLink(arch, "21", "jre", os);

        return downloadFile(url, dlPath, bar);
    }

    @SneakyThrows
    private static String downloadFile(String fileURL, String savePath, JProgressBar bar) {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileURL).openConnection();
        connection.setInstanceFollowRedirects(false); // Don't automatically follow redirects
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.connect();

        // Handle redirection
        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == 307) {
            String newUrl = connection.getHeaderField("Location");
            fileURL = newUrl;
            connection.disconnect(); // Close previous connection
            connection = (HttpURLConnection) new URL(newUrl).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
        }

        String[] strings = fileURL.split("/");
        savePath += strings[strings.length-1];

        int fileSize = connection.getContentLength();
        SwingUtilities.invokeLater(() -> bar.setMaximum(fileSize));

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            int totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int finalTotalBytesRead = totalBytesRead;
                SwingUtilities.invokeLater(() -> bar.setValue(finalTotalBytesRead));
            }
        }

        connection.disconnect();
        return savePath;
    }


    private static String generateLink(String arch, String feature_version, String image_type, String os) {
        String url = "https://api.adoptium.net/v3/binary/latest/{feature_version}/ga/{os}/{arch}/{image_type}/hotspot/normal/eclipse";
        url = url.replace("{arch}", arch);
        url = url.replace("{feature_version}", feature_version);
        url = url.replace("{image_type}", image_type);
        url = url.replace("{os}", os);
//        url = url.replace("{os}", "linux");
        return url;
    }

    public static String getArchitecture() {
        String arch = System.getProperty("os.arch").toLowerCase();
        switch (arch) {
            case "amd64":
            case "x86_64":
                return "x64";
            case "i386":
            case "i486":
            case "i586":
            case "i686":
            case "x86":
                return "x86";
            case "x32":
                return "x32";
            case "ppc64":
                return "ppc64";
            case "ppc64le":
                return "ppc64le";
            case "s390x":
                return "s390x";
            case "aarch64":
                return "aarch64";
            case "arm":
            case "armv7l":
            case "armhf":
                return "arm";
            case "sparcv9":
                return "sparcv9";
            case "riscv64":
                return "riscv64";
            default:
                return "unknown";
        }
    }

    @SneakyThrows
    public static void renameJava() {
        File javaBin = new File(Constant.resFolder + "/bin");
        File java = null;
        for (String s : javaBin.list()) {
            if (s.contains("jre")) {
                java = new File(javaBin.getPath() + "/" + s);

                if (new File(javaBin.getPath() + "/java").exists()) {
                    File file = new File(javaBin.getPath() + "/java");
                    FileUtils.deleteDirectory(file);
                }
                File njava = new File(javaBin.getPath() + "/java");
                java.renameTo(njava);
                java = njava;

            }
        }

        if (java != null && java.exists()) {
            SystemInfo info = new SystemInfo();
            if (info.getOperatingSystem().getFamily().toLowerCase().contains("win")) {
                Constant.javaBinLocation = java.getPath() + "/bin/javaw.exe";
            } else {
                Constant.javaBinLocation = java.getPath() + "/bin/java";
                new File(Constant.javaBinLocation).setExecutable(true);
            }
        }
        System.out.println(java.getPath());
        System.out.println(Constant.javaBinLocation);
    }

    public static String getOs(String family) {
        if (family.contains("nix") || family.contains("nux")) {
            return "linux";
        } else if (family.toLowerCase().contains("win")) {
            return "windows";
        } else {
            throw new RuntimeException("Os not supported !");
        }
    }
}
