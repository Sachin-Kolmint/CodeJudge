package modules.candidateResults;

import config.DBConnection;
import model.LeaderboardEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidateLeaderboardDAO {

    public List<LeaderboardEntry> findByTestId(int testId)
            throws SQLException {

        String sql = """
                SELECT c.candidate_id, c.full_name,
                       ta.score, ta.total_marks
                FROM test_attempts ta
                JOIN candidates c
                    ON c.candidate_id = ta.candidate_id
                WHERE ta.test_id = ?
                  AND ta.status IN ('SUBMITTED', 'TIMED_OUT')
                ORDER BY ta.score DESC, ta.attempt_id
                """;

        List<LeaderboardEntry> entries = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, testId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int score = result.getInt("score");

                    if (result.wasNull()) {
                        throw new SQLException(
                                "Completed attempt has no saved score.");
                    }

                    entries.add(new LeaderboardEntry(
                            result.getInt("candidate_id"),
                            result.getString("full_name"),
                            score,
                            result.getInt("total_marks")
                    ));
                }
            }
        }

        return entries;
    }
}