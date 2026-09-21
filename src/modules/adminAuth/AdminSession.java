package modules.adminAuth;

import model.Admin;

public class AdminSession {
    private Admin currentAdmin;

    public void login(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null.");
        }
        currentAdmin = admin;
    }

    public boolean isLoggedIn() {
        return currentAdmin != null;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public void logout() {
        currentAdmin = null;
    }
}