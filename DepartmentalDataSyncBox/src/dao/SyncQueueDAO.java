package dao;

import db.DBConnection;
import model.SyncQueue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncQueueDAO {
    private static final Logger LOGGER = Logger.getLogger(SyncQueueDAO.class.getName());

    private static final String SELECT_PENDING = "SELECT * FROM sync_queue WHERE STATUS = 'PENDING' ORDER BY QUEUE_ID ASC LIMIT ?";
    private static final String UPDATE_STATUS = "UPDATE sync_queue SET STATUS = ?, RETRY_COUNT = RETRY_COUNT + 1 WHERE QUEUE_ID = ?";

    public List<SyncQueue> getPendingItems(int limit) {
        List<SyncQueue> items = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_PENDING)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SyncQueue item = new SyncQueue();
                    item.setQueueId(rs.getLong("QUEUE_ID"));
                    item.setSyncUUID(rs.getString("SYNC_UUID"));
                    item.setEntityName(rs.getString("ENTITY_NAME"));
                    item.setOperationType(rs.getString("OPERATION_TYPE"));
                    item.setPayloadJson(rs.getString("PAYLOAD_JSON"));
                    item.setStatus(rs.getString("STATUS"));
                    item.setRetryCount(rs.getInt("RETRY_COUNT"));
                    item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    item.setLastModifiedTs(rs.getLong("LAST_MODIFIED_TS"));
                    item.setSourceActorId(rs.getString("SOURCE_ACTOR_ID"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching pending sync items", e);
        }
        return items;
    }

    public List<SyncQueue> getAllQueueItems(int limit) {
        List<SyncQueue> items = new ArrayList<>();
        String sql = "SELECT * FROM sync_queue ORDER BY QUEUE_ID DESC LIMIT ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SyncQueue item = new SyncQueue();
                    item.setQueueId(rs.getLong("QUEUE_ID"));
                    item.setSyncUUID(rs.getString("SYNC_UUID"));
                    item.setEntityName(rs.getString("ENTITY_NAME"));
                    item.setOperationType(rs.getString("OPERATION_TYPE"));
                    item.setPayloadJson(rs.getString("PAYLOAD_JSON"));
                    item.setStatus(rs.getString("STATUS"));
                    item.setRetryCount(rs.getInt("RETRY_COUNT"));
                    item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                    item.setLastModifiedTs(rs.getLong("LAST_MODIFIED_TS"));
                    item.setSourceActorId(rs.getString("SOURCE_ACTOR_ID"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all sync items", e);
        }
        return items;
    }

    public List<SyncQueue> getFailedItems() {
        List<SyncQueue> items = new ArrayList<>();
        String sql = "SELECT * FROM sync_queue WHERE STATUS = 'FAILED' ORDER BY QUEUE_ID ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SyncQueue item = new SyncQueue();
                item.setQueueId(rs.getLong("QUEUE_ID"));
                item.setSyncUUID(rs.getString("SYNC_UUID"));
                item.setEntityName(rs.getString("ENTITY_NAME"));
                item.setOperationType(rs.getString("OPERATION_TYPE"));
                item.setPayloadJson(rs.getString("PAYLOAD_JSON"));
                item.setStatus(rs.getString("STATUS"));
                item.setRetryCount(rs.getInt("RETRY_COUNT"));
                item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
                item.setLastModifiedTs(rs.getLong("LAST_MODIFIED_TS"));
                item.setSourceActorId(rs.getString("SOURCE_ACTOR_ID"));
                items.add(item);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching failed sync items", e);
        }
        return items;
    }

    public boolean updateStatus(long queueId, String newStatus) {
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS)) {

            stmt.setString(1, newStatus);
            stmt.setLong(2, queueId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating sync queue status", e);
            return false;
        }
    }
}
