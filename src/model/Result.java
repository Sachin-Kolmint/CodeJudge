package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Shared model for the official result of a completed attempt.
 * Created ONLY by the Evaluation module (Member 7). Reports
 * (Member 4) and Candidate Results (Member 8) read this but
 * must never recompute or overwrite it.
 */
public class Result {

    public enum PassFail {
        PASS, FAIL, NA
    }

    private int resultId;
    private int attemptId;
    private int candidateId;
    private int testId;
    private int totalQuestions;
    private int correctCount;
    private int wrongCount;
    private int unansweredCount;
    private BigDecimal score;
    private BigDecimal percentage;
    private PassFail passFail;
    private Timestamp submittedAt;

    public Result() {
    }

    public Result(int resultId, int attemptId, int candidateId, int testId, int totalQuestions,
                  int correctCount, int wrongCount, int unansweredCount, BigDecimal score,
                  BigDecimal percentage, PassFail passFail, Timestamp submittedAt) {
        this.resultId = resultId;
        this.attemptId = attemptId;
        this.candidateId = candidateId;
        this.testId = testId;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.unansweredCount = unansweredCount;
        this.score = score;
        this.percentage = percentage;
        this.passFail = passFail;
        this.submittedAt = submittedAt;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
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

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public void setUnansweredCount(int unansweredCount) {
        this.unansweredCount = unansweredCount;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public PassFail getPassFail() {
        return passFail;
    }

    public void setPassFail(PassFail passFail) {
        this.passFail = passFail;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public String toString() {
        return "Result{attemptId=" + attemptId + ", candidateId=" + candidateId
                + ", score=" + score + ", percentage=" + percentage + ", passFail=" + passFail + "}";
    }
}
