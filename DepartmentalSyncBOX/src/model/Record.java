package model;

import java.sql.Timestamp;

public class gitRecord {

    private int recordId;                  // record_id
    private String externalId;             // external_id
    private String title;                  // title
    private String payload;                // payload
    private int ownerDepartmentId;         // owner_department_id
    private String status;                 // status
    private int createdBy;                 // created_by
    private Timestamp createdAt;           // created_at
    private Integer lastUpdatedBy;         // last_updated_by (nullable)
    private Timestamp lastUpdatedAt;       // last_updated_at (nullable)
    private Integer rowVersion;            // row_version (nullable)
    private boolean isDeleted;             // is_deleted

    // Default constructor
    public Record() {}

    // Full constructor
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

    // Getters and Setters
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
}
