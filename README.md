# Candidate Authentication Module - Member 5

## Scope

Candidate registration, login, logout, and shared candidate identity.
This branch contains a standalone authentication demo for team integration.

## Features

- Registration with input validation
- Duplicate username and email prevention
- Password hashing with PBKDF2-HMAC-SHA256 and a random salt
- Password verification in the service layer
- PreparedStatement-based database access
- Shared in-memory candidate session
- Shared Scanner supplied by Main
- Explicit logout and application exit messages

## Requirements

- JDK 21
- MySQL Server 8.4
- MySQL Connector/J JAR in the lib directory

## Standalone Test Database

Open the MySQL command-line client and run:

```sql
CREATE DATABASE IF NOT EXISTS codejudge_member5
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE codejudge_member5;

CREATE TABLE IF NOT EXISTS candidates (
    candidate_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
```

This isolated database tests Member 5 against Member 1's candidate
table structure. It is not the complete team database.

The existing sql/schema.sql targets codejudge, so use the SQL above
for this isolated test setup.

## Local Database Configuration

From the project root in Windows Command Prompt:

```bat
copy src\config\db.properties.example src\config\db.properties
```

If db.properties already exists, edit it without overwriting it.

Set these properties using your local MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/codejudge_member5?useSSL=false&serverTimezone=UTC
db.user=root
db.password=YOUR_LOCAL_MYSQL_PASSWORD
```

db.properties is ignored by Git. Never commit real credentials.
The root account and disabled SSL setting are for local testing only.

DBConfig loads /config/db.properties from the runtime classpath.
The CODEJUDGE_DB_PASSWORD environment variable is no longer used.

## Compile and Run

Open Windows Command Prompt in the project root.
For the current worktree:

```bat
cd /d C:\CodeJudge-member5
javac -encoding UTF-8 -cp "lib/*" -d out -sourcepath src src\Main.java tests\SessionTest.java
copy /Y src\config\db.properties out\config\db.properties
java -cp "out;lib/*" Main
```

Stop if compilation fails.

Repeat the properties copy command whenever local credentials change.
Java compilation does not copy configuration resources automatically.

Run from a terminal for hidden password entry. The current controller
uses visible password input when System.console() is unavailable.

## Session Test

After compilation:

```bat
java -cp out SessionTest
```

This checks:

- No authenticated identity before login
- Shared candidate identity after login
- Cleared identity after logout

## Manual Checks Completed

- Registration with valid details
- Successful login
- Incorrect password rejection
- Duplicate username rejection
- Duplicate email rejection
- Blank full name rejection
- Invalid username rejection
- Invalid email rejection
- Short password rejection
- Logout returns to the authentication menu
- Menus display option 0 first
- Logout and exit display explicit messages
- Login, logout, and exit work with the shared Scanner

These checks cover the standalone module, not full team integration.

## Architecture

Controller -> Service -> DAO -> MySQL

- CandidateAuthController handles terminal input and messages.
- CandidateAuthService validates inputs and verifies passwords.
- CandidateAuthDAO performs SQL operations.
- PasswordUtil hashes and verifies passwords.
- CandidateSession holds the authenticated candidate in memory.

## Team Integration Contract

The Candidate model, DBConfig, and DBConnection were adopted from
Member 1's shared foundation branch.

- Use model.Candidate as the shared candidate model.
- Use config.DBConnection.getConnection() for database access.
- Keep password hashes separate from the session Candidate.
- CandidateAuthDAO returns candidate identity and hash as LoginData.
- CandidateAuthService returns a Candidate after successful verification,
  or null for invalid credentials.
- Main creates one CandidateSession and one Scanner.
- Construct the controller with:
  new CandidateAuthController(session, scanner)
- Pass the same session to Test Attempt and Candidate Results.
- Protected operations must check session.isLoggedIn() before reading
  session.getCurrentCandidate().getCandidateId().
- The application owns the Scanner; controllers must not close it.
- Logout clears the shared session.
- The current standalone option 0 exits the application. When integrating
  with a parent menu, adapt its label and message to the return behavior.
- JDK 21 is required by this module and must be reflected in the team build.
- Coordinate the final database setup with Member 1.

Cross-module authorization and the integrated application flow still
require testing with the other members' modules.