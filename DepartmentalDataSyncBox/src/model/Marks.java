package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Marks implements Serializable {
    private int markId;
    private String syncUUID;
    private int recordId;
    private String courseCode;
    private String courseName;
    private String grade;
    private double score;
    private String semester;
    private int issuedByUserId;
    private long lastModifiedTs;
    private String sourceActorId;
    private boolean isDeleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Marks() {
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public String getSyncUUID() {
        return syncUUID;
    }

    public void setSyncUUID(String syncUUID) {
        this.syncUUID = syncUUID;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getIssuedByUserId() {
        return issuedByUserId;
    }

    public void setIssuedByUserId(int issuedByUserId) {
        this.issuedByUserId = issuedByUserId;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Marks{" +
                "markId=" + markId +
                ", recordId=" + recordId +
                ", courseCode='" + courseCode + '\'' +
                ", score=" + score +
                '}';
    }
}