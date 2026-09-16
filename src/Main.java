import modules.candidateAuth.CandidateAuthController;
import modules.candidateAuth.CandidateSession;

public class Main {
    public static void main(String[] args) {
        CandidateSession session = new CandidateSession();
        CandidateAuthController controller =
                new CandidateAuthController(session);

        controller.start();
    }
}