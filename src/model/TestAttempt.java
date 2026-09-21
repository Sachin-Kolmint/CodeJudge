package model;

import java.time.LocalDateTime;

public class TestAttempt {
    private final int attemptId;
    private final int candidateId;
    private final int testId;
    private final LocalDateTime startedAt;
    private final LocalDateTime deadlineAt;
    private final LocalDateTime submittedAt;
    private final String status;
    private final Integer score;
    private final int totalMarks;

    public TestAttempt(int attemptId, int candidateId, int testId,
                       LocalDateTime startedAt,
                       LocalDateTime deadlineAt,
                       LocalDateTime submittedAt,
                       String status, Integer score, int totalMarks) {
        this.attemptId = attemptId;
        this.candidateId = candidateId;
        this.testId = testId;
        this.startedAt = startedAt;
        this.deadlineAt = deadlineAt;
        this.submittedAt = submittedAt;
        this.status = status;
        this.score = score;
        this.totalMarks = totalMarks;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public int getTestId() {
        return testId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getDeadlineAt() {
        return deadlineAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public Integer getScore() {
        return score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }
}
