package model;

import java.sql.Timestamp;

public class SyncLog {
    private long logId;
    private Timestamp syncTimestamp;
    private String remoteActorId;
    private String direction; // INBOUND, OUTBOUND
    private String status; // SUCCESS, FAILURE, PARTIAL
    private int itemsProcessed;
    private int itemsConflicted;
    private String details;

    public SyncLog() {
    }

    public SyncLog(String remoteActorId, String direction, String status, int itemsProcessed, int itemsConflicted,
            String details) {
        this.remoteActorId = remoteActorId;
        this.direction = direction;
        this.status = status;
        this.itemsProcessed = itemsProcessed;
        this.itemsConflicted = itemsConflicted;
        this.details = details;
        this.syncTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public Timestamp getSyncTimestamp() {
        return syncTimestamp;
    }

    public void setSyncTimestamp(Timestamp syncTimestamp) {
        this.syncTimestamp = syncTimestamp;
    }

    public String getRemoteActorId() {
        return remoteActorId;
    }

    public void setRemoteActorId(String remoteActorId) {
        this.remoteActorId = remoteActorId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getItemsProcessed() {
        return itemsProcessed;
    }

    public void setItemsProcessed(int itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
    }

    public int getItemsConflicted() {
        return itemsConflicted;
    }

    public void setItemsConflicted(int itemsConflicted) {
        this.itemsConflicted = itemsConflicted;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
