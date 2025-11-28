package model;

import java.sql.Timestamp;

public class AuditLog {
    private long logId;
    private int userId;
    private String action; // Maps to EVENT_TYPE in DB
    private String details;
    private Timestamp timestamp; // Maps to LOG_TIMESTAMP in DB

    public AuditLog() {
    }

    public AuditLog(int userId, String action, String details) {
        this.userId = userId;
        this.action = action;
        this.details = details;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}