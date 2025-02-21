package xyz.tbvns.Installer;

import org.json.JSONArray;
import org.json.JSONObject;
import oshi.SystemInfo;
import xyz.tbvns.Constant;
import xyz.tbvns.ErrorHandler;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Handler;

public class InstallerAppStore {
    private static void downloadFileFromGitHub(String repoOwner, String repoName, String savePath, JProgressBar bar) throws Exception {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", repoOwner, repoName);
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch release data from GitHub: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (Exception e) {
            ErrorHandler.handle(e, true);
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray assets = jsonResponse.getJSONArray("assets");

        String downloadUrl = null;
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.getJSONObject(i);
            if (asset.getString("name").equals("TbvnsAppStore.jar")) {
                downloadUrl = asset.getString("browser_download_url");
                break;
            }
        }

        if (downloadUrl == null) {
            ErrorHandler.handle(new IOException("Download URL for TbvnsAppStore.jar not found."), true);
        }

        downloadFile(downloadUrl, savePath, bar);
    }

    private static void downloadFile(String fileURL, String savePath, JProgressBar bar) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        // Get the content length to track progress
        int fileLength = connection.getContentLength();

        // Open input stream to read the file
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(savePath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;

            // Initialize progress bar
            bar.setMaximum(fileLength);
            bar.setValue(0);

            // Download the file in chunks and update progress
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Update the progress bar
                int progress = (int) ((totalBytesRead * 100) / fileLength);
                bar.setValue(progress);
            }
        } catch (Exception e) {
            ErrorHandler.handle(e, true);
        } finally {
            connection.disconnect();
        }
    }

    public static void download(JProgressBar bar) {
        String repoOwner = "tbvns";
        String repoName = "TbvnsAppStore";
        String savePath = Constant.resFolder + "/bin/AppStore.jar";

        try {
            downloadFileFromGitHub(repoOwner, repoName, savePath, bar);
        } catch (Exception e) {
            ErrorHandler.handle(e, true);
        }
    }
}
