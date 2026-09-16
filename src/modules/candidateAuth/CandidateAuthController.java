package modules.candidateAuth;

import model.Candidate;
import java.util.Scanner;
import java.sql.SQLException;
import java.security.GeneralSecurityException;

public class CandidateAuthController {
    private final CandidateAuthService service = new CandidateAuthService();
    private final CandidateSession session;
    private final Scanner scanner = new Scanner(System.in);

    public CandidateAuthController(CandidateSession session) {
        this.session = java.util.Objects.requireNonNull(session);
    }

    public void start() {
        while (true) {
            if (session.isLoggedIn()) {
                System.out.println("\nWelcome, "
                        + session.getCurrentCandidate().getFullName());
                System.out.println("1. Logout");
                System.out.println("0. Exit");

                String choice = read("Choose: ");

                if (choice.equals("1")) {
                    session.logout();
                    System.out.println("Logged out successfully.");
                } else if (choice.equals("0")) {
                    session.logout();
                    return;
                } else {
                    System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("\n--- CodeJudge Candidate Authentication ---");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");

                String choice = read("Choose: ");

                try {
                    switch (choice) {
                        case "1" -> register();
                        case "2" -> login();
                        case "0" -> { return; }
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