package dao;

import db.DBConnection;
import util.SerializationUtil;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract Base DAO that handles:
 * 1. Database Connections
 * 2. Transaction Management
 * 3. Automatic Sync Queueing
 */
public abstract class BaseDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(BaseDAO.class.getName());

    protected abstract String getTableName();

    // Abstract methods for CRUD that subclasses must implement
    public abstract boolean create(T entity) throws SQLException;

    public abstract boolean update(T entity) throws SQLException;

    public abstract boolean softDelete(String syncUUID) throws SQLException;

    public abstract T findBySyncUUID(String syncUUID) throws SQLException;

    protected Connection getConnection() {
        return DBConnection.getInstance().getConnection();
    }

    /**
     * Executes an update (INSERT, UPDATE, DELETE) and queues a sync event in the
     * SAME transaction.
     */
    protected int executeUpdateAndQueue(String sql, Object[] params, T entity, String operationType)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement queueStmt = null;
        int affectedRows = 0;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Execute the main operation
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            affectedRows = stmt.executeUpdate();

            // 2. Queue for Sync (if successful)
            if (affectedRows > 0) {
                String syncUUID = getSyncUUID(entity);
                long lastModifiedTs = getLastModifiedTs(entity);
                String sourceActorId = getSourceActorId(entity);
                // Use Java Serialization (Base64) instead of JSON
                String payloadBase64 = SerializationUtil.serialize(entity);

                String queueSql = "INSERT INTO sync_queue (SYNC_UUID, ENTITY_NAME, OPERATION_TYPE, PAYLOAD_JSON, LAST_MODIFIED_TS, SOURCE_ACTOR_ID, STATUS) VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";
                queueStmt = conn.prepareStatement(queueSql);
                queueStmt.setString(1, syncUUID);
                queueStmt.setString(2, getEntityName(entity));
                queueStmt.setString(3, operationType);
                queueStmt.setString(4, payloadBase64); // Storing Base64 in longtext field
                queueStmt.setLong(5, lastModifiedTs);
                queueStmt.setString(6, sourceActorId);

                queueStmt.executeUpdate();
            }

            conn.commit(); // Commit Transaction
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", ex);
                }
            }
            throw e;
        } finally {
            if (stmt != null)
                stmt.close();
            if (queueStmt != null)
                queueStmt.close();
            if (conn != null)
                conn.setAutoCommit(true); // Reset
            if (conn != null)
                conn.close();
        }
        return affectedRows;
    }

    protected void addToSyncQueue(String entityName, String operationType, T entity) throws SQLException {
        String syncUUID = getSyncUUID(entity);
        long lastModifiedTs = getLastModifiedTs(entity);
        String sourceActorId = getSourceActorId(entity);
        String payloadBase64 = SerializationUtil.serialize(entity);

        String queueSql = "INSERT INTO sync_queue (SYNC_UUID, ENTITY_NAME, OPERATION_TYPE, PAYLOAD_JSON, LAST_MODIFIED_TS, SOURCE_ACTOR_ID, STATUS) VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = getConnection();
                PreparedStatement queueStmt = conn.prepareStatement(queueSql)) {
            queueStmt.setString(1, syncUUID);
            queueStmt.setString(2, entityName);
            queueStmt.setString(3, operationType);
            queueStmt.setString(4, payloadBase64);
            queueStmt.setLong(5, lastModifiedTs);
            queueStmt.setString(6, sourceActorId);
            queueStmt.executeUpdate();
        }
    }

    // Helper methods to extract metadata from generic entity T
    private String getSyncUUID(T entity) {
        if (entity instanceof model.User)
            return ((model.User) entity).getSyncUUID();
        if (entity instanceof model.Record)
            return ((model.Record) entity).getSyncUUID();
        if (entity instanceof model.Department)
            return ((model.Department) entity).getSyncUUID();
        if (entity instanceof model.Marks)
            return ((model.Marks) entity).getSyncUUID();
        return null;
    }

    private long getLastModifiedTs(T entity) {
        if (entity instanceof model.User)
            return ((model.User) entity).getLastModifiedTs();
        if (entity instanceof model.Record)
            return ((model.Record) entity).getLastModifiedTs();
        if (entity instanceof model.Department)
            return ((model.Department) entity).getLastModifiedTs();
        if (entity instanceof model.Marks)
            return ((model.Marks) entity).getLastModifiedTs();
        return 0;
    }

    private String getSourceActorId(T entity) {
        if (entity instanceof model.User)
            return ((model.User) entity).getSourceActorId();
        if (entity instanceof model.Record)
            return ((model.Record) entity).getSourceActorId();
        if (entity instanceof model.Department)
            return ((model.Department) entity).getSourceActorId();
        if (entity instanceof model.Marks)
            return ((model.Marks) entity).getSourceActorId();
        return null;
    }

    private String getEntityName(T entity) {
        if (entity instanceof model.User)
            return "User";
        if (entity instanceof model.Record)
            return "Record";
        if (entity instanceof model.Department)
            return "Department";
        if (entity instanceof model.Marks)
            return "Marks";
        return "Unknown";
    }
}