package modules.reports;

import config.DBConnection;
import model.AdminResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {

    public List<AdminResult> findResultsByAdmin(int adminId)
            throws SQLException {

        String sql = """
                SELECT ta.attempt_id, ta.test_id, t.title,
                       c.candidate_id, c.full_name, c.username,
                       ta.status, ta.score, ta.total_marks,
                       ta.submitted_at
                FROM test_attempts ta
                JOIN tests t ON t.test_id = ta.test_id
                JOIN candidates c ON c.candidate_id = ta.candidate_id
                WHERE t.created_by = ?
                  AND ta.status IN ('SUBMITTED', 'TIMED_OUT')
                ORDER BY t.test_id, ta.score DESC, ta.attempt_id
                """;

        List<AdminResult> results = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, adminId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int score = result.getInt("score");

                    if (result.wasNull()) {
                        throw new SQLException(
                                "Completed attempt has no saved score.");
                    }

                    Timestamp submittedAt =
                            result.getTimestamp("submitted_at");

                    if (submittedAt == null) {
                        throw new SQLException(
                                "Completed attempt has no submission time.");
                    }

                    results.add(new AdminResult(
                            result.getInt("attempt_id"),
                            result.getInt("test_id"),
                            result.getString("title"),
                            result.getInt("candidate_id"),
                            result.getString("full_name"),
                            result.getString("username"),
                            result.getString("status"),
                            score,
                            result.getInt("total_marks"),
                            submittedAt.toLocalDateTime()
                    ));
                }
            }
        }

        return results;
    }
}