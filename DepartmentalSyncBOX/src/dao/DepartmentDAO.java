package dao;

import db.DBConnection;
import model.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    // Add department
    public void addDepartment(Department d) {
        String sql = "INSERT INTO departments (id, name, location) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, d.getId());
            stmt.setString(2, d.getName());
            stmt.setString(3, d.getLocation());
            stmt.executeUpdate();
            System.out.println("Department added: " + d);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all departments
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Department d = new Department(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location")
                );
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
