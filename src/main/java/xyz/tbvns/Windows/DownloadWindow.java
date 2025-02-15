package xyz.tbvns.Windows;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import oshi.SystemInfo;
import xyz.tbvns.Constant;
import xyz.tbvns.Main;
import xyz.tbvns.Others.AutoStart;
import xyz.tbvns.Installer.FileExtractor;
import xyz.tbvns.Installer.InstallerAppStore;
import xyz.tbvns.Installer.InstallerJava;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class DownloadWindow {
    @SneakyThrows
    public static void open() {
        JFrame frame = new JFrame();
        frame.setTitle("Installing TBVNS's app store...");
        frame.setSize(300, 100);
        frame.setResizable(false);
        WindowUtils.center(frame);
        frame.setIconImage(ImageIO.read(Main.class.getResource("/logo.png")));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));  // Adds margin around the panel

        Label info = new Label("Starting...");

        JProgressBar bar = new JProgressBar();
        bar.setSize(bar.getWidth() - 20, bar.getHeight());

        panel.add(info, 0);
        panel.add(bar, 1);

        frame.setContentPane(panel);
        frame.setVisible(true);

        startInstallProcess(frame, bar, info);
    }

    @SneakyThrows
    public static void startInstallProcess(Frame frame, JProgressBar bar, Label label) {


        Thread firstStep = new Thread(() -> {
            setupJava(bar, label);
            setupAppStore(bar, label);

            frame.setVisible(false);
            new FinishWindow().show();
        });
        firstStep.start();
    }

    @SneakyThrows
    public static void setupJava(JProgressBar bar, Label label) {
        label.setText("Downloading java 21...");
        String tmpFolder = Constant.resFolder + "/tmp";
        new File(tmpFolder).mkdirs();
        String savePath = tmpFolder + "/";
        savePath = InstallerJava.downloadJava(bar, savePath);
        label.setText("Extracting java 21...");
        FileExtractor.extractJava(savePath);
        InstallerJava.renameJava();
    }

    public static void setupAppStore(JProgressBar bar, Label label) {
        label.setText("Downloading app store...");
        InstallerAppStore.download(bar);
        label.setText("Setting up auto start...");
        bar.setMaximum(1);
        bar.setValue(1);
        AutoStart.addToStartup(Constant.javaBinLocation, Constant.resFolder + "/bin/AppStore.jar");
        copyLogo();
    }

    @SneakyThrows
    public static void copyLogo() {
        String os = InstallerJava.getOs(new SystemInfo().getOperatingSystem().getFamily());
        if (os.contains("nix") || os.contains("nux")) {
            InputStream stream = Main.class.getResourceAsStream("/logo.png");
            FileUtils.copyToFile(stream, new File(Constant.resFolder + "/bin/AppStore.png"));
        } else {
            InputStream stream = Main.class.getResourceAsStream("/logo.ico");
            FileUtils.copyToFile(stream, new File(Constant.resFolder + "/bin/AppStore.ico"));
        }
    }
}
