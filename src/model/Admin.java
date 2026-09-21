package model;

public class Admin {
    private final int adminId;
    private final String fullName;
    private final String username;

    public Admin(int adminId, String fullName, String username) {
        this.adminId = adminId;
        this.fullName = fullName;
        this.username = username;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }
}