package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.DBConnection;

public class UserRegister {

    // Register a new user
    public static boolean registerUser(String username, String password, String fullName, String email,
                                       int departmentId, int roleId) {

        // Check if user already exists
        if (isUserExists(username, email)) {
            System.err.println(" Registration failed: Username or Email already exists.");
            return false;
        }

        String sql = "INSERT INTO USERS (username, password_hash, full_name, email, department_id, role_id, created_at, active_flag) "
                + "VALUES (?, SHA2(?, 256), ?, ?, ?, ?, NOW(), 1)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setString(4, email);
            stmt.setInt(5, departmentId);
            stmt.setInt(6, roleId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println(" User registered successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println(" Error registering user: " + e.getMessage());
        }

        return false;
    }

    // Check if username or email already exists
    private static boolean isUserExists(String username, String email) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE username = ? OR email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println(" Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    // For testing registration directly in this class
    public static void main(String[] args) {
        //  Example: Register a new admin user
        boolean result = registerUser(
                "josepha",
                "admin123",
                "JOSEPHA ",
                "josepha@example.com",
                1,   // department_id
                1    // role_id
        );

        if (result) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed.");
        }
    }
}
