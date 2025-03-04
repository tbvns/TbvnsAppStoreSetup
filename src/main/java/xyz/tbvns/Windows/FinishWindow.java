package xyz.tbvns.Windows;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import oshi.SystemInfo;
import xyz.tbvns.Constant;
import xyz.tbvns.Installer.InstallerJava;
import xyz.tbvns.Main;
import xyz.tbvns.Others.StartMenuManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class FinishWindow {
    @SneakyThrows
    public void show() {
        JFrame frame = new JFrame("Finishing installation...");
        frame.setSize(400, 350);
        frame.setResizable(false);
        WindowUtils.center(frame);
        frame.setIconImage(ImageIO.read(Main.class.getResource("/logo.png")));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.setContentPane(panel);

        InputStream textStream = getClass().getResourceAsStream("/FinishDesc");
        String descriptionText = IOUtils.toString(textStream, "UTF-8");
        JLabel description = new JLabel(descriptionText);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(description);


        JCheckBox addToMenu = new JCheckBox("Add to start menu"){{
            setSelected(true);
            setAlignmentX(0.5f);
        }};
        JCheckBox start = new JCheckBox("Start tbvns's app store"){{
            setSelected(true);
            setAlignmentX(0.5f);
        }};

        panel.add(addToMenu);
        panel.add(start);

        JButton button = new JButton("Finish"){{
            setAlignmentX(0.5f);
        }};
        frame.add(button);

        button.addActionListener(actionEvent -> {
            if (addToMenu.isSelected()) {
                String os = InstallerJava.getOs(new SystemInfo().getOperatingSystem().getFamily());
                if (os.contains("win")) {
                    StartMenuManager.addToStartMenu(
                            "Tbvns's app store",
                            Constant.javaBinLocation,
                            Constant.resFolder + "/bin/AppStore.jar",
                            Constant.resFolder + "/bin/AppStore.ico"
                    );
                } else {
                    StartMenuManager.addToStartMenu(
                            "Tbvns's app store",
                            Constant.javaBinLocation,
                            Constant.resFolder + "/bin/AppStore.jar",
                            Constant.resFolder + "/bin/AppStore.png"
                    );
                }
            }
            if (start.isEnabled()) {
                try {
                    new ProcessBuilder()
                        .command(
                            Constant.javaBinLocation,
                            "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED",
                            "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
                            "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED",
                            "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
                            "--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED",
                            "--add-opens=java.base/java.lang=ALL-UNNAMED",
                            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
                            "--add-opens=java.base/java.io=ALL-UNNAMED",
                            "--add-opens=java.base/java.util=ALL-UNNAMED",
                            "-jar",
                            Constant.resFolder + "/bin/AppStore.jar"
                        ).start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Runtime.getRuntime().exit(0);
        });

        frame.setVisible(true);
    }
}
