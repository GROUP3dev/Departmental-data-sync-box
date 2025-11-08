package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private User loggedInUser;

    public UserDashboard(User user) {
        this.loggedInUser = user;

        setTitle("User Dashboard - " + loggedInUser.getUsername());
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome User: " + loggedInUser.getUsername(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.CENTER);

        // Add more components for users as needed
        JLabel infoLabel = new JLabel("You can view your information here.", SwingConstants.CENTER);
        add(infoLabel, BorderLayout.SOUTH);
    }
}
