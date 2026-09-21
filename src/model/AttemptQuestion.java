package model;

public record AttemptQuestion(
        int questionId,
        String questionText,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        int marks,
        String selectedOption
) {}