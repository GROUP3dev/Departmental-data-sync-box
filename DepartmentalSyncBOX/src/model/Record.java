package model;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Record {

    private int recordId;
    private String externalId;
    private String title;
    private String payload;
    private int ownerDepartmentId;
    private String status;
    private int createdBy;
    private Timestamp createdAt;
    private Integer lastUpdatedBy;
    private Timestamp lastUpdatedAt;
    private Integer rowVersion;
    private boolean isDeleted;

    public Record() {}

    public Record(int recordId, String externalId, String title, String payload,
                  int ownerDepartmentId, String status, int createdBy,
                  Timestamp createdAt, Integer lastUpdatedBy, Timestamp lastUpdatedAt,
                  Integer rowVersion, boolean isDeleted) {
        this.recordId = recordId;
        this.externalId = externalId;
        this.title = title;
        this.payload = payload;
        this.ownerDepartmentId = ownerDepartmentId;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedAt = lastUpdatedAt;
        this.rowVersion = rowVersion;
        this.isDeleted = isDeleted;
    }

    // ---------------- Getters & Setters ----------------
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public int getOwnerDepartmentId() { return ownerDepartmentId; }
    public void setOwnerDepartmentId(int ownerDepartmentId) { this.ownerDepartmentId = ownerDepartmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(Integer lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public Timestamp getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    public Integer getRowVersion() { return rowVersion; }
    public void setRowVersion(Integer rowVersion) { this.rowVersion = rowVersion; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    @Override
    public String toString() {
        return "Record{" +
                "recordId=" + recordId +
                ", externalId='" + externalId + '\'' +
                ", title='" + title + '\'' +
                ", payload='" + payload + '\'' +
                ", ownerDepartmentId=" + ownerDepartmentId +
                ", status='" + status + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", rowVersion=" + rowVersion +
                ", isDeleted=" + isDeleted +
                '}';
    }

    // ------------------ CRUD Operations ------------------

    // Add record
    public boolean add() {
        String sql = "INSERT INTO records (external_id, title, payload, owner_department_id, status, created_by, created_at, row_version, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, externalId);
            stmt.setString(2, title);
            stmt.setString(3, payload);
            stmt.setInt(4, ownerDepartmentId);
            stmt.setString(5, status);
            stmt.setInt(6, createdBy);
            stmt.setTimestamp(7, createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis()));
            stmt.setInt(8, rowVersion != null ? rowVersion : 1);
            stmt.setBoolean(9, isDeleted);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.recordId = rs.getInt(1);
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
        return false;
    }

    // Update record
    public boolean update() {
        String sql = "UPDATE records SET external_id=?, title=?, payload=?, owner_department_id=?, status=?, last_updated_by=?, last_updated_at=?, row_version=?, is_deleted=? " +
                "WHERE record_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, externalId);
            stmt.setString(2, title);
            stmt.setString(3, payload);
            stmt.setInt(4, ownerDepartmentId);
            stmt.setString(5, status);
            stmt.setObject(6, lastUpdatedBy, Types.INTEGER);
            stmt.setTimestamp(7, lastUpdatedAt != null ? lastUpdatedAt : new Timestamp(System.currentTimeMillis()));
            stmt.setInt(8, rowVersion != null ? rowVersion : 1);
            stmt.setBoolean(9, isDeleted);
            stmt.setInt(10, recordId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
        return false;
    }

    // Soft delete record
    public boolean delete(int deletedBy) {
        String sql = "UPDATE records SET is_deleted=1, last_updated_by=?, last_updated_at=? WHERE record_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deletedBy);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, recordId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
        return false;
    }

    // Find record by ID
    public static Record findById(int id) {
        String sql = "SELECT * FROM records WHERE record_id=? AND is_deleted=0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Record r = new Record();
                    r.setRecordId(rs.getInt("record_id"));
                    r.setExternalId(rs.getString("external_id"));
                    r.setTitle(rs.getString("title"));
                    r.setPayload(rs.getString("payload"));
                    r.setOwnerDepartmentId(rs.getInt("owner_department_id"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedBy(rs.getInt("created_by"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setLastUpdatedBy(rs.getObject("last_updated_by", Integer.class));
                    r.setLastUpdatedAt(rs.getTimestamp("last_updated_at"));
                    r.setRowVersion(rs.getObject("row_version", Integer.class));
                    r.setDeleted(rs.getBoolean("is_deleted"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding record: " + e.getMessage());
        }
        return null;
    }

    // Find all non-deleted records
    public static List<Record> findAll() {
        List<Record> records = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE is_deleted=0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Record r = new Record();
                r.setRecordId(rs.getInt("record_id"));
                r.setExternalId(rs.getString("external_id"));
                r.setTitle(rs.getString("title"));
                r.setPayload(rs.getString("payload"));
                r.setOwnerDepartmentId(rs.getInt("owner_department_id"));
                r.setStatus(rs.getString("status"));
                r.setCreatedBy(rs.getInt("created_by"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setLastUpdatedBy(rs.getObject("last_updated_by", Integer.class));
                r.setLastUpdatedAt(rs.getTimestamp("last_updated_at"));
                r.setRowVersion(rs.getObject("row_version", Integer.class));
                r.setDeleted(rs.getBoolean("is_deleted"));
                records.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
        return records;
    }

    // ------------------ Testing ------------------
    public static void main(String[] args) {
        Record r = new Record();
        r.setExternalId("mt3001");
        r.setTitle("Sample Record");
        r.setPayload("This is a test payload");
        r.setOwnerDepartmentId(1); // Make sure this department exists
        r.setStatus("Active");
        r.setCreatedBy(100);
        r.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        r.setRowVersion(1);
        r.setDeleted(true); // NEW record must not be deleted

        // Add record
        if (r.add()) System.out.println("Added record: " + r);

        // Update record
        r.setTitle("Updated Sample Record");
        r.setLastUpdatedBy(01);
        r.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));
        if (r.update()) System.out.println("Updated record: " + r);

        // Find by ID
        Record found = Record.findById(r.getRecordId());
        System.out.println("Found by ID: " + found);

        // Find all
        List<Record> all = Record.findAll();
        System.out.println("All records:");
        all.forEach(System.out::println);

        // Soft delete
        if (r.delete(102)) System.out.println("Soft deleted record ID: " + r.getRecordId());

        DBConnection.closeConnection();
    }
}
