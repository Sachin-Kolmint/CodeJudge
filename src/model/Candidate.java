package model;

public class Candidate {
    private final int candidateId;
    private final String fullName;
    private final String username;
    private final String email;

    public Candidate(int candidateId, String fullName,
                     String username, String email) {
        this.candidateId = candidateId;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}