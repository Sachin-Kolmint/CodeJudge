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