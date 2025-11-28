package sync;

import java.io.Serializable;

/**
 * Data Transfer Object for synchronization.
 * Sent over the network.
 */
public class SyncPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String entityName;
    private String operationType;
    private String syncUUID;
    private long lastModifiedTs;
    private String sourceActorId;
    private String payloadJson; // The actual entity data in JSON

    public SyncPayload(String entityName, String operationType, String syncUUID, long lastModifiedTs, String sourceActorId, String payloadJson) {
        this.entityName = entityName;
        this.operationType = operationType;
        this.syncUUID = syncUUID;
        this.lastModifiedTs = lastModifiedTs;
        this.sourceActorId = sourceActorId;
        this.payloadJson = payloadJson;
    }

    public String getEntityName() { return entityName; }
    public String getOperationType() { return operationType; }
    public String getSyncUUID() { return syncUUID; }
    public long getLastModifiedTs() { return lastModifiedTs; }
    public String getSourceActorId() { return sourceActorId; }
    public String getPayloadJson() { return payloadJson; }
}