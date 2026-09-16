## Candidate Authentication Module

### Features
- Candidate registration with input validation
- Duplicate username and email prevention
- Login using username and password
- Password hashing using PBKDF2-HMAC-SHA256 and random salt
- In-memory candidate session and logout

### Requirements
- JDK 21
- MySQL Server 8.4
- MySQL Connector/J JAR placed in the lib folder

### Database Setup
Start MySQL Server and open the MySQL command-line client.
Run:

```sql
SOURCE C:/CodeJudge/sql/schema.sql;
```

### Compile and Run
Open Windows Command Prompt in C:\CodeJudge.
Set your local MySQL root password for this terminal session:

```bat
set "CODEJUDGE_DB_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD"
```

Compile and run:

```bat
javac -encoding UTF-8 -cp "lib/*" -d out src\Main.java src\config\DBConnection.java src\model\Candidate.java src\modules\candidateAuth\*.java
java -cp "out;lib/*" Main
```

The database connection uses localhost:3306 and database codejudge.
The root account is used for local development.

### Manual Tests Passed
- Registration with valid details
- Successful login and logout
- Incorrect password rejection
- Duplicate account rejection
- Empty username rejection
- Invalid email rejection
- Short password rejection
- Invalid username rejection
- Stored password hash format verification

### Integration Notes
- Flow: Controller → Service → DAO → MySQL
- Shared model: src/model/Candidate.java
- Shared connection: src/config/DBConnection.java
- Reconcile shared model and schema with Member 1 during integration.
- The current console menu demonstrates candidate authentication.
- Sessions end when the application closes.

### Authentication Integration Contract
- CandidateAuthDAO retrieves the candidate and stored password hash.
- CandidateAuthService verifies the password and returns a Candidate
  on successful login, or null for invalid credentials.
- Main creates one CandidateSession and passes it to
  CandidateAuthController through its constructor.
- During integration, pass that same session instance to the
  Test Attempt and Candidate Results modules.
- Protected operations must check session.isLoggedIn() before using
  session.getCurrentCandidate().getCandidateId().
- Logout clears the candidate from the shared session.
- Actual cross-module access checks remain pending integration.

### Session Test
Run after compiling the application:

javac -encoding UTF-8 -cp out -d out tests\SessionTest.java
java -cp out SessionTest

Passed: initial logged-out state, shared candidate identity after
login, and cleared identity after logout.