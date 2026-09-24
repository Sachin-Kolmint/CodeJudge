package modules.adminAuth;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Objects;

import modules.candidateAuth.PasswordUtil;

public class CandidatePasswordService {
    private final AdminSession session;
    private final CandidatePasswordDAO dao;

    public CandidatePasswordService(AdminSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new CandidatePasswordDAO();
    }

    public void resetPassword(String username, String newPassword,
                              String confirmation)
            throws SQLException, GeneralSecurityException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as admin.");
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Candidate username is required.");
        }

        username = username.trim();

        if (username.length() > 50) {
            throw new IllegalArgumentException(
                    "Username must not exceed 50 characters.");
        }

        if (newPassword == null || newPassword.isBlank()
                || newPassword.length() < 8
                || newPassword.length() > 128) {
            throw new IllegalArgumentException(
                    "Password must contain 8-128 characters "
                    + "and cannot be only spaces.");
        }

        if (!newPassword.equals(confirmation)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        String passwordHash = PasswordUtil.hash(newPassword);

        if (!dao.updatePassword(username, passwordHash)) {
            throw new IllegalArgumentException(
                    "Candidate username not found.");
        }
    }
}