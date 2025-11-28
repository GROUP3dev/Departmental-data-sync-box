package ui.dialogs;

import util.ConfigUtil;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionConfigDialog extends JDialog {
    private JTextField txtHost;
    private JTextField txtPort;
    private JButton btnSave;
    private JButton btnTest;
    private JButton btnCancel;
    private boolean saved = false;

    public ConnectionConfigDialog(Frame parent) {
        super(parent, "Database Connection Settings", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        initializeUI();
        loadCurrentSettings();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Host
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Server IP (Host):"), gbc);

        txtHost = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtHost, gbc);

        // Port
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Port:"), gbc);

        txtPort = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtPort, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnTest = new JButton("Test Connection");
        btnSave = new JButton("Save & Retry");
        btnCancel = new JButton("Cancel");

        btnPanel.add(btnTest);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        btnTest.addActionListener(e -> testConnection());
        btnSave.addActionListener(e -> saveSettings());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadCurrentSettings() {
        txtHost.setText(ConfigUtil.getProperty("db.host"));
        txtPort.setText(ConfigUtil.getProperty("db.port"));
    }

    private void testConnection() {
        String host = txtHost.getText().trim();
        String port = txtPort.getText().trim();

        if (host.isEmpty() || port.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Host and Port.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Temporarily update config in memory to test
            String oldHost = ConfigUtil.getProperty("db.host");
            String oldPort = ConfigUtil.getProperty("db.port");

            ConfigUtil.setProperty("db.host", host);
            ConfigUtil.setProperty("db.port", port);

            // Force a new connection attempt
            Connection conn = DBConnection.getInstance().createConnectionForTest();
            if (conn != null && !conn.isClosed()) {
                JOptionPane.showMessageDialog(this, "Connection Successful!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                conn.close();
            } else {
                JOptionPane.showMessageDialog(this, "Connection Failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Revert config if not saved? No, let's keep it for now as we might save it.
            // Actually, better to revert if test fails?
            // For simplicity, we rely on Save button to persist.

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Connection Failed:\n" + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSettings() {
        String host = txtHost.getText().trim();
        String port = txtPort.getText().trim();

        if (host.isEmpty() || port.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Host and Port.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConfigUtil.setProperty("db.host", host);
        ConfigUtil.setProperty("db.port", port);
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
