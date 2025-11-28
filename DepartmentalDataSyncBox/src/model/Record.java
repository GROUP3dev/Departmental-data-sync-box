package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Record implements Serializable {
    private static final long serialVersionUID = 1L;

    private int recordId;
    private String syncUUID;
    private String studentIdNo;
    private String recordType; // ENUM: STUDENT, STAFF
    private String title;
    private String description;
    private int departmentOriginId;
    private String status; // DRAFT, PENDING, APPROVED, ARCHIVED
    private int createdBy;
    private Timestamp createdAt;

    // Sync Metadata
    private long lastModifiedTs;
    private String sourceActorId;
    private boolean isDeleted;

    public Record() {
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getSyncUUID() {
        return syncUUID;
    }

    public void setSyncUUID(String syncUUID) {
        this.syncUUID = syncUUID;
    }

    public String getStudentIdNo() {
        return studentIdNo;
    }

    public void setStudentIdNo(String studentIdNo) {
        this.studentIdNo = studentIdNo;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDepartmentOriginId() {
        return departmentOriginId;
    }

    public void setDepartmentOriginId(int departmentOriginId) {
        this.departmentOriginId = departmentOriginId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastModifiedTs() {
        return lastModifiedTs;
    }

    public void setLastModifiedTs(long lastModifiedTs) {
        this.lastModifiedTs = lastModifiedTs;
    }

    public String getSourceActorId() {
        return sourceActorId;
    }

    public void setSourceActorId(String sourceActorId) {
        this.sourceActorId = sourceActorId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}