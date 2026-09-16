import model.Candidate;
import modules.candidateAuth.CandidateSession;

public class SessionTest {
    public static void main(String[] args) {
        CandidateSession authSession = new CandidateSession();

        // for other module, will  receive same session object 
        CandidateSession otherModuleSession = authSession;

        check(!otherModuleSession.isLoggedIn(),
                "Before login: no authenticated session");

        authSession.login(
                new Candidate(1, "Test Candidate", "testcandidate",
                        "testcandidate@example.com"));

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