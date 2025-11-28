package ui;

import dao.UserDAO;
import model.User;
import util.EncryptionUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignupUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(SignupUI.class.getName());

    private final UserDAO userDAO = new UserDAO();
    private final dao.RoleDAO roleDAO = new dao.RoleDAO();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JComboBox<String> cbRole;
    private JTextField txtDeptId;
    private JButton btnRegister;
    private JButton btnBack;

    public SignupUI() {
        setTitle("Departmental Data Sync Box - Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("Create New Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Fields
        int y = 1;

        mainPanel.add(new JLabel("First Name:"), getGbc(0, y));
        txtFirstName = new JTextField(20);
        mainPanel.add(txtFirstName, getGbc(1, y++));

        mainPanel.add(new JLabel("Last Name:"), getGbc(0, y));
        txtLastName = new JTextField(20);
        mainPanel.add(txtLastName, getGbc(1, y++));

        mainPanel.add(new JLabel("Email:"), getGbc(0, y));
        txtEmail = new JTextField(20);
        mainPanel.add(txtEmail, getGbc(1, y++));

        mainPanel.add(new JLabel("Username:"), getGbc(0, y));
        txtUsername = new JTextField(20);
        mainPanel.add(txtUsername, getGbc(1, y++));

        mainPanel.add(new JLabel("Password:"), getGbc(0, y));
        txtPassword = new JPasswordField(20);
        mainPanel.add(txtPassword, getGbc(1, y++));

        mainPanel.add(new JLabel("Confirm Password:"), getGbc(0, y));
        txtConfirmPassword = new JPasswordField(20);
        mainPanel.add(txtConfirmPassword, getGbc(1, y++));

        mainPanel.add(new JLabel("Role:"), getGbc(0, y));
        // 1=ADMIN, 2=DEPT_HEAD, 3=REGISTRY, 4=STUDENT
        cbRole = new JComboBox<>();
        try {
            java.util.List<model.Role> roles = roleDAO.findAll();
            for (model.Role r : roles) {
                cbRole.addItem(r.toString());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading roles", e);
        }
        mainPanel.add(cbRole, getGbc(1, y++));

        mainPanel.add(new JLabel("Department ID:"), getGbc(0, y));
        txtDeptId = new JTextField("1", 20); // Default to 1
        mainPanel.add(txtDeptId, getGbc(1, y++));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(new Color(245, 245, 245));

        btnRegister = new JButton("Register");
        btnRegister.setBackground(new Color(0, 120, 215));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnBack = new JButton("Back to Login");
        btnBack.setBackground(Color.GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnPanel.add(btnRegister);
        btnPanel.add(btnBack);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(btnPanel, gbc);

        add(mainPanel);

        // Actions
        btnRegister.addActionListener(e -> performRegistration());
        btnBack.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });
    }

    private GridBagConstraints getGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void performRegistration() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String deptIdStr = txtDeptId.getText().trim();

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()
                || deptIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int deptId = Integer.parseInt(deptIdStr);
            String selectedRole = (String) cbRole.getSelectedItem();
            int roleId = Integer.parseInt(selectedRole.split(" ")[0]);

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(EncryptionUtil.hashPassword(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setRoleId(roleId);
            user.setDepartmentId(deptId);
            user.setActive(true); // Default to active

            if (userDAO.create(user)) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now login.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new LoginUI().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Username might be taken.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Department ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Registration error", e);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
