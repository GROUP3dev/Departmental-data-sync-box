package model;

import  java.sql.Timestamp;

public class SyncQueue {
    private Integer syncId;
    private int recordId;
    private int sourceDepartmentId;
    private int targetDepartmentId;
    private String operation;
    private String payload ;
    private Timestamp createAt;
    private boolean processed ;
    private Timestamp processedAt;
    private String errorMessage;
    private int retryCount;

    //constructor getter and setter

    public SyncQueue(){};

    public SyncQueue(int recordId , int sourceDepartmentId , int targetDepartmentId , String operation ,String payload){
         this.recordId = recordId;
         this.sourceDepartmentId = sourceDepartmentId;
         this.targetDepartmentId = targetDepartmentId;
         this.operation = operation;
         this.payload= payload;
         this.processed = false;
         this.retryCount = 0 ;
    }

    public Integer getSyncId() {
        return syncId;
    }

    public void setSyncId(Integer syncId) {
        this.syncId = syncId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getSourceDepartmentId() {
        return sourceDepartmentId;
    }

    public void setSourceDepartmentId(int sourceDepartmentId) {
        this.sourceDepartmentId = sourceDepartmentId;
    }

    public int getTargetDepartmentId() {
        return targetDepartmentId;
    }

    public void setTargetDepartmentId(int targetDepartmentId) {
        this.targetDepartmentId = targetDepartmentId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Timestamp getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Timestamp processedAt) {
        this.processedAt = processedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
