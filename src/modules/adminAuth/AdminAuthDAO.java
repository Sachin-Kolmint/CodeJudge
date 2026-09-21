package modules.adminAuth;

import config.DBConnection;
import model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminAuthDAO {

    public AdminCredentials findByUsername(String username)
            throws SQLException {

        String sql = """
                SELECT admin_id, full_name, username, password_hash
                FROM admins
                WHERE username = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                Admin admin = new Admin(
                        result.getInt("admin_id"),
                        result.getString("full_name"),
                        result.getString("username")
                );

                return new AdminCredentials(
                        admin,
                        result.getString("password_hash")
                );
            }
        }
    }
    public boolean createAdmin(String fullName, String username,
                               String passwordHash)
            throws SQLException {

        String sql = """
                INSERT INTO admins (full_name, username, password_hash)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, fullName);
            statement.setString(2, username);
            statement.setString(3, passwordHash);

            return statement.executeUpdate() == 1;
        }
    }

    public static class AdminCredentials {
        private final Admin admin;
        private final String passwordHash;

        public AdminCredentials(Admin admin, String passwordHash) {
            this.admin = admin;
            this.passwordHash = passwordHash;
        }

        public Admin getAdmin() {
            return admin;
        }

        public String getPasswordHash() {
            return passwordHash;
        }
    }
}