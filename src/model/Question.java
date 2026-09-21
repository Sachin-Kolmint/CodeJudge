package model;

public class Question {
    private final int questionId;
    private final int testId;
    private final String questionText;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final String correctOption;
    private final int marks;

    public Question(int questionId, int testId, String questionText,
                    String optionA, String optionB,
                    String optionC, String optionD,
                    String correctOption, int marks) {
        this.questionId = questionId;
        this.testId = testId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.marks = marks;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getTestId() {
        return testId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public int getMarks() {
        return marks;
    }
}