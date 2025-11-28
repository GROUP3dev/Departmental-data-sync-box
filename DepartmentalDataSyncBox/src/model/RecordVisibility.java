package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class RecordVisibility implements Serializable {
    private int recordId;
    private int departmentId;
    private int grantedByUserId;
    private Timestamp grantedAt;

    public RecordVisibility() {
    }

    public RecordVisibility(int recordId, int departmentId, int grantedByUserId) {
        this.recordId = recordId;
        this.departmentId = departmentId;
        this.grantedByUserId = grantedByUserId;
        this.grantedAt = new Timestamp(System.currentTimeMillis());
    }

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

    public int getGrantedByUserId() {
        return grantedByUserId;
    }

    public void setGrantedByUserId(int grantedByUserId) {
        this.grantedByUserId = grantedByUserId;
    }

    public Timestamp getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(Timestamp grantedAt) {
        this.grantedAt = grantedAt;
    }

    @Override
    public String toString() {
        return "RecordVisibility{" +
                "recordId=" + recordId +
                ", departmentId=" + departmentId +
                ", grantedByUserId=" + grantedByUserId +
                '}';
    }
}