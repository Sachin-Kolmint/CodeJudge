import java.util.Scanner;
import modules.candidateAuth.CandidateAuthController;
import modules.candidateAuth.CandidateSession;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            CandidateSession session = new CandidateSession();
            CandidateAuthController controller =
                    new CandidateAuthController(session, scanner);

            controller.start();
        }
    }
}