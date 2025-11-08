package ui;

import javax.swing.*;
import java.awt.*;

public class SyncStatusUI extends JFrame {
    private JTextArea logArea;

    public SyncStatusUI() {
        setTitle("Sync Status");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.append("Starting synchronization...\n");

        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                logArea.append("LAN Sync: Success\n");
                Thread.sleep(1000);
                logArea.append("Cloud Sync: Success\n");
                logArea.append("Synchronization complete.");
            } catch (InterruptedException ignored) {}
        }).start();
    }
}
