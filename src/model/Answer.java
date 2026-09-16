package model;

import java.sql.Timestamp;

/**
 * Shared model for a candidate's selected answer to one question
 * within one attempt. selectedOption is null until answered.
 */
public class Answer {

    private int answerId;
    private int attemptId;
    private int questionId;
    private Question.Option selectedOption;
    private Timestamp answeredAt;

    public Answer() {
    }

    public Answer(int answerId, int attemptId, int questionId, Question.Option selectedOption, Timestamp answeredAt) {
        this.answerId = answerId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.answeredAt = answeredAt;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Question.Option getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(Question.Option selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Timestamp getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Timestamp answeredAt) {
        this.answeredAt = answeredAt;
    }

    @Override
    public String toString() {
        return "Answer{attemptId=" + attemptId + ", questionId=" + questionId + ", selectedOption=" + selectedOption + "}";
    }
}
