// [File: Departmental-data-sync-box/DepartmentalSyncBOX/src/model/RecordVisibility.java]
//
package model;

import java.sql.Timestamp;

public class RecordVisibility {
    private int recordId;
    private int departmentId;
    private String accessLevel;
    private Timestamp grantedAt;
    private int grantedBy;

    // Constructors
    public RecordVisibility() {}

    public RecordVisibility(int recordId, int departmentId, String accessLevel, int grantedBy) {
        this.recordId = recordId;
        this.departmentId = departmentId;
        this.accessLevel = accessLevel;
        this.grantedBy = grantedBy;
        this.grantedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Timestamp getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(Timestamp grantedAt) {
        this.grantedAt = grantedAt;
    }

    public int getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(int grantedBy) {
        this.grantedBy = grantedBy;
    }
}