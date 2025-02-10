package xyz.tbvns.Windows;

import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class WelcomeWindow {
    private JPanel main;
    private JLabel logo;
    private JButton cancelButton;
    private JButton installButton;
    private JPanel textArea;
    private JFrame frame;

    public WelcomeWindow() {
        initializeMainPanel();
        setupButtons();
    }

    private void initializeMainPanel() {
        // Main panel setup (BorderLayout, size constraints)
        main = new JPanel(new BorderLayout(0, 0));
        main.setMaximumSize(new Dimension(500, 400));
        main.setMinimumSize(new Dimension(500, 400));
        main.setPreferredSize(new Dimension(500, 400));

        // Create and add components for each region
        main.add(createNorthPanel(), BorderLayout.NORTH);
        main.add(createSouthPanel(), BorderLayout.SOUTH);
        main.add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createNorthPanel() {
        // North panel with logo (FlowLayout centered)
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        northPanel.setBorder(null);

        // Logo setup with image
        logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(200, 200));
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/logo.png"));
            Image scaled = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
        } catch (IOException e) {
            e.printStackTrace();
        }
        northPanel.add(logo);

        return northPanel;
    }

    private JPanel createSouthPanel() {
        // South panel with buttons (FlowLayout centered)
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        southPanel.setBorder(null);

        cancelButton = new JButton("Cancel");
        installButton = new JButton("Install");
        southPanel.add(cancelButton);
        southPanel.add(installButton);

        return southPanel;
    }

    private JPanel createCenterPanel() {
        // Center panel (textArea) with scrollable text
        textArea = new JPanel();
        textArea.setLayout(new BoxLayout(textArea, BoxLayout.PAGE_AXIS));
        textArea.setBorder(null);

        try {
            InputStream textStream = getClass().getResourceAsStream("/WelcomeDesc");
            String descriptionText = IOUtils.toString(textStream, "UTF-8");
            JLabel description = new JLabel(descriptionText);
            description.setAlignmentX(Component.CENTER_ALIGNMENT);
            textArea.add(description);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return textArea;
    }

    private void setupButtons() {
        cancelButton.addActionListener(e -> System.exit(0));
        installButton.addActionListener(e -> {
            frame.setVisible(false);
            DownloadWindow.open();
        });
    }

    public void show() {
        frame = new JFrame("TBVNS's app store setup");
        frame.setContentPane(main);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack(); // Use preferred sizes from components
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);
    }
}