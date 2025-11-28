package dao;

import model.Marks;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO extends BaseDAO<Marks> {

    @Override
    protected String getTableName() {
        return "marks";
    }

    public boolean create(Marks mark) throws SQLException {
        String sql = "INSERT INTO marks (SYNC_UUID, RECORD_ID, COURSE_CODE, COURSE_NAME, GRADE, SCORE, SEMESTER, ISSUED_BY_USER_ID, SOURCE_ACTOR_ID, LAST_MODIFIED_TS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, mark.getSyncUUID());
            stmt.setInt(2, mark.getRecordId());
            stmt.setString(3, mark.getCourseCode());
            stmt.setString(4, mark.getCourseName());
            stmt.setString(5, mark.getGrade());
            stmt.setDouble(6, mark.getScore());
            stmt.setString(7, mark.getSemester());
            stmt.setInt(8, mark.getIssuedByUserId());
            stmt.setString(9, mark.getSourceActorId());
            stmt.setLong(10, mark.getLastModifiedTs());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mark.setMarkId(generatedKeys.getInt(1));
                    }
                }
                addToSyncQueue("MARKS", "CREATE", mark);
                return true;
            }
            return false;
        }
    }

    public boolean update(Marks mark) throws SQLException {
        String sql = "UPDATE marks SET SCORE = ?, GRADE = ?, LAST_MODIFIED_TS = ? WHERE SYNC_UUID = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, mark.getScore());
            stmt.setString(2, mark.getGrade());
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setString(4, mark.getSyncUUID());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                addToSyncQueue("MARKS", "UPDATE", mark);
                return true;
            }
            return false;
        }
    }

    public boolean softDelete(String syncUUID) throws SQLException {
        String sql = "UPDATE marks SET IS_DELETED = TRUE, LAST_MODIFIED_TS = ? WHERE SYNC_UUID = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, System.currentTimeMillis());
            stmt.setString(2, syncUUID);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                Marks mark = findBySyncUUID(syncUUID);
                if (mark != null) {
                    mark.setDeleted(true);
                    addToSyncQueue("MARKS", "DELETE", mark);
                }
                return true;
            }
            return false;
        }
    }

    public Marks findBySyncUUID(String syncUUID) throws SQLException {
        String sql = "SELECT * FROM marks WHERE SYNC_UUID = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, syncUUID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMarks(rs);
                }
            }
        }
        return null;
    }

    public List<Marks> findByRecordId(int recordId) throws SQLException {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE RECORD_ID = ? AND IS_DELETED = FALSE";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMarks(rs));
                }
            }
        }
        return list;
    }

    private Marks mapResultSetToMarks(ResultSet rs) throws SQLException {
        Marks m = new Marks();
        m.setMarkId(rs.getInt("MARK_ID"));
        m.setSyncUUID(rs.getString("SYNC_UUID"));
        m.setRecordId(rs.getInt("RECORD_ID"));
        m.setCourseCode(rs.getString("COURSE_CODE"));
        m.setCourseName(rs.getString("COURSE_NAME"));
        m.setGrade(rs.getString("GRADE"));
        m.setScore(rs.getDouble("SCORE"));
        m.setSemester(rs.getString("SEMESTER"));
        m.setIssuedByUserId(rs.getInt("ISSUED_BY_USER_ID"));
        m.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        m.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));
        m.setLastModifiedTs(rs.getLong("LAST_MODIFIED_TS"));
        m.setSourceActorId(rs.getString("SOURCE_ACTOR_ID"));
        m.setDeleted(rs.getBoolean("IS_DELETED"));
        return m;
    }
}
