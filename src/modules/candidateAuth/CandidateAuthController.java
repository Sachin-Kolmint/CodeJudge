package modules.candidateAuth;

import model.Candidate;
import model.Test;
import java.util.List;
import modules.testAttempt.TestAttemptService;
import java.util.Scanner;
import java.sql.SQLException;
import java.security.GeneralSecurityException;
import modules.testAttempt.TestAttemptController;
import model.CandidateResult;
import modules.candidateResults.CandidateResultsService;
import java.time.format.DateTimeFormatter;

public class CandidateAuthController {
    private final CandidateAuthService service = new CandidateAuthService();
    private final CandidateSession session;
        private final Scanner scanner;

    public CandidateAuthController(CandidateSession session,
                                   Scanner scanner) {
        this.session = java.util.Objects.requireNonNull(session);
        this.scanner = java.util.Objects.requireNonNull(scanner);
    }

    public void start() {
        while (true) {
            if (session.isLoggedIn()) {
                System.out.println("\nWelcome, "
                        + session.getCurrentCandidate().getFullName());
                System.out.println("0. Back");
                System.out.println("1. Logout");
                System.out.println("2. Available Tests");
                System.out.println("3. Change Username");
                System.out.println("4. Change Password");
                System.out.println("5. Start / Resume Test");
                System.out.println("6. My Results");

                String choice = read("Choose: ");

                if (choice.equals("1")) {
                    session.logout();
                    System.out.println("Logged out successfully.");
                } else if (choice.equals("2")) {
                    showAvailableTests();
                } else if (choice.equals("3")) {
                    changeUsername();
                } else if (choice.equals("4")) {
                    changePassword();
                } else if (choice.equals("5")) {
                    TestAttemptController controller =
                            new TestAttemptController(session, scanner);
                    controller.start();
                } else if (choice.equals("6")) {
                    showMyResults();
                } else if (choice.equals("0")) {
                    session.logout();
                    System.out.println(
                            "Candidate logged out successfully. Returning to main menu.");
                    return;
                } else {
                    System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("\n--- CodeJudge Candidate Authentication ---");
                System.out.println("0. Back");
                System.out.println("1. Register");
                System.out.println("2. Login");

                String choice = read("Choose: ");

                try {
                    switch (choice) {
                        case "1" -> register();
                        case "2" -> login();
                        case "0" -> {
                            System.out.println("Returning to main menu.");
                            return;
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                } catch (SQLException e) {
                    System.out.println("Database operation failed. Check your connection.");
                } catch (GeneralSecurityException e) {
                    System.out.println("Password processing failed.");
                }
            }
        }
    }
    private void showAvailableTests() {
        try {
            TestAttemptService testService =
                    new TestAttemptService(session);

            List<Test> tests = testService.getAvailableTests();

            System.out.println("\n--- Available Tests ---");

            if (tests.isEmpty()) {
                System.out.println("No tests are currently available.");
                return;
            }

            for (Test test : tests) {
                System.out.println(
                        "ID: " + test.getTestId()
                        + " | " + test.getTitle()
                        + " | Category: " + test.getCategory()
                        + " | Duration: "
                        + test.getDurationMinutes() + " minutes");
            }

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not load tests. Check the database.");
        }
    }
    private void changeUsername() {
        System.out.println("\n--- Change Username ---");

        try {
            String newUsername = read("New username: ");

            System.out.println("Enter your current password.");
            String currentPassword = readPassword();

            service.changeUsername(
                    session, currentPassword, newUsername);

            System.out.println(
                    "Username changed successfully. New username: "
                    + session.getCurrentCandidate().getUsername());

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not change username. Check the database.");
        } catch (GeneralSecurityException e) {
            System.out.println("Password verification failed.");
        }
    }

    private void changePassword() {
        System.out.println("\n--- Change Password ---");

        try {
            System.out.println("Enter your current password.");
            String currentPassword = readPassword();

            System.out.println("Enter your new password.");
            String newPassword = readPassword();

            System.out.println("Confirm your new password.");
            String confirmation = readPassword();

            service.changePassword(
                    session, currentPassword, newPassword, confirmation);

            System.out.println(
                    "Password changed successfully. Use it for your next login.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Could not change password. Check the database.");
        } catch (GeneralSecurityException e) {
            System.out.println("Password processing failed.");
        }
    }
    private void showMyResults() {
        try {
            CandidateResultsService resultsService =
                    new CandidateResultsService(session);

            List<CandidateResult> results =
                    resultsService.getMyResults();

            System.out.println("\n--- My Results ---");

            if (results.isEmpty()) {
                System.out.println("No completed tests yet.");
                return;
            }

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            for (CandidateResult result : results) {
                System.out.println(
                        "\nTest: " + result.testTitle()
                        + " | Test ID: " + result.testId());
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
                    "Could not load results. Check the database and saved results.");
        }
    }
    private void register() throws SQLException, GeneralSecurityException {
        String fullName = read("Full name: ");
        String username = read("Username: ");
        String email = read("Email: ");
        String password = readPassword();

        boolean registered = service.register(
                fullName, username, email, password);

        System.out.println(registered
                ? "Registration successful. Please log in."
                : "Username or email already exists.");
    }

    private void login() throws SQLException, GeneralSecurityException {
        String username = read("Username: ");
        String password = readPassword();

        Candidate candidate = service.login(username, password);

        if (candidate == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        session.login(candidate);
        System.out.println("Login successful.");
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String readPassword() {
        if (System.console() != null) {
            char[] password = System.console().readPassword("Password: ");
            if (password == null) {
                throw new IllegalArgumentException("Password is required.");
            }
            try {
                return new String(password);
            } finally {
                java.util.Arrays.fill(password, '\0');
            }
        }
        return read("Password (visible): ");
    }
}