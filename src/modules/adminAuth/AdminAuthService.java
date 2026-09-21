package modules.adminAuth;

import java.security.GeneralSecurityException;
import java.sql.SQLException;

import modules.candidateAuth.PasswordUtil;

public class AdminAuthService {
    private final AdminAuthDAO dao;
    private final AdminSession session;

    public AdminAuthService(AdminSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null.");
        }

        this.dao = new AdminAuthDAO();
        this.session = session;
    }

    public boolean login(String username, String password)
            throws SQLException, GeneralSecurityException {

        session.logout();

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        AdminAuthDAO.AdminCredentials credentials =
                dao.findByUsername(username.trim());

        if (credentials == null) {
            return false;
        }

        boolean valid = PasswordUtil.verify(
                password, credentials.getPasswordHash()
        );

        if (!valid) {
            return false;
        }

        session.login(credentials.getAdmin());
        return true;
    }

    public void logout() {
        session.logout();
    }
}
