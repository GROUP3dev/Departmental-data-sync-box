package dao;

import db.DBConnection;
import model.AuditLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuditLogDAO {
    private static final Logger LOGGER = Logger.getLogger(AuditLogDAO.class.getName());

    private static final String INSERT_SQL = "INSERT INTO audit_log (USER_ID, EVENT_TYPE, DETAILS, LOG_TIMESTAMP) VALUES (?, ?, ?, NOW())";
    private static final String SELECT_ALL = "SELECT * FROM audit_log ORDER BY LOG_TIMESTAMP DESC LIMIT 100";

    public void log(int userId, String action, String details) {
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, userId);
            stmt.setString(2, action); // Maps to EVENT_TYPE
            stmt.setString(3, details);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to write audit log", e);
        }
    }

    public List<AuditLog> getRecentLogs() {
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getLong("LOG_ID"));
                log.setUserId(rs.getInt("USER_ID"));
                log.setAction(rs.getString("EVENT_TYPE"));
                log.setDetails(rs.getString("DETAILS"));
                log.setTimestamp(rs.getTimestamp("LOG_TIMESTAMP"));
                logs.add(log);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching audit logs", e);
        }
        return logs;
    }
}
