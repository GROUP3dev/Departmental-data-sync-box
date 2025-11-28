package model;

import java.sql.Timestamp;

public class SyncQueue {
    private long queueId;
    private String syncUUID;
    private String entityName;
    private String operationType; // CREATE, UPDATE, DELETE
    private String payloadJson;
    private String status; // PENDING, COMPLETED, FAILED
    private int retryCount;
    private Timestamp createdAt;

    // Metadata of the change
    private long lastModifiedTs;
    private String sourceActorId;

    public SyncQueue() {}

    public SyncQueue(String syncUUID, String entityName, String operationType, String payloadJson, long lastModifiedTs, String sourceActorId) {
        this.syncUUID = syncUUID;
        this.entityName = entityName;
        this.operationType = operationType;
        this.payloadJson = payloadJson;
        this.lastModifiedTs = lastModifiedTs;
        this.sourceActorId = sourceActorId;
        this.status = "PENDING";
        this.retryCount = 0;
    }

    // Getters and Setters
    public long getQueueId() { return queueId; }
    public void setQueueId(long queueId) { this.queueId = queueId; }

    public String getSyncUUID() { return syncUUID; }
    public void setSyncUUID(String syncUUID) { this.syncUUID = syncUUID; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public long getLastModifiedTs() { return lastModifiedTs; }
    public void setLastModifiedTs(long lastModifiedTs) { this.lastModifiedTs = lastModifiedTs; }

    public String getSourceActorId() { return sourceActorId; }
    public void setSourceActorId(String sourceActorId) { this.sourceActorId = sourceActorId; }
}