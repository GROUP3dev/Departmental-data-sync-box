package dao;

import model.Department;
import model.NodeInfo;
import util.NodeConfig;
import util.TimestampUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DepartmentDAO extends BaseDAO<Department> {

    @Override
    protected String getTableName() {
        return "departments";
    }

    private static final String INSERT_SQL = "INSERT INTO departments (SYNC_UUID, DEPARTMENT_NAME, CODE, NODE_IP, CREATED_AT, UPDATED_AT, ACTIVE_FLAG, LAST_MODIFIED_TS, SOURCE_ACTOR_ID, IS_DELETED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE departments SET DEPARTMENT_NAME=?, CODE=?, NODE_IP=?, UPDATED_AT=?, ACTIVE_FLAG=?, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=?, IS_DELETED=? WHERE SYNC_UUID=?";
    private static final String SOFT_DELETE_SQL = "UPDATE departments SET IS_DELETED=1, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=? WHERE SYNC_UUID=?";
    private static final String SELECT_ALL = "SELECT * FROM departments WHERE IS_DELETED = 0";
    private static final String SELECT_BY_ID = "SELECT * FROM departments WHERE DEPARTMENT_ID = ? AND IS_DELETED = 0";
    private static final String SELECT_BY_UUID = "SELECT * FROM departments WHERE SYNC_UUID = ?";
    private static final String SELECT_ALL_NODES = "SELECT DEPARTMENT_ID, NODE_IP FROM departments WHERE NODE_IP IS NOT NULL AND NODE_IP != '' AND IS_DELETED = 0";

    @Override
    public boolean create(Department dept) throws SQLException {
        if (dept.getSyncUUID() == null)
            dept.setSyncUUID(UUID.randomUUID().toString());
        dept.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        dept.setSourceActorId(NodeConfig.NODE_ID);
        dept.setDeleted(false);
        if (dept.getCreatedAt() == null)
            dept.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Object[] params = {
                dept.getSyncUUID(), dept.getDepartmentName(), dept.getDepartmentCode(),
                dept.getNodeIp(), dept.getCreatedAt(), dept.getUpdatedAt(),
                dept.isActiveFlag(), dept.getLastModifiedTs(), dept.getSourceActorId(),
                dept.isDeleted()
        };

        return executeUpdateAndQueue(INSERT_SQL, params, dept, "CREATE") > 0;
    }

    @Override
    public boolean update(Department dept) throws SQLException {
        dept.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        dept.setSourceActorId(NodeConfig.NODE_ID);
        dept.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Object[] params = {
                dept.getDepartmentName(), dept.getDepartmentCode(), dept.getNodeIp(),
                dept.getUpdatedAt(), dept.isActiveFlag(), dept.getLastModifiedTs(),
                dept.getSourceActorId(), dept.isDeleted(), dept.getSyncUUID()
        };

        return executeUpdateAndQueue(UPDATE_SQL, params, dept, "UPDATE") > 0;
    }

    @Override
    public boolean softDelete(String syncUUID) throws SQLException {
        Department dept = findBySyncUUID(syncUUID);
        if (dept == null)
            return false;

        dept.setDeleted(true);
        dept.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        dept.setSourceActorId(NodeConfig.NODE_ID);

        Object[] params = { dept.getLastModifiedTs(), dept.getSourceActorId(), syncUUID };
        return executeUpdateAndQueue(SOFT_DELETE_SQL, params, dept, "DELETE") > 0;
    }

    @Override
    public Department findBySyncUUID(String syncUUID) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_UUID)) {
            stmt.setString(1, syncUUID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToDepartment(rs);
            }
        }
        return null;
    }

    public List<Department> findAll() throws SQLException {
        List<Department> depts = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                depts.add(mapResultSetToDepartment(rs));
            }
        }
        return depts;
    }

    public Department findById(int id) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToDepartment(rs);
            }
        }
        return null;
    }

    public List<NodeInfo> findAllNodes() throws SQLException {
        List<NodeInfo> nodes = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) {
            return nodes; // Return empty list if DB is down
        }
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_ALL_NODES)) {
            while (rs.next()) {
                nodes.add(new NodeInfo(rs.getInt("DEPARTMENT_ID"), rs.getString("NODE_IP")));
            }
        } finally {
            if (conn != null)
                conn.close();
        }
        return nodes;
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setDepartmentId(rs.getInt("DEPARTMENT_ID"));
        d.setDepartmentName(rs.getString("DEPARTMENT_NAME"));
        d.setDepartmentCode(rs.getString("CODE")); // Assuming CODE is correct based on INSERT_SQL
        d.setNodeIp(rs.getString("NODE_IP"));
        d.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        d.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        d.setActiveFlag(rs.getBoolean("ACTIVE_FLAG"));

        d.setSyncUUID(rs.getString("SYNC_UUID"));
        d.setLastModifiedTs(rs.getLong("LAST_MODIFIED_TS"));
        d.setSourceActorId(rs.getString("SOURCE_ACTOR_ID"));
        d.setDeleted(rs.getBoolean("IS_DELETED"));
        return d;
    }
}
