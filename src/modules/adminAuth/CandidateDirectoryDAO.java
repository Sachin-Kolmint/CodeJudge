package modules.adminAuth;

import config.DBConnection;
import model.Candidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidateDirectoryDAO {

    public List<Candidate> findAll() throws SQLException {
        String sql = """
                SELECT candidate_id, full_name, username, email
                FROM candidates
                ORDER BY candidate_id
                """;

        List<Candidate> candidates = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                candidates.add(new Candidate(
                        result.getInt("candidate_id"),
                        result.getString("full_name"),
                        result.getString("username"),
                        result.getString("email")
                ));
            }
        }

        return candidates;
    }
}