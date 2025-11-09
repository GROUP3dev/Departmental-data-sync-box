package model;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Role class with methods to insert, update, and delete roles from the database.
 */
public class Role {
    private int roleId;
    private String roleName;
    private String description;

    // Constructors
    public Role() {}

    public Role(int roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
    }

    // Getters and Setters
    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Main method for testing insert, update, delete
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose operation: 1 = Insert, 2 = Update, 3 = Delete");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1 -> insertRole(scanner);
            case 2 -> updateRole(scanner);
            case 3 -> deleteRole(scanner);
            default -> System.out.println("Invalid choice.");
        }

        DBConnection.closeConnection();
        scanner.close();
    }

    private static void insertRole(Scanner scanner) {
        System.out.print("Enter role name: ");
        String roleName = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        String sql = "INSERT INTO roles (role_name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            stmt.setString(2, description);

            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Role inserted successfully." : "Insert failed.");

        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }

    private static void updateRole(Scanner scanner) {
        System.out.print("Enter role ID to update: ");
        int roleId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter new role name: ");
        String roleName = scanner.nextLine();

        System.out.print("Enter new description: ");
        String description = scanner.nextLine();

        String sql = "UPDATE roles SET role_name = ?, description = ? WHERE role_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roleName);
            stmt.setString(2, description);
            stmt.setInt(3, roleId);

            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Role updated successfully." : "Update failed.");

        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    private static void deleteRole(Scanner scanner) {
        System.out.print("Enter role ID to delete: ");
        int roleId = scanner.nextInt();

        String sql = "DELETE FROM roles WHERE role_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roleId);

            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "Role deleted successfully." : "Delete failed.");

        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }
}
