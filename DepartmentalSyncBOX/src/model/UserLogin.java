package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.DBConnection;

public class UserLogin {

    // Method to validate login using email and password
    public static boolean loginUser(String email, String password) {
        String sql = "SELECT * FROM USERS WHERE email = ? AND password_hash = SHA2(?, 256) AND active_flag = 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Login successful! Welcome, " + rs.getString("full_name"));

                // Update last login timestamp after successful login
                updateLastLoginWithoutLogin(email);

                return true;
            } else {
                System.out.println("Invalid email or password.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    // Update the user's last login time without logging in
    public static void updateLastLoginWithoutLogin(String email) {
        String updateSql = "UPDATE USERS SET last_login_at = CURRENT_TIMESTAMP WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateSql)) {

            stmt.setString(1, email);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Last login updated for: " + email);
            } else {
                System.out.println("No user found with email: " + email);
            }

        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }

    // Optional: retrieve user info (for role-based control)
    public static User getUserDetails(String email) {
        String sql = "SELECT user_id, username, full_name, email, department_id, role_id FROM USERS WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setDepartmentId(rs.getInt("department_id"));
                user.setRoleId(rs.getInt("role_id"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user details: " + e.getMessage());
        }
        return null;
    }

    // Test the login and last login update without login
    public static void main(String[] args) {
        // Update last login without password
        updateLastLoginWithoutLogin("admin@example1.com");

        // Optional: fetch user details to verify
        User user = getUserDetails("admin@example1.com");
        if (user != null) {
            System.out.println("User Info after last login update: " + user);
        }
// here is for login to check login

    boolean login =     loginUser("admin@example1.com","admin123");

        if (login){
            System.out.println("Login success full ");
        }else {
            System.out.println("Faied login");
        }
    }
}
