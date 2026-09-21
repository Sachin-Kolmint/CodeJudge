package model;

public class AttemptAnswer {
    private final int answerId;
    private final int attemptId;
    private final int questionId;
    private final String selectedOption;
    private final Integer marksAwarded;

    public AttemptAnswer(int answerId, int attemptId, int questionId,
                         String selectedOption, Integer marksAwarded) {
        this.answerId = answerId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.marksAwarded = marksAwarded;
    }

    public int getAnswerId() {
        return answerId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public Integer getMarksAwarded() {
        return marksAwarded;
    }
}