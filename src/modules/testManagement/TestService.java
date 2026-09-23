package modules.testManagement;

import java.sql.SQLException;
import java.util.Objects;

import model.Test;
import java.util.List;

import modules.adminAuth.AdminSession;

public class TestService {
    private final TestDAO dao;
    private final AdminSession session;

    public TestService(AdminSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new TestDAO();
    }

    public int createTest(String title, String description,
                          int durationMinutes) throws SQLException {
        return createTest(title, description, "General", durationMinutes);
    }

    public int createTest(String title, String description,
                          String category, int durationMinutes)
            throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as admin.");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Test title is required.");
        }

        title = title.trim();

        if (title.length() > 150) {
            throw new IllegalArgumentException(
                    "Test title must not exceed 150 characters.");
        }

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than zero.");
        }

        if (description != null) {
            description = description.trim();
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }

        category = category.trim();

        if (category.length() > 50) {
            throw new IllegalArgumentException(
                    "Category must not exceed 50 characters.");
        }

        return dao.createTest(
                title,
                description,
                category,
                durationMinutes,
                session.getCurrentAdmin().getAdminId()
        );
    }
    public void activateTest(int testId) throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as admin.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException(
                    "Test ID must be positive.");
        }

        boolean activated = dao.activateTest(
                testId, session.getCurrentAdmin().getAdminId());

        if (!activated) {
            throw new IllegalStateException(
                    "Activation failed. Choose your own inactive test "
                    + "with at least one question.");
        }
    }
    public void deactivateTest(int testId) throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as admin.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException(
                    "Test ID must be positive.");
        }

        boolean deactivated = dao.deactivateTest(
                testId, session.getCurrentAdmin().getAdminId());

        if (!deactivated) {
            throw new IllegalStateException(
                    "Deactivation failed. Choose your own active test.");
        }
    }
    public List<Test> getMyTests() throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as admin.");
        }

        return dao.findByAdminId(
                session.getCurrentAdmin().getAdminId());
    }
    public void updateTest(int testId, String title,
                           String description, String category,
                           int durationMinutes) throws SQLException {

        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as admin.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException(
                    "Test ID must be positive.");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Test title is required.");
        }

        title = title.trim();

        if (title.length() > 150) {
            throw new IllegalArgumentException(
                    "Test title must not exceed 150 characters.");
        }

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than zero.");
        }

        if (description != null) {
            description = description.trim();
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }

        category = category.trim();

        if (category.length() > 50) {
            throw new IllegalArgumentException(
                    "Category must not exceed 50 characters.");
        }

        boolean updated = dao.updateTest(
                testId,
                session.getCurrentAdmin().getAdminId(),
                title,
                description,
                category,
                durationMinutes
        );

        if (!updated) {
            throw new IllegalStateException(
                    "Update failed. Choose your own inactive test "
                    + "with no attempts.");
        }
    }
    public void deleteTest(int testId) throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Please log in as admin.");
        }

        if (testId <= 0) {
            throw new IllegalArgumentException("Test ID must be positive.");
        }

        boolean deleted = dao.deleteTest(
                testId, session.getCurrentAdmin().getAdminId());

        if (!deleted) {
            throw new IllegalStateException(
                    "Delete failed. Choose your own inactive test "
                    + "with no attempts.");
        }
    }
}
