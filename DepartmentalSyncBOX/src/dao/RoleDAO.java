package dao;

import db.DBConnection;
import model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Role entity.
 * Handles CRUD operations on the roles table.
 */
public class RoleDAO {
    private final Connection conn;

    public RoleDAO() {
        this.conn = DBConnection.getConnection();
    }

    // Get a role by its ID
    public Role getRoleById(int roleId) throws SQLException {
        String sql = "SELECT * FROM roles WHERE role_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("description")
                );
            }
        }
        return null;
    }

    // Get all roles
    public List<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("description")
                ));
            }
        }
        return roles;
    }

    // Insert a new role
    public boolean insertRole(Role role) throws SQLException {
        String sql = "INSERT INTO roles (role_name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDescription());
            return stmt.executeUpdate() > 0;
        }
    }

    // Update an existing role
    public boolean updateRole(Role role) throws SQLException {
        String sql = "UPDATE roles SET role_name = ?, description = ? WHERE role_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDescription());
            stmt.setInt(3, role.getRoleId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete a role by ID
    public boolean deleteRole(int roleId) throws SQLException {
        String sql = "DELETE FROM roles WHERE role_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            return stmt.executeUpdate() > 0;
        }
    }
}
