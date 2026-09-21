package modules.candidateAuth;

import model.Candidate;
import java.sql.SQLException;
import java.security.GeneralSecurityException;

public class CandidateAuthService {
    private final CandidateAuthDAO dao = new CandidateAuthDAO();

    public boolean register(String fullName, String username,
                            String email, String password)
            throws SQLException, GeneralSecurityException {

        requireValue(fullName, "Full name");
        requireValue(username, "Username");
        requireValue(email, "Email");
        requireValue(password, "Password");

        fullName = fullName.trim();
        username = username.trim();
        email = email.trim();

        if (fullName.length() > 100) {
            throw new IllegalArgumentException(
                    "Full name must not exceed 100 characters.");
        }

        if (!username.matches("[A-Za-z0-9_]{3,50}")) {
            throw new IllegalArgumentException(
                    "Username must contain 3–50 letters, numbers or underscores.");
        }

        if (email.length() > 100
                || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }

        if (password.length() < 8 || password.length() > 128) {
            throw new IllegalArgumentException(
                    "Password must contain 8–128 characters.");
        }

        return dao.register(fullName, username, email,
                PasswordUtil.hash(password));
    }

        public Candidate login(String username, String password)
            throws SQLException, GeneralSecurityException {

        requireValue(username, "Username");
        requireValue(password, "Password");

        CandidateAuthDAO.LoginData data =
                dao.findByUsername(username.trim());

        if (data == null) {
            return null;
        }

        if (!PasswordUtil.verify(password, data.passwordHash())) {
            return null;
        }

        return data.candidate();
    }
    public void changeUsername(CandidateSession session,
                               String currentPassword,
                               String newUsername)
            throws SQLException, GeneralSecurityException {

        CandidateAuthDAO.LoginData data =
                verifyCurrentPassword(session, currentPassword);

        requireValue(newUsername, "New username");
        newUsername = newUsername.trim();

        if (!newUsername.matches("[A-Za-z0-9_]{3,50}")) {
            throw new IllegalArgumentException(
                    "Username must contain 3-50 letters, numbers or underscores.");
        }

        Candidate candidate = data.candidate();

        if (newUsername.equalsIgnoreCase(candidate.getUsername())) {
            throw new IllegalArgumentException(
                    "Choose a different username.");
        }

        boolean updated;

        try {
            updated = dao.updateUsername(
                    candidate.getCandidateId(),
                    newUsername,
                    data.passwordHash()
            );
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException(
                        "Username already taken. Choose another.");
            }
            throw e;
        }

        if (!updated) {
            throw new IllegalStateException(
                    "Account changed during this request. Please try again.");
        }

        session.login(new Candidate(
                candidate.getCandidateId(),
                candidate.getFullName(),
                newUsername,
                candidate.getEmail()
        ));
    }

    public void changePassword(CandidateSession session,
                               String currentPassword,
                               String newPassword,
                               String confirmation)
            throws SQLException, GeneralSecurityException {

        CandidateAuthDAO.LoginData data =
                verifyCurrentPassword(session, currentPassword);

        requireValue(newPassword, "New password");

        if (newPassword.length() < 8 || newPassword.length() > 128) {
            throw new IllegalArgumentException(
                    "Password must contain 8-128 characters.");
        }

        if (!newPassword.equals(confirmation)) {
            throw new IllegalArgumentException(
                    "New passwords do not match.");
        }

        if (newPassword.equals(currentPassword)) {
            throw new IllegalArgumentException(
                    "New password must differ from current password.");
        }

        String newHash = PasswordUtil.hash(newPassword);

        boolean updated = dao.updatePassword(
                data.candidate().getCandidateId(),
                newHash,
                data.passwordHash()
        );

        if (!updated) {
            throw new IllegalStateException(
                    "Account changed during this request. Please try again.");
        }
    }

    private CandidateAuthDAO.LoginData verifyCurrentPassword(
            CandidateSession session, String currentPassword)
            throws SQLException, GeneralSecurityException {

        if (session == null || !session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as candidate.");
        }

        requireValue(currentPassword, "Current password");

        CandidateAuthDAO.LoginData data = dao.findById(
                session.getCurrentCandidate().getCandidateId());

        if (data == null) {
            session.logout();
            throw new IllegalStateException(
                    "Account not found. Please log in again.");
        }

        if (!PasswordUtil.verify(currentPassword, data.passwordHash())) {
            throw new IllegalArgumentException(
                    "Current password is incorrect.");
        }

        return data;
    }
    private void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
    }
}