package modules.testManagement;

import config.DBConnection;

import model.Test;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDAO {

    public int createTest(String title, String description,
                          int durationMinutes, int createdBy)
            throws SQLException {
        return createTest(title, description, "General",
                durationMinutes, createdBy);
    }

    public int createTest(String title, String description,
                          String category, int durationMinutes,
                          int createdBy) throws SQLException {
        return createTest(title, description, category,
                durationMinutes, 1, createdBy);
    }

    public int createTest(String title, String description,
                          String category, int durationMinutes,
                          int totalMarks, int createdBy)
            throws SQLException {

        String sql = """
                INSERT INTO tests
                    (title, description, category,
                     duration_minutes, total_marks, created_by)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, title);
            statement.setString(2, description);
            statement.setString(3, category);
            statement.setInt(4, durationMinutes);
            statement.setInt(5, totalMarks);
            statement.setInt(6, createdBy);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Test could not be created.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }

                throw new SQLException("Created test ID was not returned.");
            }
        }
    }
    public boolean activateTest(int testId, int adminId)
            throws SQLException {

        String sql = """
                UPDATE tests
                SET is_active = TRUE
                WHERE test_id = ?
                  AND created_by = ?
                  AND is_active = FALSE
                  AND EXISTS (
                      SELECT 1 FROM questions
                      WHERE questions.test_id = tests.test_id
                  )
                  AND total_marks = (
                      SELECT SUM(q.marks)
                      FROM questions q
                      WHERE q.test_id = tests.test_id
                  )
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, testId);
            statement.setInt(2, adminId);

            return statement.executeUpdate() == 1;
        }
    }
    public boolean deactivateTest(int testId, int adminId)
            throws SQLException {

        String sql = """
                UPDATE tests
                SET is_active = FALSE
                WHERE test_id = ?
                  AND created_by = ?
                  AND is_active = TRUE
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, testId);
            statement.setInt(2, adminId);

            return statement.executeUpdate() == 1;
        }
    }
    public List<Test> findByAdminId(int adminId) throws SQLException {
        String sql = """
                SELECT test_id, title, description, category,
                       duration_minutes, total_marks, created_by, is_active
                FROM tests
                WHERE created_by = ?
                ORDER BY test_id
                """;

        List<Test> tests = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, adminId);

            try (ResultSet result = statement.executeQuery()) {
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
        }

        return tests;
    }
    public boolean updateTest(int testId, int adminId,
                              String title, String description,
                              String category, int durationMinutes,
                              int totalMarks) throws SQLException {

        String sql = """
                UPDATE tests t
                SET title = ?,
                    description = ?,
                    category = ?,
                    duration_minutes = ?,
                    total_marks = ?
                WHERE t.test_id = ?
                  AND t.created_by = ?
                  AND t.is_active = FALSE
                  AND NOT EXISTS (
                      SELECT 1
                      FROM test_attempts ta
                      WHERE ta.test_id = t.test_id
                  )
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, title);
            statement.setString(2, description);
            statement.setString(3, category);
            statement.setInt(4, durationMinutes);
            statement.setInt(5, totalMarks);
            statement.setInt(6, testId);
            statement.setInt(7, adminId);

            return statement.executeUpdate() == 1;
        }
    }
    public boolean deleteTest(int testId, int adminId)
            throws SQLException {

        String lockSql = """
                SELECT test_id
                FROM tests
                WHERE test_id = ?
                  AND created_by = ?
                  AND is_active = FALSE
                FOR UPDATE
                """;

        String attemptsSql = """
                SELECT 1 FROM test_attempts
                WHERE test_id = ?
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(lockSql)) {
                    statement.setInt(1, testId);
                    statement.setInt(2, adminId);

                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(attemptsSql)) {
                    statement.setInt(1, testId);

                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "DELETE FROM questions WHERE test_id = ?")) {
                    statement.setInt(1, testId);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "DELETE FROM tests WHERE test_id = ? "
                                     + "AND created_by = ? AND is_active = FALSE")) {
                    statement.setInt(1, testId);
                    statement.setInt(2, adminId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Test could not be deleted.");
                    }
                }

                connection.commit();
                return true;

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
