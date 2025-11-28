package dao;

import model.User;
import util.EncryptionUtil;
import util.NodeConfig;
import util.TimestampUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO extends BaseDAO<User> {

    @Override
    protected String getTableName() {
        return "users";
    }

    // Updated SQL to match new schema (ROLE_ID, DEPARTMENT_ID)
    private static final String INSERT_SQL = "INSERT INTO users (SYNC_UUID, USERNAME, PASSWORD_HASH, ROLE_ID, DEPARTMENT_ID, FIRST_NAME, LAST_NAME, EMAIL, IS_ACTIVE, LAST_MODIFIED_TS, SOURCE_ACTOR_ID, IS_DELETED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE users SET USERNAME=?, PASSWORD_HASH=?, ROLE_ID=?, DEPARTMENT_ID=?, FIRST_NAME=?, LAST_NAME=?, EMAIL=?, IS_ACTIVE=?, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=?, IS_DELETED=? WHERE SYNC_UUID=?";
    private static final String SOFT_DELETE_SQL = "UPDATE users SET IS_DELETED=1, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=? WHERE SYNC_UUID=?";
    private static final String SELECT_ALL = "SELECT * FROM users WHERE IS_DELETED = 0";
    private static final String SELECT_BY_UUID = "SELECT * FROM users WHERE SYNC_UUID = ?";
    private static final String SELECT_BY_USERNAME = "SELECT * FROM users WHERE USERNAME = ? AND IS_DELETED = 0";

    @Override
    public boolean create(User user) throws SQLException {
        if (user.getSyncUUID() == null)
            user.setSyncUUID(UUID.randomUUID().toString());
        user.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        user.setSourceActorId(NodeConfig.NODE_ID);
        user.setDeleted(false);

        Object[] params = {
                user.getSyncUUID(), user.getUsername(), user.getPasswordHash(),
                user.getRoleId(), user.getDepartmentId(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.isActive(),
                user.getLastModifiedTs(), user.getSourceActorId(), user.isDeleted()
        };

        return executeUpdateAndQueue(INSERT_SQL, params, user, "CREATE") > 0;
    }

    @Override
    public boolean update(User user) throws SQLException {
        user.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        user.setSourceActorId(NodeConfig.NODE_ID);

        Object[] params = {
                user.getUsername(), user.getPasswordHash(), user.getRoleId(),
                user.getDepartmentId(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.isActive(), user.getLastModifiedTs(),
                user.getSourceActorId(), user.isDeleted(), user.getSyncUUID()
        };

        return executeUpdateAndQueue(UPDATE_SQL, params, user, "UPDATE") > 0;
    }

    @Override
    public boolean softDelete(String syncUUID) throws SQLException {
        User user = findBySyncUUID(syncUUID);
        if (user == null)
            return false;

        user.setDeleted(true);
        user.setLastModifiedTs(TimestampUtil.getCurrentTimestampMillis());
        user.setSourceActorId(NodeConfig.NODE_ID);

        Object[] params = { user.getLastModifiedTs(), user.getSourceActorId(), syncUUID };
        return executeUpdateAndQueue(SOFT_DELETE_SQL, params, user, "DELETE") > 0;
    }

    @Override
    public User findBySyncUUID(String syncUUID) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_UUID)) {
            stmt.setString(1, syncUUID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) {
            return users;
        }
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } finally {
            if (conn != null)
                conn.close();
        }
        return users;
    }

    public User authenticate(String username, String password) throws SQLException {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    if (EncryptionUtil.checkPassword(password, user.getPasswordHash())) {
                        return user;
                    }
                }
            }
        } finally {
            if (conn != null)
                conn.close();
        }
        return null;
    }

    public boolean applyIncomingSync(User user) throws SQLException {
        // Try update first
        String sqlUpdate = "UPDATE users SET USERNAME=?, PASSWORD_HASH=?, ROLE_ID=?, DEPARTMENT_ID=?, FIRST_NAME=?, LAST_NAME=?, EMAIL=?, IS_ACTIVE=?, LAST_MODIFIED_TS=?, SOURCE_ACTOR_ID=?, IS_DELETED=? WHERE SYNC_UUID=?";
        Object[] paramsUpdate = {
                user.getUsername(), user.getPasswordHash(), user.getRoleId(),
                user.getDepartmentId(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.isActive(), user.getLastModifiedTs(),
                user.getSourceActorId(), user.isDeleted(), user.getSyncUUID()
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

        // If update failed (0 rows), it's an insert
        String sqlInsert = "INSERT INTO users (SYNC_UUID, USERNAME, PASSWORD_HASH, ROLE_ID, DEPARTMENT_ID, FIRST_NAME, LAST_NAME, EMAIL, IS_ACTIVE, LAST_MODIFIED_TS, SOURCE_ACTOR_ID, IS_DELETED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] paramsInsert = {
                user.getSyncUUID(), user.getUsername(), user.getPasswordHash(),
                user.getRoleId(), user.getDepartmentId(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.isActive(),
                user.getLastModifiedTs(), user.getSourceActorId(), user.isDeleted()
        };

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            for (int i = 0; i < paramsInsert.length; i++) {
                stmt.setObject(i + 1, paramsInsert[i]);
            }
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("USER_ID"));
        u.setSyncUUID(rs.getString("SYNC_UUID"));
        u.setUsername(rs.getString("USERNAME"));
        u.setPasswordHash(rs.getString("PASSWORD_HASH"));
        u.setRoleId(rs.getInt("ROLE_ID"));
        u.setDepartmentId(rs.getInt("DEPARTMENT_ID"));
        u.setFirstName(rs.getString("FIRST_NAME"));
        u.setLastName(rs.getString("LAST_NAME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setActive(rs.getBoolean("IS_ACTIVE"));
        u.setLastModifiedTs(rs.getLong("LAST_MODIFIED_TS"));
        u.setSourceActorId(rs.getString("SOURCE_ACTOR_ID"));
        u.setDeleted(rs.getBoolean("IS_DELETED"));
        return u;
    }
}