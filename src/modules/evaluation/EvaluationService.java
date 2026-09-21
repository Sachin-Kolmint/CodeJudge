package modules.evaluation;

import java.sql.SQLException;
import java.util.Objects;

import modules.candidateAuth.CandidateSession;

public class EvaluationService {
    private final EvaluationDAO dao;
    private final CandidateSession session;

    public EvaluationService(CandidateSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new EvaluationDAO();
    }

    public int submitAttempt(int attemptId) throws SQLException {
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

        return dao.submitAttempt(attemptId, candidateId);
    }
}