package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String syncUUID;
    private String username;
    private String passwordHash;
    private int roleId; // Changed from String role
    private int departmentId; // Changed from String department
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActive;

    // Sync Metadata
    private long lastModifiedTs;
    private String sourceActorId;
    private boolean isDeleted;

    public User() {
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSyncUUID() {
        return syncUUID;
    }

    public void setSyncUUID(String syncUUID) {
        this.syncUUID = syncUUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
}