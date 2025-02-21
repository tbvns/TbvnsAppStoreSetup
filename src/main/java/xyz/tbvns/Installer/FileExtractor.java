package xyz.tbvns.Installer;


import net.lingala.zip4j.ZipFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import xyz.tbvns.Constant;

import java.io.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.compressors.gzip.*;
import xyz.tbvns.ErrorHandler;

import java.io.*;
import java.util.zip.*;
import java.nio.file.*;

public class FileExtractor {

    public static void extractJava(String path) throws IOException {
        File file = new File(path);
        String extension = getFileExtension(file);

        File javaBin = new File(Constant.resFolder + "/bin");
        javaBin.mkdirs();

        if (extension.equalsIgnoreCase("zip")) {
            // Extract .zip file
            try (ZipFile zipFile = new ZipFile(file)) {
                zipFile.extractAll(javaBin.getPath());
            } catch (Exception e) {
                ErrorHandler.handle(e, true);
            }
        } else if (extension.equalsIgnoreCase("tar.gz") || path.endsWith(".tar.gz")) {
            // Extract .tar.gz file
            extractTarGz(file, javaBin);
        } else {
            System.out.println("Unsupported file type: " + extension);
        }
    }

    // Helper function to get file extension
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    // Method to extract .tar.gz files
    private static void extractTarGz(File file, File destination) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             GzipCompressorInputStream gis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gis)) {

            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                File outFile = new File(destination, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    try (OutputStream out = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = tarIn.read(buffer)) != -1) {
                            out.write(buffer, 0, length);
                        }
                    } catch (Exception e) {
                        ErrorHandler.handle(e, true);
                    }
                }
            }
        } catch (Exception e) {
            ErrorHandler.handle(e, true);
        }
    }
}
