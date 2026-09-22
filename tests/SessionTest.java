import model.Candidate;
import modules.candidateAuth.CandidateSession;

public class SessionTest {
    public static void main(String[] args) {
        CandidateSession authSession = new CandidateSession();

        // for other module, will  receive same session object 
        CandidateSession otherModuleSession = authSession;

        check(!otherModuleSession.isLoggedIn(),
                "Before login: no authenticated session");

        Candidate candidate = new Candidate();
        candidate.setCandidateId(1);
        candidate.setFullName("Test Candidate");
        candidate.setUsername("testcandidate");
        candidate.setEmail("testcandidate@example.com");

        authSession.login(candidate);

        check(otherModuleSession.isLoggedIn()
                        && otherModuleSession.getCurrentCandidate()
                                .getCandidateId() == 1,
                "After login: candidate identity is shared");

        authSession.logout();

        check(!otherModuleSession.isLoggedIn()
                        && otherModuleSession.getCurrentCandidate() == null,
                "After logout: shared identity is cleared");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAIL: " + message);
        }
        System.out.println("PASS: " + message);
    }
}