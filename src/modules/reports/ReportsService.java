package modules.reports;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Comparator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import model.AdminResult;
import modules.adminAuth.AdminSession;

public class ReportsService {
    private final ReportsDAO dao;
    private final AdminSession session;

    public ReportsService(AdminSession session) {
        this.session = Objects.requireNonNull(session);
        this.dao = new ReportsDAO();
    }

    public List<AdminResult> getResultsReport() throws SQLException {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException(
                    "Please log in as admin.");
        }

        int adminId = session.getCurrentAdmin().getAdminId();

        return dao.findResultsByAdmin(adminId);
    }
    public List<AdminResult> getLeaderboard(int testId)
            throws SQLException {

        if (testId <= 0) {
            throw new IllegalArgumentException(
                    "Test ID must be positive.");
        }

        return getResultsReport().stream()
                .filter(result -> result.testId() == testId)
                .sorted(Comparator.comparingInt(AdminResult::score)
                        .reversed()
                        .thenComparingInt(AdminResult::attemptId))
                .toList();
    }
    public Path exportResultsCsv() throws SQLException, IOException {
        List<AdminResult> results = getResultsReport();

        if (results.isEmpty()) {
            throw new IllegalStateException(
                    "No completed results to export.");
        }

        Path directory = Path.of("exports").toAbsolutePath();
        Files.createDirectories(directory);

        Path file = Files.createTempFile(
                directory, "results-", ".csv");

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    file, StandardCharsets.UTF_8)) {

                writer.write(
                        "Attempt ID,Test ID,Test Title,Candidate ID,"
                        + "Candidate Name,Username,Status,Score,"
                        + "Total Marks,Finalized At");
                writer.newLine();

                for (AdminResult result : results) {
                    writer.write(String.join(",",
                            Integer.toString(result.attemptId()),
                            Integer.toString(result.testId()),
                            csvCell(result.testTitle()),
                            Integer.toString(result.candidateId()),
                            csvCell(result.candidateName()),
                            csvCell(result.username()),
                            csvCell(result.status()),
                            Integer.toString(result.score()),
                            Integer.toString(result.totalMarks()),
                            csvCell(result.submittedAt().toString())
                    ));
                    writer.newLine();
                }
            }

            return file;

        } catch (IOException e) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw e;
        }
    }

    private String csvCell(String value) {
        if (value == null) {
            return "\"\"";
        }

        String trimmed = value.stripLeading();

        if (!trimmed.isEmpty()
                && "=+-@".indexOf(trimmed.charAt(0)) >= 0) {
            value = "'" + value;
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}