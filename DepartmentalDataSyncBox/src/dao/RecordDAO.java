package dao;

import model.Record;
import util.NodeConfig;
import util.TimestampUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecordDAO extends BaseDAO<Record> {

    @Override
    protected String getTableName() {
        return "records";
    }

    private static final String INSERT_SQL = "INSERT INTO records (SYNC_UUID, STUDENT_ID_NO, RECORD_TYPE, TITLE, DESCRIPTION, DEPARTMENT_ORIGIN_ID, STATUS, CREATED_BY, LAST_MODIFIED_TS, SOURCE_ACTOR_ID, IS_DELETED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE records SET STUDENT_ID_NO=?, RECORD_TYPE=?, TITLE=?, DESCRIPTION=?, DEPARTMENT_ORIGIN_ID=?, STATUS=?, CREATED_BY=?, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=?, IS_DELETED=? WHERE SYNC_UUID=?";
    private static final String SOFT_DELETE_SQL = "UPDATE records SET IS_DELETED=1, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=? WHERE SYNC_UUID=?";
    private static final String SELECT_ALL = "SELECT * FROM records WHERE IS_DELETED = 0";
    private static final String SELECT_BY_UUID = "SELECT * FROM records WHERE SYNC_UUID = ?";
    private static final String SELECT_BY_DEPT = "SELECT * FROM records WHERE DEPARTMENT_ORIGIN_ID = ? AND IS_DELETED = 0";

    @Override
    public boolean create(Record record) throws SQLException {
        if (record.getSyncUUID() == null)
            record.setSyncUUID(UUID.randomUUID().toString());
        record.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        record.setSourceActorId(NodeConfig.NODE_ID);
        record.setDeleted(false);

        Object[] params = {
                record.getSyncUUID(), record.getStudentIdNo(), record.getRecordType(),
                record.getTitle(), record.getDescription(), record.getDepartmentOriginId(),
                record.getStatus(), record.getCreatedBy(), record.getLastModifiedTs(),
                record.getSourceActorId(), record.isDeleted()
        };

        return executeUpdateAndQueue(INSERT_SQL, params, record, "CREATE") > 0;
    }

    @Override
    public boolean update(Record record) throws SQLException {
        record.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        record.setSourceActorId(NodeConfig.NODE_ID);

        Object[] params = {
                record.getStudentIdNo(), record.getRecordType(), record.getTitle(),
                record.getDescription(), record.getDepartmentOriginId(), record.getStatus(),
                record.getCreatedBy(), record.getLastModifiedTs(), record.getSourceActorId(),
                record.isDeleted(), record.getSyncUUID()
        };

        return executeUpdateAndQueue(UPDATE_SQL, params, record, "UPDATE") > 0;
    }

    @Override
    public boolean softDelete(String syncUUID) throws SQLException {
        Record record = findBySyncUUID(syncUUID);
        if (record == null)
            return false;

        record.setDeleted(true);
        record.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        record.setSourceActorId(NodeConfig.NODE_ID);

        Object[] params = { record.getLastModifiedTs(), record.getSourceActorId(), syncUUID };
        return executeUpdateAndQueue(SOFT_DELETE_SQL, params, record, "DELETE") > 0;
    }

    @Override
    public Record findBySyncUUID(String syncUUID) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_UUID)) {
            stmt.setString(1, syncUUID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToRecord(rs);
            }
        }
        return null;
    }

    public Record findById(int recordId) throws SQLException {
        String sql = "SELECT * FROM records WHERE RECORD_ID = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToRecord(rs);
            }
        }
        return null;
    }

    public List<Record> findAll() throws SQLException {
        List<Record> records = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                records.add(mapResultSetToRecord(rs));
            }
        }
        return records;
    }

    public List<Record> findByDepartment(int departmentId) throws SQLException {
        List<Record> records = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DEPT)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        return records;
    }

    public boolean applyIncomingSync(Record record) throws SQLException {
        String sqlUpdate = "UPDATE records SET STUDENT_ID_NO=?, RECORD_TYPE=?, TITLE=?, DESCRIPTION=?, DEPARTMENT_ORIGIN_ID=?, STATUS=?, CREATED_BY=?, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=?, IS_DELETED=? WHERE SYNC_UUID=?";
        Object[] paramsUpdate = {
                record.getStudentIdNo(), record.getRecordType(), record.getTitle(),
                record.getDescription(), record.getDepartmentOriginId(), record.getStatus(),
                record.getCreatedBy(), record.getLastModifiedTs(), record.getSourceActorId(),
                record.isDeleted(), record.getSyncUUID()
        };

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
            for (int i = 0; i < paramsUpdate.length; i++) {
                stmt.setObject(i + 1, paramsUpdate[i]);
            }
            int rows = stmt.executeUpdate();
            if (rows > 0)
                return true;
        }

        String sqlInsert = "INSERT INTO records (SYNC_UUID, STUDENT_ID_NO, RECORD_TYPE, TITLE, DESCRIPTION, DEPARTMENT_ORIGIN_ID, STATUS, CREATED_BY, LAST_MODIFIED_TS, SOURCE_ACTOR_ID, IS_DELETED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] paramsInsert = {
                record.getSyncUUID(), record.getStudentIdNo(), record.getRecordType(),
                record.getTitle(), record.getDescription(), record.getDepartmentOriginId(),
                record.getStatus(), record.getCreatedBy(), record.getLastModifiedTs(),
                record.getSourceActorId(), record.isDeleted()
        };

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            for (int i = 0; i < paramsInsert.length; i++) {
                stmt.setObject(i + 1, paramsInsert[i]);
            }
            return stmt.executeUpdate() > 0;
        }
    }

    private Record mapResultSetToRecord(ResultSet rs) throws SQLException {
        Record r = new Record();
        r.setRecordId(rs.getInt("RECORD_ID"));
        r.setSyncUUID(rs.getString("SYNC_UUID"));
        r.setStudentIdNo(rs.getString("STUDENT_ID_NO"));
        r.setRecordType(rs.getString("RECORD_TYPE"));
        r.setTitle(rs.getString("TITLE"));
        r.setDescription(rs.getString("DESCRIPTION"));
        r.setDepartmentOriginId(rs.getInt("DEPARTMENT_ORIGIN_ID"));
        r.setStatus(rs.getString("STATUS"));
        r.setCreatedBy(rs.getInt("CREATED_BY"));
        r.setLastModifiedTs(rs.getLong("LAST_MODIFIED_TS"));
        r.setSourceActorId(rs.getString("SOURCE_ACTOR_ID"));
        r.setDeleted(rs.getBoolean("IS_DELETED"));
        try {
            r.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist (backward compatibility)
        }
        return r;
    }
}