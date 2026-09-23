package modules.candidateResults;

import model.LeaderboardEntry;
import modules.candidateAuth.CandidateSession;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CandidateLeaderboardService {
    private final CandidateSession session;
    private final CandidateLeaderboardDAO dao;

    public CandidateLeaderboardService(CandidateSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new CandidateLeaderboardDAO();
    }

    public List<LeaderboardEntry> getLeaderboard(int testId)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as candidate.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException(
                    "Test ID must be positive.");
        }

        return dao.findByTestId(testId);
    }
}