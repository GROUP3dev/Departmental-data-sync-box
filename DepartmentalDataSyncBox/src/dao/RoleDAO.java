package dao;

import model.Role;
import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY role_id";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                // role.setDescription(rs.getString("description")); // If column exists
                roles.add(role);
            }
        }
        return roles;
    }

    public String getRoleName(int roleId) throws SQLException {
        String sql = "SELECT role_name FROM roles WHERE role_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role_name");
                }
            }
        }
        return "UNKNOWN";
    }
}
