package model;

import java.sql.Timestamp;

/**
 * Shared model for a candidate's attempt at a test.
 */
public class Attempt {

    public enum Status {
        IN_PROGRESS, SUBMITTED, TIMED_OUT
    }

    private int attemptId;
    private int candidateId;
    private int testId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Status status;
    private Timestamp createdAt;

    public Attempt() {
    }

    public Attempt(int attemptId, int candidateId, int testId, Timestamp startTime,
                    Timestamp endTime, Status status, Timestamp createdAt) {
        this.attemptId = attemptId;
        this.candidateId = candidateId;
        this.testId = testId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Attempt{attemptId=" + attemptId + ", candidateId=" + candidateId
                + ", testId=" + testId + ", status=" + status + "}";
    }
}
