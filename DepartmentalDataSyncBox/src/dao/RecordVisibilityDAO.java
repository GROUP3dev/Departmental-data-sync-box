package dao;

import db.DBConnection;
import model.RecordVisibility;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordVisibilityDAO {

    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    public boolean grantVisibility(RecordVisibility rv) throws SQLException {
        String sql = "INSERT INTO record_visibility (RECORD_ID, DEPARTMENT_ID, GRANTED_BY_USER_ID, GRANTED_AT) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rv.getRecordId());
            stmt.setInt(2, rv.getDepartmentId());
            stmt.setInt(3, rv.getGrantedByUserId());
            stmt.setTimestamp(4, rv.getGrantedAt());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean revokeVisibility(int recordId, int departmentId) throws SQLException {
        String sql = "DELETE FROM record_visibility WHERE RECORD_ID = ? AND DEPARTMENT_ID = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            stmt.setInt(2, departmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Integer> getVisibleDepartmentIds(int recordId) throws SQLException {
        List<Integer> deptIds = new ArrayList<>();
        String sql = "SELECT DEPARTMENT_ID FROM record_visibility WHERE RECORD_ID = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    deptIds.add(rs.getInt("DEPARTMENT_ID"));
                }
            }
        }
        return deptIds;
    }

    public List<RecordVisibility> getAllVisibilities() throws SQLException {
        List<RecordVisibility> list = new ArrayList<>();
        String sql = "SELECT * FROM record_visibility";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RecordVisibility rv = new RecordVisibility();
                rv.setRecordId(rs.getInt("RECORD_ID"));
                rv.setDepartmentId(rs.getInt("DEPARTMENT_ID"));
                rv.setGrantedByUserId(rs.getInt("GRANTED_BY_USER_ID"));
                rv.setGrantedAt(rs.getTimestamp("GRANTED_AT"));
                list.add(rv);
            }
        }
        return list;
    }
}
