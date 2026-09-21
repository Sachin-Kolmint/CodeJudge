package modules.testManagement;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;

import java.util.List;

import model.Question;
import modules.adminAuth.AdminSession;

public class QuestionService {
    private final QuestionDAO dao;
    private final AdminSession session;

    public QuestionService(AdminSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new QuestionDAO();
    }

    public int addQuestion(int testId, String questionText,
                           String optionA, String optionB,
                           String optionC, String optionD,
                           String correctOption, int marks)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as admin.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException("Test ID must be positive.");
        }

        questionText = required(questionText, "Question");
        optionA = validateOption(optionA, "Option A");
        optionB = validateOption(optionB, "Option B");
        optionC = validateOption(optionC, "Option C");
        optionD = validateOption(optionD, "Option D");

        correctOption = required(correctOption, "Correct option")
                .toUpperCase(Locale.ROOT);

        if (!correctOption.matches("[ABCD]")) {
            throw new IllegalArgumentException(
                    "Correct option must be A, B, C or D.");
        }

        if (marks <= 0) {
            throw new IllegalArgumentException(
                    "Marks must be greater than zero.");
        }

        Question question = new Question(
                0, testId, questionText,
                optionA, optionB, optionC, optionD,
                correctOption, marks
        );

        int questionId = dao.addQuestion(
                question, session.getCurrentAdmin().getAdminId());

        if (questionId == 0) {
            throw new IllegalStateException(
                    "Choose your own inactive test with no attempts.");
        }

        return questionId;
    }

    private String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required.");
        }
        return value.trim();
    }

    private String validateOption(String value, String fieldName) {
        value = required(value, fieldName);

        if (value.length() > 500) {
            throw new IllegalArgumentException(
                    fieldName + " must not exceed 500 characters.");
        }
        return value;
    }
    public List<Question> getTestQuestions(int testId)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as admin.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException(
                    "Test ID must be positive.");
        }

        return dao.findByTestId(
                testId, session.getCurrentAdmin().getAdminId());
    }
    public void updateQuestion(int questionId, int testId,
                               String questionText,
                               String optionA, String optionB,
                               String optionC, String optionD,
                               String correctOption, int marks)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as admin.");
        }

        if (questionId <= 0 || testId <= 0) {
            throw new IllegalArgumentException(
                    "Question ID and Test ID must be positive.");
        }

        questionText = required(questionText, "Question");
        optionA = validateOption(optionA, "Option A");
        optionB = validateOption(optionB, "Option B");
        optionC = validateOption(optionC, "Option C");
        optionD = validateOption(optionD, "Option D");

        correctOption = required(correctOption, "Correct option")
                .toUpperCase(Locale.ROOT);

        if (!correctOption.matches("[ABCD]")) {
            throw new IllegalArgumentException(
                    "Correct option must be A, B, C or D.");
        }

        if (marks <= 0) {
            throw new IllegalArgumentException(
                    "Marks must be greater than zero.");
        }

        Question question = new Question(
                questionId, testId, questionText,
                optionA, optionB, optionC, optionD,
                correctOption, marks
        );

        boolean updated = dao.updateQuestion(
                question, session.getCurrentAdmin().getAdminId());

        if (!updated) {
            throw new IllegalStateException(
                    "Update failed. Check question/test IDs. "
                    + "The test must belong to you, be inactive "
                    + "and have no attempts.");
        }
    }
    public void deleteQuestion(int questionId, int testId)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as admin.");
        }

        if (questionId <= 0 || testId <= 0) {
            throw new IllegalArgumentException(
                    "Question ID and Test ID must be positive.");
        }

        boolean deleted = dao.deleteQuestion(
                questionId,
                testId,
                session.getCurrentAdmin().getAdminId()
        );

        if (!deleted) {
            throw new IllegalStateException(
                    "Delete failed. Check question/test IDs. "
                    + "The test must belong to you, be inactive "
                    + "and have no attempts.");
        }
    }
}