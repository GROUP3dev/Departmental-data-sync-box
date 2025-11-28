package ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class AddUserDialog extends JDialog {
    public AddUserDialog(JFrame parent) {
        super(parent, "Add New User", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        add(new JLabel("Add User Dialog Stub: Collect User/Role/Dept info", SwingConstants.CENTER), BorderLayout.CENTER);
        setVisible(false); // Controlled by AdminDashboard
    }
}