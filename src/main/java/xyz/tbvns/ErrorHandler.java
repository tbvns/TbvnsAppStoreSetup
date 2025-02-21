package xyz.tbvns;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tbvns.Windows.WindowUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

@Slf4j
public class ErrorHandler {
    @SneakyThrows
    public static void handle(Exception e, boolean isFatal) {
        JFrame frame = new JFrame("Error: " + e.getMessage());
        frame.setSize(600, 400);
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.setIconImage(ImageIO.read(Main.class.getResource("/logo.png")));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        JLabel errorLabel = new JLabel("Error: " + e.getMessage()) {{
            setIcon(UIManager.getIcon("OptionPane.errorIcon"));
            setBorder(new EmptyBorder(10, 5, 10, 10));
            setFont(getFont().deriveFont(20F));
            if (isFatal) {
                setText("Fatal error: " + e.getMessage());
            }
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }};
        labelPanel.add(errorLabel);
        labelPanel.add(Box.createHorizontalGlue());
        panel.add(labelPanel);

        JTextArea area = new JTextArea(){{
            setEditable(false);
            setRows(10);
        }};
//        Arrays.stream(e.getStackTrace()).map(el -> el.toString() + "\n").forEach(area::append);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stream));
        String trace = new String(stream.toByteArray());
        area.append(trace);
        log.error(trace);

        panel.add(new JScrollPane(area){{
            setPreferredSize(new Dimension(300, 300));
            setBorder(new EmptyBorder(0, 0, 10, 0));
        }});

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton button = new JButton("Close") {{
            addActionListener(a -> {
                if (isFatal) {
                    Runtime.getRuntime().exit(1);
                }
                frame.dispose();
            });

            if (isFatal) {
                setBackground(new Color(199, 84, 80));
                setText("End program");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            }
            setAlignmentX(Component.RIGHT_ALIGNMENT);
        }};

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(button);

        panel.add(buttonPanel);
        frame.setContentPane(panel);
        WindowUtils.center(frame);
        frame.setVisible(true);

        if (isFatal) {
            Thread.currentThread().join();
        }
    }

    @SneakyThrows
    public static void warn(String message) {
        log.warn(message);
        JFrame frame = new JFrame("Waning");
        frame.setIconImage(ImageIO.read(Main.class.getResource("/logo.png")));
        new Thread(() -> {
            JOptionPane.showMessageDialog(frame,
                    message,
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }).start();
    }
}
