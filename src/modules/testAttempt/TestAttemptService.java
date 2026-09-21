package modules.testAttempt;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import model.TestAttempt;
import model.Test;
import modules.candidateAuth.CandidateSession;
import model.AttemptQuestion;
import java.util.Locale;
import modules.evaluation.EvaluationService;

public class TestAttemptService {
    private final TestAttemptDAO dao;
    private final CandidateSession session;

    public TestAttemptService(CandidateSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new TestAttemptDAO();
    }

    public List<Test> getAvailableTests() throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as candidate.");
        }

        return dao.findAvailableTests();
    }
    public TestAttempt startOrResumeTest(int testId)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as candidate.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException(
                    "Test ID must be positive.");
        }

        int candidateId =
                session.getCurrentCandidate().getCandidateId();

        TestAttempt attempt = dao.findAttempt(candidateId, testId);

        if (attempt == null) {
            try {
                int attemptId = dao.createAttempt(candidateId, testId);

                if (attemptId == 0) {
                    throw new IllegalStateException(
                            "Test is unavailable or has no questions.");
                }
            } catch (SQLException e) {
                if (e.getErrorCode() != 1062) {
                    throw e;
                }
                // Another request may have created the same attempt.
            }

            attempt = dao.findAttempt(candidateId, testId);

            if (attempt == null) {
                throw new SQLException(
                        "Could not load the test attempt.");
            }
        }

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException(
                    "You have already completed this test. "
                    + "Another attempt is not allowed.");
        }

        if (!dao.isAttemptOpen(attempt.getAttemptId(), candidateId)) {
            EvaluationService evaluationService =
                    new EvaluationService(session);

            int score = evaluationService.submitAttempt(
                    attempt.getAttemptId());

            throw new IllegalStateException(
                    "Attempt finalized. Your score: "
                    + score + "/" + attempt.getTotalMarks()
                    + ". Another attempt is not allowed.");
        }

        return attempt;
    }
    public List<AttemptQuestion> getAttemptQuestions(int attemptId)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as candidate.");
        }

        if (attemptId <= 0) {
            throw new IllegalArgumentException(
                    "Attempt ID must be positive.");
        }

        int candidateId =
                session.getCurrentCandidate().getCandidateId();

        List<AttemptQuestion> questions =
                dao.findAttemptQuestions(attemptId, candidateId);

        if (questions.isEmpty()) {
            throw new IllegalStateException(
                    "Attempt unavailable, closed or expired.");
        }

        return questions;
    }

    public void saveAnswer(int attemptId, int questionId,
                           String selectedOption) throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as candidate.");
        }

        if (attemptId <= 0 || questionId <= 0) {
            throw new IllegalArgumentException(
                    "Attempt ID and Question ID must be positive.");
        }

        if (selectedOption == null || selectedOption.isBlank()) {
            throw new IllegalArgumentException(
                    "Please choose A, B, C or D.");
        }

        selectedOption = selectedOption.trim()
                .toUpperCase(Locale.ROOT);

        if (!selectedOption.matches("[ABCD]")) {
            throw new IllegalArgumentException(
                    "Answer must be A, B, C or D.");
        }

        int candidateId =
                session.getCurrentCandidate().getCandidateId();

        boolean saved = dao.saveAnswer(
                attemptId, candidateId, questionId, selectedOption);

        if (!saved) {
            throw new IllegalStateException(
                    "Answer not saved. Attempt may be closed or expired, "
                    + "or the question does not belong to this attempt.");
        }
    }
}