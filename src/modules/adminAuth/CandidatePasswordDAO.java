package modules.adminAuth;

import config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CandidatePasswordDAO {

    public boolean updatePassword(String username, String passwordHash)
            throws SQLException {

        String sql = """
                UPDATE candidates
                SET password_hash = ?
                WHERE username = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, passwordHash);
            statement.setString(2, username);

            return statement.executeUpdate() == 1;
        }
    }
}