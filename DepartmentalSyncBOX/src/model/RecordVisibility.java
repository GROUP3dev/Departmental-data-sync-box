package model;

import java.sql.Timestamp;

public class RecordVisibility {

    private int recordId;
    private int departmentId;
    private String recordTitle; // optional, from JOIN with record table
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
    }

    // Getters and Setters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getRecordTitle() { return recordTitle; }
    public void setRecordTitle(String recordTitle) { this.recordTitle = recordTitle; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    public Timestamp getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Timestamp grantedAt) { this.grantedAt = grantedAt; }

    public int getGrantedBy() { return grantedBy; }
    public void setGrantedBy(int grantedBy) { this.grantedBy = grantedBy; }

    // Access Level Constants
    public static final String READ_ONLY = "READ_ONLY";
    public static final String EDIT = "EDIT";
    public static final String FULL_ACCESS = "FULL_ACCESS";
}
