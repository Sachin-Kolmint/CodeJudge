package modules.adminAuth;

import modules.adminAuth.AdminDAO;
import modules.adminAuth.Admin;

public class AdminService {

    private AdminDAO adminDAO;
    private Admin loggedInAdmin;

    public AdminService() {
        adminDAO = new AdminDAO();
    }

    public boolean login(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        if (password == null || password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }

        Admin admin = adminDAO.login(username, password);

        if (admin != null) {
            loggedInAdmin = admin;
            System.out.println("Login successful. Welcome, " + admin.getUsername() + "!");
            return true;
        }

        System.out.println("Invalid username or password.");
        return false;
    }

    public void logout() {

        loggedInAdmin = null;
        System.out.println("Logout successful.");
    }

    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }

    public Admin getLoggedInAdmin() {
        return loggedInAdmin;
    }
}