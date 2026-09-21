package modules.testManagement;

import config.DBConnection;
import model.Question;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QuestionDAO {

    public int addQuestion(Question question, int adminId)
            throws SQLException {

        String sql = """
                INSERT INTO questions
                    (test_id, question_text, option_a, option_b,
                     option_c, option_d, correct_option, marks)
                SELECT test_id, ?, ?, ?, ?, ?, ?, ?
                FROM tests
                WHERE test_id = ?
                  AND created_by = ?
                  AND is_active = FALSE
                  AND NOT EXISTS (
                      SELECT 1 FROM test_attempts
                      WHERE test_attempts.test_id = tests.test_id
                  )
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, question.getQuestionText());
            statement.setString(2, question.getOptionA());
            statement.setString(3, question.getOptionB());
            statement.setString(4, question.getOptionC());
            statement.setString(5, question.getOptionD());
            statement.setString(6, question.getCorrectOption());
            statement.setInt(7, question.getMarks());
            statement.setInt(8, question.getTestId());
            statement.setInt(9, adminId);

            if (statement.executeUpdate() == 0) {
                return 0;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }

                throw new SQLException("Created question ID was not returned.");
            }
        }
    }
    public List<Question> findByTestId(int testId, int adminId)
            throws SQLException {

        String sql = """
                SELECT q.question_id, q.test_id, q.question_text,
                       q.option_a, q.option_b, q.option_c, q.option_d,
                       q.correct_option, q.marks
                FROM questions q
                JOIN tests t ON t.test_id = q.test_id
                WHERE q.test_id = ? AND t.created_by = ?
                ORDER BY q.question_id
                """;

        List<Question> questions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, testId);
            statement.setInt(2, adminId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    questions.add(new Question(
                            result.getInt("question_id"),
                            result.getInt("test_id"),
                            result.getString("question_text"),
                            result.getString("option_a"),
                            result.getString("option_b"),
                            result.getString("option_c"),
                            result.getString("option_d"),
                            result.getString("correct_option"),
                            result.getInt("marks")
                    ));
                }
            }
        }

        return questions;
    }
    public boolean updateQuestion(Question question, int adminId)
            throws SQLException {

        String sql = """
                UPDATE questions q
                JOIN tests t ON t.test_id = q.test_id
                SET q.question_text = ?,
                    q.option_a = ?,
                    q.option_b = ?,
                    q.option_c = ?,
                    q.option_d = ?,
                    q.correct_option = ?,
                    q.marks = ?
                WHERE q.question_id = ?
                  AND q.test_id = ?
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

            statement.setString(1, question.getQuestionText());
            statement.setString(2, question.getOptionA());
            statement.setString(3, question.getOptionB());
            statement.setString(4, question.getOptionC());
            statement.setString(5, question.getOptionD());
            statement.setString(6, question.getCorrectOption());
            statement.setInt(7, question.getMarks());
            statement.setInt(8, question.getQuestionId());
            statement.setInt(9, question.getTestId());
            statement.setInt(10, adminId);

            return statement.executeUpdate() == 1;
        }
    }
    public boolean deleteQuestion(int questionId, int testId,
                                  int adminId) throws SQLException {

        String sql = """
                DELETE q
                FROM questions q
                JOIN tests t ON t.test_id = q.test_id
                WHERE q.question_id = ?
                  AND q.test_id = ?
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

            statement.setInt(1, questionId);
            statement.setInt(2, testId);
            statement.setInt(3, adminId);

            return statement.executeUpdate() == 1;
        }
    }
}