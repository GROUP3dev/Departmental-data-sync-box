package model;

import java.time.LocalDateTime;

public class SyncLog {
    private int id;
    private String syncType; // LAN or Cloud
    private LocalDateTime timestamp;
    private String status;

    public SyncLog() {}

    public SyncLog(int id, String syncType, LocalDateTime timestamp, String status) {
        this.id = id;
        this.syncType = syncType;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSyncType() { return syncType; }
    public void setSyncType(String syncType) { this.syncType = syncType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
