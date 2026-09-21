import java.util.Scanner;

import modules.adminAuth.AdminAuthController;
import modules.adminAuth.AdminSession;
import modules.candidateAuth.CandidateAuthController;
import modules.candidateAuth.CandidateSession;
import modules.evaluation.EvaluationScheduler;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             EvaluationScheduler scheduler = new EvaluationScheduler()) {

            scheduler.start();
            AdminSession adminSession = new AdminSession();
            CandidateSession candidateSession = new CandidateSession();

            AdminAuthController adminController =
                    new AdminAuthController(adminSession, scanner);

            CandidateAuthController candidateController =
                    new CandidateAuthController(candidateSession, scanner);

            while (true) {
                System.out.println("\n--- CodeJudge ---");
                System.out.println("0. Exit");
                System.out.println("1. Admin");
                System.out.println("2. Candidate");
                System.out.print("Choose: ");

                if (!scanner.hasNextLine()) {
                    return;
                }

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> adminController.start();
                    case "2" -> candidateController.start();
                    case "0" -> {
                        adminSession.logout();
                        candidateSession.logout();
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
    }
}