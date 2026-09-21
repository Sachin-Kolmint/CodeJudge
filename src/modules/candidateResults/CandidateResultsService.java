package modules.candidateResults;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import model.CandidateResult;
import modules.candidateAuth.CandidateSession;

public class CandidateResultsService {
    private final CandidateResultsDAO dao;
    private final CandidateSession session;

    public CandidateResultsService(CandidateSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new CandidateResultsDAO();
    }

    public List<CandidateResult> getMyResults() throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as candidate.");
        }

        int candidateId =
                session.getCurrentCandidate().getCandidateId();

        return dao.findByCandidateId(candidateId);
    }
}