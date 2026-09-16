package model;

import java.sql.Timestamp;

/**
 * Shared model for a test (assessment) defined by an admin.
 */
public class Test {

    public enum Status {
        DRAFT, PUBLISHED, ARCHIVED
    }

    private int testId;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalMarks;
    private Status status;
    private int createdBy;
    private Timestamp createdAt;

    public Test() {
    }

    public Test(int testId, String title, String description, int durationMinutes,
                int totalMarks, Status status, int createdBy, Timestamp createdAt) {
        this.testId = testId;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Test{testId=" + testId + ", title='" + title + "', status=" + status + "}";
    }
}
