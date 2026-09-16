package model;

import java.sql.Timestamp;

/**
 * Shared model for a candidate account.
 */
public class Candidate {

    private int candidateId;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private Timestamp createdAt;

    public Candidate() {
    }

    public Candidate(int candidateId, String username, String email, String passwordHash,
                      String fullName, Timestamp createdAt) {
        this.candidateId = candidateId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = createdAt;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Candidate{candidateId=" + candidateId + ", username='" + username + "', email='" + email + "'}";
    }
}
