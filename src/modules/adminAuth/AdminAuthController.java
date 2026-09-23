package modules.adminAuth;

import java.io.Console;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;
import model.AdminResult;
import modules.reports.ReportsService;
import java.util.List;
import java.time.format.DateTimeFormatter;

import model.Question;

import model.Test;
import model.Candidate;

import java.io.IOException;
import java.nio.file.Path;

import modules.testManagement.TestService;
import modules.testManagement.QuestionService;

public class AdminAuthController {
    private final AdminSession session;
    private final AdminAuthService service;
    private final TestService testService;
    private final Scanner scanner;

    public AdminAuthController(AdminSession session, Scanner scanner) {
        this.session = session;
        this.service = new AdminAuthService(session);
        this.testService = new TestService(session);
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");

            if (session.isLoggedIn()) {
                System.out.println(
                        "Logged in: " + session.getCurrentAdmin().getFullName());
            }

            System.out.println("0. Back");

            if (session.isLoggedIn()) {
                System.out.println("1. Logout");
                System.out.println("2. Create Test");
                System.out.println("3. Add Question");
                System.out.println("4. Activate Test");
                System.out.println("5. Deactivate Test");
                System.out.println("6. Reset Candidate Password");
                System.out.println("7. View Results Report");
                System.out.println("8. Test Leaderboard");
                System.out.println("9. Export Results CSV");
                System.out.println("10. My Tests");
                System.out.println("11. View Test Questions");
                System.out.println("12. Edit Question");
                System.out.println("13. Delete Question");
                System.out.println("14. Edit Test");
                System.out.println("15. Delete Test");
                System.out.println("16. Registered Candidates");
            } else {
                System.out.println("1. Login");
            }

            System.out.print("Choose: ");

            if (!scanner.hasNextLine()) {
                return;
            }

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    if (session.isLoggedIn()) {
                        service.logout();
                        System.out.println("Logged out.");
                    } else {
                        login();
                    }
                }
                case "2" -> {
                    if (session.isLoggedIn()) {
                        createTest();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "3" -> {
                    if (session.isLoggedIn()) {
                        addQuestion();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "4" -> {
                    if (session.isLoggedIn()) {
                        activateTest();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "5" -> {
                    if (session.isLoggedIn()) {
                        deactivateTest();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "6" -> {
                    if (session.isLoggedIn()) {
                        resetCandidatePassword();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "7" -> {
                    if (session.isLoggedIn()) {
                        showResultsReport();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "8" -> {
                    if (session.isLoggedIn()) {
                        showLeaderboard();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "9" -> {
                    if (session.isLoggedIn()) {
                        exportResultsCsv();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "10" -> {
                    if (session.isLoggedIn()) {
                        showMyTests();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "11" -> {
                    if (session.isLoggedIn()) {
                        showTestQuestions();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "12" -> {
                    if (session.isLoggedIn()) {
                        editQuestion();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "13" -> {
                    if (session.isLoggedIn()) {
                        deleteQuestion();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "14" -> {
                    if (session.isLoggedIn()) {
                        editTest();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "15" -> {
                    if (session.isLoggedIn()) {
                        deleteTest();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "16" -> {
                    if (session.isLoggedIn()) {
                        showCandidates();
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
                case "0" -> {
                    if (session.isLoggedIn()) {
                        service.logout();
                        System.out.println(
                                "Admin logged out successfully. Returning to main menu.");
                    } else {
                        System.out.println("Returning to main menu.");
                    }
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    private void showCandidates() {
        try {
            CandidateDirectoryService directoryService =
                    new CandidateDirectoryService(session);

            List<Candidate> candidates =
                    directoryService.getCandidates();

            System.out.println("\n--- Registered Candidates ---");

            if (candidates.isEmpty()) {
                System.out.println("No candidates registered yet.");
                return;
            }

            for (Candidate candidate : candidates) {
                System.out.println(
                        "\nCandidate ID: " + candidate.getCandidateId());
                System.out.println("Name: " + candidate.getFullName());
                System.out.println("Username: " + candidate.getUsername());
                System.out.println("Email: " + candidate.getEmail());
            }

            System.out.println("\nTotal candidates: " + candidates.size());

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not load candidates. Check the database.");
        }
    }
    private void login() {
        if (session.isLoggedIn()) {
            System.out.println("Already logged in. Logout to switch accounts.");
            return;
        }

        System.out.print("Username: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String username = scanner.nextLine();

        Console console = System.console();
        if (console == null) {
            System.out.println("Run from Windows CMD for hidden password input.");
            return;
        }

        char[] password = console.readPassword("Password: ");
        if (password == null) {
            return;
        }

        try {
            boolean success = service.login(
                    username, new String(password));

            System.out.println(success
                    ? "Admin login successful."
                    : "Invalid username or password.");

        } catch (SQLException e) {
            System.out.println("Database error. Please check the connection.");
        } catch (GeneralSecurityException e) {
            System.out.println("Password verification failed.");
        } finally {
            Arrays.fill(password, '\0');
        }
    }
    private void addQuestion() {
        System.out.println("\n--- Add Question ---");

        String[] prompts = {
                "Test ID: ",
                "Question: ",
                "Option A: ",
                "Option B: ",
                "Option C: ",
                "Option D: ",
                "Correct option (A/B/C/D): ",
                "Marks: "
        };

        String[] answers = new String[prompts.length];

        for (int i = 0; i < prompts.length; i++) {
            System.out.print(prompts[i]);

            if (!scanner.hasNextLine()) {
                return;
            }

            answers[i] = scanner.nextLine();
        }

        try {
            int testId = Integer.parseInt(answers[0].trim());
            int marks = Integer.parseInt(answers[7].trim());

            QuestionService questionService =
                    new QuestionService(session);

            int questionId = questionService.addQuestion(
                    testId,
                    answers[1],
                    answers[2],
                    answers[3],
                    answers[4],
                    answers[5],
                    answers[6],
                    marks
            );

            System.out.println(
                    "Question added successfully. Question ID: " + questionId);

        } catch (NumberFormatException e) {
            System.out.println(
                    "Test ID and marks must be valid whole numbers.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not save the question. Check the database.");
        }
    }
    private void activateTest() {
        System.out.println("\n--- Activate Test ---");
        System.out.print("Test ID: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String input = scanner.nextLine().trim();

        try {
            int testId = Integer.parseInt(input);
            testService.activateTest(testId);

            System.out.println(
                    "Test activated successfully. Test ID: " + testId);

        } catch (NumberFormatException e) {
            System.out.println("Enter a valid whole number for Test ID.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not activate the test. Check the database.");
        }
    }
    private void deactivateTest() {
        System.out.println("\n--- Deactivate Test ---");
        System.out.print("Test ID: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String input = scanner.nextLine().trim();

        try {
            int testId = Integer.parseInt(input);
            testService.deactivateTest(testId);

            System.out.println(
                    "Test deactivated successfully. Test ID: " + testId);
            System.out.println(
                    "It is hidden from candidates until reactivated.");

        } catch (NumberFormatException e) {
            System.out.println("Enter a valid whole number for Test ID.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not deactivate the test. Check the database.");
        }
    }
    private void resetCandidatePassword() {
        System.out.println("\n--- Reset Candidate Password ---");

        Console console = System.console();
        if (console == null) {
            System.out.println("Run from Windows CMD for hidden password input.");
            return;
        }

        System.out.print("Candidate username: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String username = scanner.nextLine().trim();

        System.out.print("Reset password for '" + username + "'? (yes/no): ");
        if (!scanner.hasNextLine()) {
            return;
        }

        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Password reset cancelled.");
            return;
        }

        char[] password = console.readPassword("New password: ");
        char[] confirmation = null;

        try {
            if (password == null) {
                return;
            }

            confirmation = console.readPassword("Confirm new password: ");
            if (confirmation == null) {
                return;
            }

            CandidatePasswordService passwordService =
                    new CandidatePasswordService(session);

            passwordService.resetPassword(
                    username,
                    new String(password),
                    new String(confirmation)
            );

            System.out.println(
                    "Candidate password reset successfully.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not reset the password. Check the database.");
        } catch (GeneralSecurityException e) {
            System.out.println("Password hashing failed.");
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            if (confirmation != null) {
                Arrays.fill(confirmation, '\0');
            }
        }
    }
    private void showResultsReport() {
        try {
            ReportsService reportsService = new ReportsService(session);
            List<AdminResult> results =
                    reportsService.getResultsReport();

            System.out.println("\n--- Results Report ---");

            if (results.isEmpty()) {
                System.out.println(
                        "No completed attempts for your tests yet.");
                return;
            }

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            for (AdminResult result : results) {
                System.out.println(
                        "\nTest: " + result.testTitle()
                        + " | Test ID: " + result.testId());
                System.out.println(
                        "Candidate: " + result.candidateName()
                        + " | Username: " + result.username()
                        + " | Candidate ID: " + result.candidateId());
                System.out.println(
                        "Attempt ID: " + result.attemptId());
                System.out.println(
                        "Score: " + result.score()
                        + "/" + result.totalMarks());
                System.out.println(
                        "Status: " + result.status());
                System.out.println(
                        "Finalized at: "
                        + result.submittedAt().format(formatter));
            }

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not load the report. Check the database and saved results.");
        }
    }
    private void showLeaderboard() {
        System.out.print("Test ID: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String input = scanner.nextLine().trim();

        try {
            int testId = Integer.parseInt(input);

            ReportsService reportsService = new ReportsService(session);
            List<AdminResult> results =
                    reportsService.getLeaderboard(testId);

            if (results.isEmpty()) {
                System.out.println(
                        "No completed results available for this test "
                        + "in your account.");
                return;
            }

            System.out.println(
                    "\n--- Leaderboard: "
                    + results.get(0).testTitle()
                    + " (Test ID: " + testId + ") ---");

            int rank = 0;
            int previousScore = -1;

            for (int i = 0; i < results.size(); i++) {
                AdminResult result = results.get(i);

                if (i == 0 || result.score() != previousScore) {
                    rank = i + 1;
                }

                System.out.println(
                        "Rank " + rank
                        + " | " + result.candidateName()
                        + " (" + result.username() + ")"
                        + " | Score: " + result.score()
                        + "/" + result.totalMarks());

                previousScore = result.score();
            }

        } catch (NumberFormatException e) {
            System.out.println("Enter a valid whole number for Test ID.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not load the leaderboard. Check the database.");
        }
    }
    private void exportResultsCsv() {
        try {
            ReportsService reportsService = new ReportsService(session);
            Path file = reportsService.exportResultsCsv();

            System.out.println("Results exported successfully.");
            System.out.println("File: " + file);

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not load results. Check the database.");
        } catch (IOException e) {
            System.out.println(
                    "Could not write the CSV file. Check folder access and disk space.");
        }
    }
    private void showMyTests() {
        try {
            List<Test> tests = testService.getMyTests();

            System.out.println("\n--- My Tests ---");

            if (tests.isEmpty()) {
                System.out.println("You have not created any tests yet.");
                return;
            }

            for (Test test : tests) {
                System.out.println(
                        "\nTest ID: " + test.getTestId()
                        + " | " + test.getTitle()
                        + " | Category: " + test.getCategory());
                System.out.println(
                        "Duration: " + test.getDurationMinutes()
                        + " minutes");
                System.out.println(
                        "Status: " + (test.isActive()
                                ? "ACTIVE" : "INACTIVE"));

                if (test.getDescription() != null
                        && !test.getDescription().isBlank()) {
                    System.out.println(
                            "Description: " + test.getDescription());
                }
            }

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not load your tests. Check the database.");
        }
    }
    private void showTestQuestions() {
        System.out.print("Test ID: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String input = scanner.nextLine().trim();

        try {
            int testId = Integer.parseInt(input);

            QuestionService questionService =
                    new QuestionService(session);

            List<Question> questions =
                    questionService.getTestQuestions(testId);

            System.out.println("\n--- Test Questions ---");

            if (questions.isEmpty()) {
                System.out.println(
                        "No questions found. Check that the test "
                        + "belongs to you and contains questions.");
                return;
            }

            for (Question question : questions) {
                System.out.println(
                        "\nQuestion ID: " + question.getQuestionId()
                        + " | Test ID: " + question.getTestId());
                System.out.println(question.getQuestionText());
                System.out.println("A. " + question.getOptionA());
                System.out.println("B. " + question.getOptionB());
                System.out.println("C. " + question.getOptionC());
                System.out.println("D. " + question.getOptionD());
                System.out.println(
                        "Correct option: " + question.getCorrectOption());
                System.out.println("Marks: " + question.getMarks());
            }

        } catch (NumberFormatException e) {
            System.out.println("Enter a valid whole number for Test ID.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not load questions. Check the database.");
        }
    }
    private void editQuestion() {
        System.out.println("\n--- Edit Question ---");
        System.out.println(
                "Use your own inactive test with no attempts.");
        System.out.println(
                "Enter all replacement values; blank fields are not kept.");

        String[] prompts = {
                "Test ID: ",
                "Question ID: ",
                "Question text: ",
                "Option A: ",
                "Option B: ",
                "Option C: ",
                "Option D: ",
                "Correct option (A/B/C/D): ",
                "Marks: "
        };

        String[] values = new String[prompts.length];

        for (int i = 0; i < prompts.length; i++) {
            System.out.print(prompts[i]);

            if (!scanner.hasNextLine()) {
                return;
            }

            values[i] = scanner.nextLine();
        }

        try {
            int testId = Integer.parseInt(values[0].trim());
            int questionId = Integer.parseInt(values[1].trim());
            int marks = Integer.parseInt(values[8].trim());

            System.out.print("Save these changes? (yes/no): ");

            if (!scanner.hasNextLine()
                    || !scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                System.out.println("Question update cancelled.");
                return;
            }

            QuestionService questionService =
                    new QuestionService(session);

            questionService.updateQuestion(
                    questionId,
                    testId,
                    values[2],
                    values[3],
                    values[4],
                    values[5],
                    values[6],
                    values[7],
                    marks
            );

            System.out.println("Question updated successfully.");

        } catch (NumberFormatException e) {
            System.out.println(
                    "Test ID, Question ID and marks must be valid whole numbers.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not update the question. Check the database.");
        }
    }
    private void deleteQuestion() {
        System.out.println("\n--- Delete Question ---");

        System.out.print("Test ID: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String testInput = scanner.nextLine().trim();

        System.out.print("Question ID: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String questionInput = scanner.nextLine().trim();

        try {
            int testId = Integer.parseInt(testInput);
            int questionId = Integer.parseInt(questionInput);

            if (testId <= 0 || questionId <= 0) {
                System.out.println(
                        "Test ID and Question ID must be positive.");
                return;
            }

            QuestionService questionService =
                    new QuestionService(session);

            Question target = null;

            for (Question question :
                    questionService.getTestQuestions(testId)) {
                if (question.getQuestionId() == questionId) {
                    target = question;
                    break;
                }
            }

            if (target == null) {
                System.out.println(
                        "Question not found in your specified test.");
                return;
            }

            System.out.println("Question: " + target.getQuestionText());
            System.out.println("This deletion cannot be undone.");
            System.out.print(
                    "Type DELETE to confirm, or anything else to cancel: ");

            if (!scanner.hasNextLine()
                    || !scanner.nextLine().trim().equals("DELETE")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            questionService.deleteQuestion(questionId, testId);

            System.out.println("Question deleted successfully.");

        } catch (NumberFormatException e) {
            System.out.println(
                    "Test ID and Question ID must be valid whole numbers.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not delete the question. Check the database.");
        }
    }
    private void deleteTest() {
        System.out.println("\n--- Delete Test ---");
        System.out.println(
                "Only your own inactive test with no attempts can be deleted.");

        System.out.print("Test ID (0 to cancel): ");
        if (!scanner.hasNextLine()) {
            return;
        }

        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Test deletion cancelled.");
            return;
        }

        try {
            int testId = Integer.parseInt(input);

            if (testId <= 0) {
                System.out.println("Test ID must be positive.");
                return;
            }

            System.out.println(
                    "This permanently deletes the test and all its questions.");
            System.out.print(
                    "Delete Test ID " + testId + "? (yes/no): ");

            if (!scanner.hasNextLine()
                    || !scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                System.out.println("Test deletion cancelled.");
                return;
            }

            testService.deleteTest(testId);
            System.out.println("Test and its questions deleted successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Test ID must be a valid whole number.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not delete the test. Check the database.");
        }
    }
    private void editTest() {
        System.out.println("\n--- Edit Test ---");
        System.out.println(
                "Use your own inactive test with no attempts.");
        System.out.println(
                "Enter all replacement values. "
                + "A blank description will clear the old description.");

        String[] prompts = {
                "Test ID: ",
                "New title: ",
                "New description (optional): ",
                "New category (e.g. Java, SQL, Aptitude): ",
                "New duration in minutes: "
        };

        String[] values = new String[prompts.length];

        for (int i = 0; i < prompts.length; i++) {
            System.out.print(prompts[i]);

            if (!scanner.hasNextLine()) {
                return;
            }

            values[i] = scanner.nextLine();
        }

        try {
            int testId = Integer.parseInt(values[0].trim());
            int duration = Integer.parseInt(values[4].trim());

            System.out.print(
                    "Save changes to Test ID " + testId + "? (yes/no): ");

            if (!scanner.hasNextLine()
                    || !scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                System.out.println("Test update cancelled.");
                return;
            }

            testService.updateTest(
                    testId, values[1], values[2], values[3], duration);

            System.out.println("Test updated successfully.");

        } catch (NumberFormatException e) {
            System.out.println(
                    "Test ID and duration must be valid whole numbers.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not update the test. Check the database.");
        }
    }
    private void createTest() {
        System.out.println("\n--- Create Test ---");

        System.out.print("Title: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String title = scanner.nextLine();

        System.out.print("Description (optional): ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String description = scanner.nextLine();

        System.out.print("Category (e.g. Java, SQL, Aptitude): ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String category = scanner.nextLine();

        System.out.print("Duration in minutes: ");
        if (!scanner.hasNextLine()) {
            return;
        }
        String durationInput = scanner.nextLine().trim();

        try {
            int duration = Integer.parseInt(durationInput);

            int testId = testService.createTest(
                    title, description, category, duration);

            System.out.println(
                    "Test created successfully. Test ID: " + testId);
            System.out.println(
                    "Test is inactive. Add questions before activating it.");

        } catch (NumberFormatException e) {
            System.out.println("Enter a valid whole number for duration.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Could not save the test. Check the database.");
        }
    }
}
