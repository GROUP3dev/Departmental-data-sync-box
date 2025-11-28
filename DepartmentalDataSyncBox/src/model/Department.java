package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private int departmentId;
    private String departmentName;
    private String departmentCode;
    private String nodeIp;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean activeFlag;

    // Sync Metadata
    private String syncUUID;
    private long lastModifiedTs;
    private String sourceActorId;
    private boolean isDeleted;

    public Department() {
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getSyncUUID() {
        return syncUUID;
    }

    public void setSyncUUID(String syncUUID) {
        this.syncUUID = syncUUID;
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

    @Override
    public String toString() {
        return departmentName + " (" + departmentCode + ")";
    }
}