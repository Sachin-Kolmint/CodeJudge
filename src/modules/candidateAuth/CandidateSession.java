package modules.candidateAuth;

import model.Candidate;

public class CandidateSession {
    private Candidate currentCandidate;

    public void login(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null.");
        }
        currentCandidate = candidate;
    }

    public boolean isLoggedIn() {
        return currentCandidate != null;
    }

    public Candidate getCurrentCandidate() {
        return currentCandidate;
    }

    public void logout() {
        currentCandidate = null;
    }
}