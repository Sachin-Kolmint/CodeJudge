package modules.candidateAuth;

import config.DBConnection;
import model.Candidate;
import java.sql.*;
import java.security.GeneralSecurityException;

public class CandidateAuthDAO {

    public boolean register(String fullName, String username,
                            String email, String passwordHash)
            throws SQLException {

        String sql = """
                INSERT INTO candidates
                (full_name, username, email, password_hash)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, passwordHash);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return false;
            }
            throw e;
        }
    }

    public Candidate login(String username, String password)
            throws SQLException, GeneralSecurityException {

        String sql = """
                SELECT candidate_id, full_name, username, email, password_hash
                FROM candidates WHERE username = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(
                        password, rs.getString("password_hash"))) {

                    return new Candidate(
                            rs.getInt("candidate_id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("email")
                    );
                }
            }
        }

        return null;
    }
}