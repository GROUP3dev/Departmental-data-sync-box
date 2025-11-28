package dao;

import db.DBConnection;
import model.SyncLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SyncLogDAO {

    private Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    public boolean logSyncEvent(SyncLog log) {
        String sql = "INSERT INTO sync_logs (SYNC_TIMESTAMP, REMOTE_ACTOR_ID, DIRECTION, STATUS, ITEMS_PROCESSED, ITEMS_CONFLICTED, DETAILS) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, log.getSyncTimestamp());
            stmt.setString(2, log.getRemoteActorId());
            stmt.setString(3, log.getDirection());
            stmt.setString(4, log.getStatus());
            stmt.setInt(5, log.getItemsProcessed());
            stmt.setInt(6, log.getItemsConflicted());
            stmt.setString(7, log.getDetails());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        log.setLogId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SyncLog> getRecentLogs() {
        List<SyncLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM sync_logs ORDER BY SYNC_TIMESTAMP DESC LIMIT 50";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SyncLog log = new SyncLog();
                log.setLogId(rs.getLong("LOG_ID"));
                log.setSyncTimestamp(rs.getTimestamp("SYNC_TIMESTAMP"));
                log.setRemoteActorId(rs.getString("REMOTE_ACTOR_ID"));
                log.setDirection(rs.getString("DIRECTION"));
                log.setStatus(rs.getString("STATUS"));
                log.setItemsProcessed(rs.getInt("ITEMS_PROCESSED"));
                log.setItemsConflicted(rs.getInt("ITEMS_CONFLICTED"));
                log.setDetails(rs.getString("DETAILS"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
