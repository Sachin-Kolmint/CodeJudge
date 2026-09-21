package modules.evaluation;

import config.DBConnection;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EvaluationDAO {

    public int submitAttempt(int attemptId, int candidateId)
            throws SQLException {

        String lockSql = """
                SELECT status, score
                FROM test_attempts
                WHERE attempt_id = ? AND candidate_id = ?
                FOR UPDATE
                """;

        String gradeSql = """
                UPDATE attempt_answers aa
                JOIN questions q ON q.question_id = aa.question_id
                JOIN test_attempts ta
                    ON ta.attempt_id = aa.attempt_id
                    AND ta.test_id = q.test_id
                SET aa.marks_awarded =
                    CASE
                        WHEN aa.selected_option = q.correct_option
                        THEN q.marks
                        ELSE 0
                    END
                WHERE ta.attempt_id = ?
                  AND ta.candidate_id = ?
                """;

        String finishSql = """
                UPDATE test_attempts
                SET score = (
                        SELECT COALESCE(SUM(aa.marks_awarded), 0)
                        FROM attempt_answers aa
                        JOIN questions q
                            ON q.question_id = aa.question_id
                        WHERE aa.attempt_id = test_attempts.attempt_id
                          AND q.test_id = test_attempts.test_id
                    ),
                    status = CASE
                        WHEN deadline_at <= CURRENT_TIMESTAMP
                        THEN 'TIMED_OUT'
                        ELSE 'SUBMITTED'
                    END,
                    submitted_at = CURRENT_TIMESTAMP
                WHERE attempt_id = ?
                  AND candidate_id = ?
                  AND status = 'IN_PROGRESS'
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(lockSql)) {

                    statement.setInt(1, attemptId);
                    statement.setInt(2, candidateId);

                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new IllegalArgumentException(
                                    "Attempt not found for this candidate.");
                        }

                        if (!"IN_PROGRESS".equals(result.getString("status"))) {
                            int score = result.getInt("score");

                            if (result.wasNull()) {
                                throw new SQLException(
                                        "Completed attempt has no saved score.");
                            }

                            connection.commit();
                            return score;
                        }
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(gradeSql)) {

                    statement.setInt(1, attemptId);
                    statement.setInt(2, candidateId);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(finishSql)) {

                    statement.setInt(1, attemptId);
                    statement.setInt(2, candidateId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not finalize the attempt.");
                    }
                }

                int score;

                try (PreparedStatement statement =
                             connection.prepareStatement(lockSql)) {

                    statement.setInt(1, attemptId);
                    statement.setInt(2, candidateId);

                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException(
                                    "Could not read the saved result.");
                        }

                        score = result.getInt("score");

                        if (result.wasNull()) {
                            throw new SQLException(
                                    "Evaluation did not produce a score.");
                        }
                    }
                }

                connection.commit();
                return score;

            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }
    public void finalizeExpiredAttempts() throws SQLException {
        String sql = """
                SELECT attempt_id, candidate_id
                FROM test_attempts
                WHERE status = 'IN_PROGRESS'
                  AND deadline_at <= CURRENT_TIMESTAMP
                ORDER BY attempt_id
                """;

        List<int[]> expiredAttempts = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                expiredAttempts.add(new int[] {
                        result.getInt("attempt_id"),
                        result.getInt("candidate_id")
                });
            }
        }

        SQLException failure = null;

        for (int[] attempt : expiredAttempts) {
            try {
                submitAttempt(attempt[0], attempt[1]);
            } catch (SQLException e) {
                if (failure == null) {
                    failure = new SQLException(
                            "Some expired attempts could not be finalized.");
                }
                failure.addSuppressed(e);
            }
        }

        if (failure != null) {
            throw failure;
        }
    }
}