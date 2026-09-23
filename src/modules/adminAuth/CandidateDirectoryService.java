package modules.adminAuth;

import model.Candidate;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CandidateDirectoryService {
    private final AdminSession session;
    private final CandidateDirectoryDAO dao;

    public CandidateDirectoryService(AdminSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new CandidateDirectoryDAO();
    }

    public List<Candidate> getCandidates() throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as admin.");
        }

        return dao.findAll();
    }
}