package ui;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import db.DBConnection;
import ui.dialogs.ConnectionConfigDialog;

public class LoginUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoginUI.class.getName());
    private final UserDAO userDAO = new UserDAO();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginUI() {
        setTitle("Departmental Data Sync Box - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        // Check Database Connection on Startup
        checkDatabaseConnection();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("Sync Box Login");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Username:"), gbc);

        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(245, 245, 245));

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnSignup = new JButton("Sign Up");
        btnSignup.setBackground(new Color(40, 167, 69)); // Green
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFocusPainted(false);
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Config Button (Small gear icon or text)
        JButton btnConfig = new JButton("⚙");
        btnConfig.setToolTipText("Database Settings");
        btnConfig.setMargin(new Insets(2, 5, 2, 5));
        btnConfig.addActionListener(e -> showConnectionDialog());

        btnPanel.add(btnLogin);
        btnPanel.add(btnSignup);
        btnPanel.add(btnConfig);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(btnPanel, gbc);

        add(mainPanel);

        // Actions
        btnLogin.addActionListener(e -> performLogin());
        btnSignup.addActionListener(e -> {
            new SignupUI().setVisible(true);
            dispose();
        });
        getRootPane().setDefaultButton(btnLogin);
    }

    private void checkDatabaseConnection() {
        // Run in background to avoid freezing UI
        new Thread(() -> {
            try {
                if (DBConnection.getInstance().getConnection() == null) {
                    SwingUtilities.invokeLater(() -> {
                        int choice = JOptionPane.showConfirmDialog(this,
                                "Cannot connect to Database.\nDo you want to configure connection settings?",
                                "Connection Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                        if (choice == JOptionPane.YES_OPTION) {
                            showConnectionDialog();
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error checking connection", e);
            }
        }).start();
    }

    private void showConnectionDialog() {
        ConnectionConfigDialog dialog = new ConnectionConfigDialog(this);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            JOptionPane.showMessageDialog(this, "Settings saved. Please restart the application.", "Restart Required",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                if (!user.isActive()) {
                    JOptionPane.showMessageDialog(this, "Account is inactive. Contact Admin.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Audit Log: LOGIN
                new dao.AuditLogDAO().log(user.getUserId(), "LOGIN", "User " + user.getUsername() + " logged in.");

                openDashboard(user);
                dispose(); // Close Login Window
            } else {
                // Check if it's a connection error or actual invalid credentials
                if (DBConnection.getInstance().getConnection() == null) {
                    JOptionPane.showMessageDialog(this, "Database connection failed.\nPlease check settings.",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                    showConnectionDialog();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Login error", e);
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        // Role IDs: 1=ADMIN, 2=DEPT_HEAD, 3=REGISTRY, 4=STUDENT
        switch (user.getRoleId()) {
            case 1:
                new AdminDashboard(user).setVisible(true);
                break;
            case 3:
                new RegistryDashboard(user).setVisible(true);
                break;
            case 2:
                new DepartmentHeadDashboard(user).setVisible(true);
                break;
            case 4:
                new StudentDashboard(user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown Role ID: " + user.getRoleId(), "Error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Ensure UI is created on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new LoginUI().setVisible(true);
        });
    }
}