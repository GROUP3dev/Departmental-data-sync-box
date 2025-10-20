package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.User;

public class LoginUI extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;

    // Simulated user "database"
    private Map<String, User> users = new HashMap<>();

    public LoginUI() {
        setTitle("Login");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));

        // Sample hardcoded users
        users.put("admin", new User(1, "admin", "1234", "admin"));
        users.put("staff", new User(2, "staff", "1234", "staff"));
        users.put("user", new User(3, "user", "1234", "user"));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cmbRole = new JComboBox<>(new String[]{"Admin", "Staff", "User"});
        btnLogin = new JButton("Login");

        add(new JLabel("Username:"));
        add(txtUsername);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Role:"));
        add(cmbRole);
        add(new JLabel());
        add(btnLogin);

        btnLogin.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String selectedRole = cmbRole.getSelectedItem().toString().toLowerCase();

        if (users.containsKey(username)) {
            User user = users.get(username);

            if (user.getPassword().equals(password) && user.getRole().equalsIgnoreCase(selectedRole)) {
                JOptionPane.showMessageDialog(this, "Welcome, " + user.getUsername() + "!");
                dispose();

                switch (user.getRole().toLowerCase()) {
                    case "admin":
                        new AdminDashboard(user).setVisible(true);
                        break;
                    case "staff":
                        new StaffDashboard(user).setVisible(true);
                        break;
                    default:
                        new UserDashboard(user).setVisible(true);
                        break;
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid username, password, or role!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
