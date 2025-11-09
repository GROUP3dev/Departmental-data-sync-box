package model;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import db.DBConnection;



public class AuditLog {
    private int auditId;
    private int userId;
    private int recordId;
    private String action;
    private String description;
    private LocalDateTime timestamp;

    // Constructors
    public AuditLog() {}

    public AuditLog(int userId, int recordId, String action, String description) {
        this.userId = userId;
        this.recordId = recordId;
        this.action = action;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // --- CRUD Operations ---

    // CREATE
    public boolean insertAuditLog() {
        String sql = "INSERT INTO AUDIT_LOG (USER_ID, RECORD_ID, ACTION, DESCRIPTION, TIMESTAMP) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt =connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, recordId);
            stmt.setString(3, action);
            stmt.setString(4, description);
            stmt.setTimestamp(5, Timestamp.valueOf(timestamp));
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error inserting audit log: " + e.getMessage());
            return false;
        }
    }

    // READ (All Logs)
    public static List<AuditLog> getAllLogs() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AUDIT_LOG ORDER BY TIMESTAMP DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setAuditId(rs.getInt("AUDIT_ID"));
                log.setUserId(rs.getInt("USER_ID"));
                log.setRecordId(rs.getInt("RECORD_ID"));
                log.setAction(rs.getString("ACTION"));
                log.setDescription(rs.getString("DESCRIPTION"));
                log.setTimestamp(rs.getTimestamp("TIMESTAMP").toLocalDateTime());
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }
        return logs;
    }

    // READ (By User)
    public static List<AuditLog> getLogsByUser(int userId) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM AUDIT_LOG WHERE USER_ID = ? ORDER BY TIMESTAMP DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setAuditId(rs.getInt("AUDIT_ID"));
                log.setUserId(rs.getInt("USER_ID"));
                log.setRecordId(rs.getInt("RECORD_ID"));
                log.setAction(rs.getString("ACTION"));
                log.setDescription(rs.getString("DESCRIPTION"));
                log.setTimestamp(rs.getTimestamp("TIMESTAMP").toLocalDateTime());
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs for user: " + e.getMessage());
        }
        return logs;
    }

    // UPDATE
    public boolean updateAuditLog() {
        String sql = "UPDATE AUDIT_LOG SET ACTION = ?, DESCRIPTION = ? WHERE AUDIT_ID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, action);
            stmt.setString(2, description);
            stmt.setInt(3, auditId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating audit log: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public static boolean deleteAuditLog(int auditId) {
        String sql = "DELETE FROM AUDIT_LOG WHERE AUDIT_ID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, auditId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error deleting audit log: " + e.getMessage());
            return false;
        }
    }

    // --- Utility to Log Actions ---
    public static void logAction(int userId, int recordId, String action, String description) {
        AuditLog log = new AuditLog(userId, recordId, action, description);
        log.insertAuditLog();
    }


    public static void main(String[] args) {

        // 1️⃣ Insert a new audit log
        AuditLog log = new AuditLog(1, 101, "UPDATE", "Updated HR record");
        boolean inserted = log.insertAuditLog();
        System.out.println("Insert audit log: " + (inserted ? "SUCCESS" : "FAILED"));

        // 2️⃣ Fetch all logs
        System.out.println("\nAll Audit Logs:");
        List<AuditLog> logs = AuditLog.getAllLogs();
        for (AuditLog l : logs) {
            System.out.println(
                    l.getAuditId() + " | UserID: " + l.getUserId() +
                            " | RecordID: " + l.getRecordId() +
                            " | Action: " + l.getAction() +
                            " | Desc: " + l.getDescription() +
                            " | Time: " + l.getTimestamp()
            );
        }

        // 3️⃣ Fetch logs by userId = 1
        System.out.println("\nLogs for UserID = 1:");
        List<AuditLog> userLogs = AuditLog.getLogsByUser(1);
        for (AuditLog l : userLogs) {
            System.out.println(l.getAuditId() + " | Action: " + l.getAction() + " | Desc: " + l.getDescription());
        }

        // 4️⃣ Update first log description
        if (!logs.isEmpty()) {
            AuditLog firstLog = logs.get(0);
            firstLog.setDescription("Corrected HR record details");
            boolean updated = firstLog.updateAuditLog();
            System.out.println("\nUpdate first log: " + (updated ? "SUCCESS" : "FAILED"));
        }

        // 5️⃣ Delete last log
        if (!logs.isEmpty()) {
            AuditLog lastLog = logs.get(logs.size() - 1);
            boolean deleted = AuditLog.deleteAuditLog(lastLog.getAuditId());
            System.out.println("Delete last log: " + (deleted ? "SUCCESS" : "FAILED"));
        }

        // 6️⃣ Log an action using the utility
        AuditLog.logAction(1, 101, "DELETE", "Deleted an outdated HR record");
        System.out.println("Logged an action using logAction utility.");
    }

}

