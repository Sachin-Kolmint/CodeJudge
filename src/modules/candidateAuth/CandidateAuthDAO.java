package modules.candidateAuth;

import config.DBConnection;
import model.Candidate;
import java.sql.*;

public class CandidateAuthDAO {

        // Carries the candidate identity and password hash to the service.
    public record LoginData(Candidate candidate, String passwordHash) {}

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

    public LoginData findByUsername(String username) throws SQLException {
        String sql = """
                SELECT candidate_id, full_name, username, email, password_hash
                FROM candidates WHERE username = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Candidate candidate = new Candidate();
                candidate.setCandidateId(rs.getInt("candidate_id"));
                candidate.setFullName(rs.getString("full_name"));
                candidate.setUsername(rs.getString("username"));
                candidate.setEmail(rs.getString("email"));

                return new LoginData(
                        candidate, rs.getString("password_hash"));
            }
        }
    }
}