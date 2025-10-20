package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDialog extends JDialog {

    private JTextField txtId = new JTextField(5);
    private JTextField txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15);
    private JComboBox<String> comboRole = new JComboBox<>(new String[]{"Admin", "Staff"});
    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    private UserDAO userDAO = new UserDAO();
    private boolean isEditMode = false;
    private User existingUser;

    public UserDialog(Frame parent, String title, User userToEdit) {
        super(parent, title, true);
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("User ID:"));
        add(txtId);
        add(new JLabel("Username:"));
        add(txtUsername);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Role:"));
        add(comboRole);

        add(btnSave);
        add(btnCancel);

        if (userToEdit != null) {
            isEditMode = true;
            existingUser = userToEdit;
            populateFields(userToEdit);
            txtId.setEditable(false); // ID cannot change when editing
        }

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUser();
            }
        });

        btnCancel.addActionListener(e -> dispose());
    }

    private void populateFields(User user) {
        txtId.setText(String.valueOf(user.getId()));
        txtUsername.setText(user.getUsername());
        txtPassword.setText(user.getPassword());
        comboRole.setSelectedItem(user.getRole());
    }

    private void saveUser() {
        int id = Integer.parseInt(txtId.getText());
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String role = (String) comboRole.getSelectedItem();

        User user = new User(id, username, password, role);

        if (isEditMode) {
            userDAO.updateUser(user);
            JOptionPane.showMessageDialog(this, "User updated successfully!");
        } else {
            userDAO.addUser(user);
            JOptionPane.showMessageDialog(this, "User added successfully!");
        }

        dispose();
    }
}
