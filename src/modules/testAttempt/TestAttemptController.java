package modules.testAttempt;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import modules.evaluation.EvaluationService;

import model.AttemptQuestion;
import model.TestAttempt;
import modules.candidateAuth.CandidateSession;

public class TestAttemptController {
    private final TestAttemptService service;
    private final EvaluationService evaluationService;
    private final Scanner scanner;

    public TestAttemptController(CandidateSession session, Scanner scanner) {
        this.service = new TestAttemptService(session);
        this.evaluationService = new EvaluationService(session);
        this.scanner = Objects.requireNonNull(scanner);
    }

    public void start() {
        System.out.println("\n--- Start / Resume Test ---");
        System.out.print("Test ID: ");

        if (!scanner.hasNextLine()) {
            return;
        }
        String input = scanner.nextLine().trim();

        try {
            int testId = Integer.parseInt(input);

            if (testId <= 0) {
                System.out.println("Test ID must be positive.");
                return;
            }

            System.out.println(
                    "A new attempt starts the timer immediately.");
            System.out.println(
                    "Resuming keeps the original deadline. Back does not pause it.");
            System.out.print("Continue? (yes/no): ");

            if (!scanner.hasNextLine()
                    || !scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                System.out.println("Cancelled.");
                return;
            }

            TestAttempt attempt = service.startOrResumeTest(testId);

            System.out.println("Attempt ID: " + attempt.getAttemptId());
            System.out.println("Deadline: " + attempt.getDeadlineAt());
            System.out.println("Total marks: " + attempt.getTotalMarks());

            answerQuestions(attempt.getAttemptId());

        } catch (NumberFormatException e) {
            System.out.println("Enter a valid whole number for Test ID.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(
                    "Database operation failed. Reopen the same test to resume.");
        }
    }

    private void answerQuestions(int attemptId) throws SQLException {
        while (true) {
            List<AttemptQuestion> questions =
                    service.getAttemptQuestions(attemptId);

            System.out.println("\n--- Test Questions ---");

            for (int i = 0; i < questions.size(); i++) {
                AttemptQuestion question = questions.get(i);

                System.out.println(
                        "\n" + (i + 1) + ". " + question.questionText()
                        + " [" + question.marks() + " marks]");
                System.out.println("A. " + question.optionA());
                System.out.println("B. " + question.optionB());
                System.out.println("C. " + question.optionC());
                System.out.println("D. " + question.optionD());
                System.out.println(
                        "Saved answer: "
                        + (question.selectedOption() == null
                           ? "Not answered" : question.selectedOption()));
            }

            System.out.println("\n0. Back (timer continues)");
            System.out.println("S. Submit Test");
            System.out.print("Choose a question number, S or 0: ");

            if (!scanner.hasNextLine()) {
                return;
            }
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("S")) {
                System.out.println(
                        "After submission, you cannot change answers "
                        + "or attempt this test again.");
                System.out.print("Submit now? (yes/no): ");

                if (!scanner.hasNextLine()) {
                    return;
                }

                if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                    System.out.println("Submission cancelled.");
                    continue;
                }

                int score = evaluationService.submitAttempt(attemptId);

                System.out.println("Test finalized successfully.");
                System.out.println("Your score: " + score);
                System.out.println("Returning to candidate menu.");
                return;
            }
            if (input.equals("0")) {
                System.out.println(
                        "Returning to candidate menu. Saved answers are kept.");
                return;
            }

            try {
                int number = Integer.parseInt(input);

                if (number < 1 || number > questions.size()) {
                    System.out.println("Choose a question number from the list.");
                    continue;
                }

                AttemptQuestion question = questions.get(number - 1);

                System.out.print("Answer (A/B/C/D): ");
                if (!scanner.hasNextLine()) {
                    return;
                }

                String answer = scanner.nextLine();

                service.saveAnswer(
                        attemptId, question.questionId(), answer);

                System.out.println("Answer saved successfully.");

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid question number.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}