package xyz.tbvns.Windows;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import xyz.tbvns.Constant;
import xyz.tbvns.Others.StartMenuManager;

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
                StartMenuManager.addToStartMenu("Tbvns's app store", Constant.javaBinLocation, Constant.resFolder + "/bin/AppStore.jar", Constant.resFolder + "/bin/AppStore.ico");
            }
            if (start.isEnabled()) {
                try {
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.command(Constant.javaBinLocation, "-jar", Constant.resFolder + "/bin/AppStore.jar");
                    builder.start();
//                    Runtime.getRuntime().exit(0);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        frame.setVisible(true);
    }
}
