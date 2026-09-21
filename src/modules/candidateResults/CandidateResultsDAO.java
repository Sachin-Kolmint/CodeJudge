package modules.candidateResults;

import config.DBConnection;
import model.CandidateResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CandidateResultsDAO {

    public List<CandidateResult> findByCandidateId(int candidateId)
            throws SQLException {

        String sql = """
                SELECT ta.attempt_id, ta.test_id, t.title,
                       ta.status, ta.score, ta.total_marks,
                       ta.submitted_at
                FROM test_attempts ta
                JOIN tests t ON t.test_id = ta.test_id
                WHERE ta.candidate_id = ?
                  AND ta.status IN ('SUBMITTED', 'TIMED_OUT')
                ORDER BY ta.submitted_at DESC, ta.attempt_id DESC
                """;

        List<CandidateResult> results = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, candidateId);

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

                    results.add(new CandidateResult(
                            result.getInt("attempt_id"),
                            result.getInt("test_id"),
                            result.getString("title"),
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