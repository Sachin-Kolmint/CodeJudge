package modules.testAttempt;

import config.DBConnection;
import model.Test;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.TestAttempt;
import java.sql.Timestamp;
import model.AttemptQuestion;

public class TestAttemptDAO {

    public List<Test> findAvailableTests() throws SQLException {
        String sql = """
                SELECT test_id, title, description, category,
                       duration_minutes, total_marks, created_by, is_active
                FROM tests
                WHERE is_active = TRUE
                  AND EXISTS (
                      SELECT 1 FROM questions
                      WHERE questions.test_id = tests.test_id
                  )
                ORDER BY test_id
                """;

        List<Test> tests = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                tests.add(new Test(
                        result.getInt("test_id"),
                        result.getString("title"),
                        result.getString("description"),
                        result.getString("category"),
                        result.getInt("duration_minutes"),
                        result.getInt("total_marks"),
                        result.getInt("created_by"),
                        result.getBoolean("is_active")
                ));
            }
        }

        return tests;
    }
    public int createAttempt(int candidateId, int testId)
            throws SQLException {

        String sql = """
                INSERT INTO test_attempts
                    (candidate_id, test_id, started_at,
                     deadline_at, total_marks)
                SELECT ?, t.test_id, CURRENT_TIMESTAMP,
                       TIMESTAMPADD(
                           MINUTE, t.duration_minutes, CURRENT_TIMESTAMP),
                       (
                           SELECT SUM(q.marks)
                           FROM questions q
                           WHERE q.test_id = t.test_id
                       )
                FROM tests t
                WHERE t.test_id = ?
                  AND t.is_active = TRUE
                  AND EXISTS (
                      SELECT 1 FROM questions q
                      WHERE q.test_id = t.test_id
                  )
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, candidateId);
            statement.setInt(2, testId);

            if (statement.executeUpdate() == 0) {
                return 0;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }

                throw new SQLException(
                        "Created attempt ID was not returned.");
            }
        }
    }
    public TestAttempt findAttempt(int candidateId, int testId)
            throws SQLException {

        String sql = """
                SELECT attempt_id, candidate_id, test_id,
                       started_at, deadline_at, submitted_at,
                       status, score, total_marks
                FROM test_attempts
                WHERE candidate_id = ? AND test_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, candidateId);
            statement.setInt(2, testId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                Timestamp submittedAt =
                        result.getTimestamp("submitted_at");

                int storedScore = result.getInt("score");
                Integer score = result.wasNull() ? null : storedScore;

                return new TestAttempt(
                        result.getInt("attempt_id"),
                        result.getInt("candidate_id"),
                        result.getInt("test_id"),
                        result.getTimestamp("started_at").toLocalDateTime(),
                        result.getTimestamp("deadline_at").toLocalDateTime(),
                        submittedAt == null
                                ? null : submittedAt.toLocalDateTime(),
                        result.getString("status"),
                        score,
                        result.getInt("total_marks")
                );
            }
        }
    }
    public boolean isAttemptOpen(int attemptId, int candidateId)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM test_attempts
                WHERE attempt_id = ?
                  AND candidate_id = ?
                  AND status = 'IN_PROGRESS'
                  AND deadline_at > CURRENT_TIMESTAMP
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, attemptId);
            statement.setInt(2, candidateId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
    public List<AttemptQuestion> findAttemptQuestions(
            int attemptId, int candidateId) throws SQLException {

        String sql = """
                SELECT q.question_id, q.question_text,
                       q.option_a, q.option_b, q.option_c, q.option_d,
                       q.marks, aa.selected_option
                FROM test_attempts ta
                JOIN questions q ON q.test_id = ta.test_id
                LEFT JOIN attempt_answers aa
                    ON aa.attempt_id = ta.attempt_id
                    AND aa.question_id = q.question_id
                WHERE ta.attempt_id = ?
                  AND ta.candidate_id = ?
                  AND ta.status = 'IN_PROGRESS'
                  AND ta.deadline_at > CURRENT_TIMESTAMP
                ORDER BY q.question_id
                """;

        List<AttemptQuestion> questions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, attemptId);
            statement.setInt(2, candidateId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    questions.add(new AttemptQuestion(
                            result.getInt("question_id"),
                            result.getString("question_text"),
                            result.getString("option_a"),
                            result.getString("option_b"),
                            result.getString("option_c"),
                            result.getString("option_d"),
                            result.getInt("marks"),
                            result.getString("selected_option")
                    ));
                }
            }
        }

        return questions;
    }
    public boolean saveAnswer(int attemptId, int candidateId,
                              int questionId, String selectedOption)
            throws SQLException {

        String lockSql = """
                SELECT attempt_id
                FROM test_attempts
                WHERE attempt_id = ? AND candidate_id = ?
                FOR UPDATE
                """;

        String saveSql = """
                INSERT INTO attempt_answers
                    (attempt_id, question_id, selected_option)
                SELECT ta.attempt_id, q.question_id, ?
                FROM test_attempts ta
                JOIN questions q ON q.test_id = ta.test_id
                WHERE ta.attempt_id = ?
                  AND ta.candidate_id = ?
                  AND q.question_id = ?
                  AND ta.status = 'IN_PROGRESS'
                  AND ta.deadline_at > CURRENT_TIMESTAMP
                ON DUPLICATE KEY UPDATE selected_option = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement lock =
                             connection.prepareStatement(lockSql)) {

                    lock.setInt(1, attemptId);
                    lock.setInt(2, candidateId);

                    try (ResultSet result = lock.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                int affectedRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(saveSql)) {

                    statement.setString(1, selectedOption);
                    statement.setInt(2, attemptId);
                    statement.setInt(3, candidateId);
                    statement.setInt(4, questionId);
                    statement.setString(5, selectedOption);

                    affectedRows = statement.executeUpdate();
                }

                connection.commit();
                return affectedRows > 0;

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
}