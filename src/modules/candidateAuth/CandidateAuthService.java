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
    private void requireValue(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
    }
}