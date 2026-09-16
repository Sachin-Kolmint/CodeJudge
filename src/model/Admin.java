package model;

import java.sql.Timestamp;

/**
 * Shared model for an administrator account.
 * Data-only class -- no business logic, no SQL.
 */
public class Admin {

    private int adminId;
    private String username;
    private String passwordHash;
    private String fullName;
    private Timestamp createdAt;

    public Admin() {
    }

    public Admin(int adminId, String username, String passwordHash, String fullName, Timestamp createdAt) {
        this.adminId = adminId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = createdAt;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return "Admin{adminId=" + adminId + ", username='" + username + "', fullName='" + fullName + "'}";
    }
}
