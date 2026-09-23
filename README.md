# CodeJudge

A console-based assessment platform built with Java, JDBC and MySQL.

## Project Status

The main console workflows are implemented and have been manually
tested during development. Final access-control checks, concurrency
testing and fresh-install verification are still pending.

A graphical user interface is planned after console validation.

## Features

### Admin
- Login and logout
- Create, view, edit and delete eligible tests
- Set test category, duration and total marks
- Activate and deactivate tests
- Add, view, edit and delete questions
- View registered candidates
- Reset candidate passwords
- View results for owned tests
- View per-test leaderboards
- Export results to CSV

### Candidate
- Register, log in and log out
- Change username and password using the current password
- View available tests with category, duration and total marks
- Search available tests by title or category
- Sort tests by ID, title, duration or total marks using Java Streams
- Start or resume a test with a live console countdown
- Save and change answers before the deadline
- Submit a test
- View personal results and test history
- View per-test leaderboards with the current candidate marked as You

### Category Design
Questions use their parent test's category. There is no separate
question-category field. Admins are responsible for adding questions
that match the test category.

Passing marks, pass/fail classification and grades are not implemented.

## Assessment Rules

- Each candidate can have only one attempt per test.
- An unfinished attempt can resume before its original deadline.
- Returning to the menu does not pause the timer.
- Submitted or timed-out attempts cannot be attempted again.
- Correct answers receive the question's marks.
- Incorrect and unanswered questions receive zero marks.
- Candidates cannot edit their scores.
- Equal scores receive equal leaderboard ranks, such as 1, 1, 3.

## Test Management Rules

- Admins manage tests that they created.
- Test duration and total marks must be positive whole numbers.
- Activation requires at least one question.
- The sum of question marks must equal the configured test total
  before activation.
- Test details and questions can only be changed while the test
  is inactive and has no attempts.
- An admin can delete an owned, inactive test with no attempts.
- Deletion removes the test and its questions in one transaction.
- Tests with attempts cannot be deleted.
- Deactivation hides a test from the available-tests list and
  prevents new attempts.
- Existing attempts may continue until their original deadlines.
- Deactivation does not remove previous results.

## Requirements

- JDK 21
- MySQL Server 8.4
- MySQL Connector/J JAR in the lib folder
- Windows CMD for the commands below and hidden password input

## Database Setup

Start the MySQL service. If necessary, open CMD as administrator:

```bat
net start MySQL84
```

The service name may differ on another computer.

Open the MySQL client:

```bat
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p
```

For a fresh database, run:

```sql
SOURCE C:/CodeJudge/sql/schema.sql;
```

Adjust the file paths for your computer.

The schema uses CREATE TABLE IF NOT EXISTS. Running it against an
older database does not automatically add missing columns or
constraints. Existing databases require appropriate migrations.

## Upgrading an Existing Database

Fresh installations use the latest sql/schema.sql only.
Do not run these migrations after creating a fresh database from
that schema: the columns and constraints already exist.

For a database created before categories and configurable total
marks were added, run the missing migrations in this order:

```sql
SOURCE C:/CodeJudge/sql/migration_001_test_category.sql;
SOURCE C:/CodeJudge/sql/migration_002_test_total_marks.sql;

## Compile

From the project directory:

```bat
cd /d C:\CodeJudge
javac -encoding UTF-8 -cp "lib/*" -sourcepath src -d out src\Main.java src\modules\adminAuth\AdminSetup.java
```

This compiles the application, the admin setup utility and their
referenced source files.

## Database Password

Set the MySQL root password in the CMD session used to run the app:

```bat
set "CODEJUDGE_DB_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD"
```

The current local-development configuration uses:

- Host: localhost
- Port: 3306
- Database: codejudge
- User: root

Never commit real passwords. A deployed application should use a
dedicated database account with suitable permissions.

## Create an Admin Account

Run the local setup utility:

```bat
java -cp "out;lib/*" modules.adminAuth.AdminSetup
```

Enter the name, username, password and password confirmation.
This utility is intended for the person administering the local
installation and is not part of candidate registration.

## Run the Application

```bat
java -cp "out;lib/*" Main
```

Choose Admin or Candidate from the main menu.

## Automatic Evaluation

While the application is running, a background scheduler checks
for expired attempts. It waits one second after each completed
check before starting the next check.

Expired attempts are evaluated from saved answers and marked
TIMED_OUT. Database failures are retried on later checks.

When the application is closed, the scheduler is not running.
Expired attempts are processed after the application restarts.
Deadlines remain unchanged.

A console input prompt may remain visible after automatic
evaluation. Further answer changes are rejected.

## Countdown Display

A separate scheduled thread displays the remaining time every second
while the candidate is in the test screen.

- The display uses the original attempt deadline.
- Back stops the display but does not pause the attempt.
- Resume starts the display using the remaining time.
- Submission or leaving the test screen stops the display thread.
- The countdown prints on new console lines and may appear between
  input prompts.
- At expiry, press Enter to leave a pending input prompt, then view
  My Results.
- The countdown does not calculate scores or perform submission.
  The existing evaluation scheduler handles automatic finalization.
- Database deadline checks determine whether an answer can be saved.
- Keep application and database clocks and time-zone settings aligned.

## CSV Exports

Reports are saved in an exports folder relative to the application's
working directory. Each export receives a separate filename.

Exports include candidate information and should remain local.
The exports folder and compiled output are excluded from Git.

## Architecture

Controller -> Service -> DAO -> MySQL

- Controller: console menus, input and output
- Service: validation, session checks and business rules
- DAO: SQL queries and database transactions
- Model: shared data objects
- Session: current logged-in identity

Passwords are stored as salted PBKDF2-HMAC-SHA256 hashes.
SQL parameters use PreparedStatement.

Candidate-facing services obtain the candidate ID from the session.
Admin services obtain the admin ID from the session.

## Project Structure

- sql/: database schema and seed file
- src/Main.java: application entry point
- src/config/: database connection
- src/model/: shared models
- src/modules/adminAuth/: admin authentication and candidate password reset
- src/modules/candidateAuth/: candidate authentication and account changes
- src/modules/testManagement/: tests and questions
- src/modules/testAttempt/: attempts and saved answers
- src/modules/evaluation/: submission, scoring and timeout processing
- src/modules/candidateResults/: personal results and history
- src/modules/reports/: admin reports, leaderboards and CSV export
- lib/: JDBC driver
- tests/: existing test sources
- out/: generated class files
- exports/: generated CSV reports

## Validation

Manually checked during development:

- Authentication and account changes
- Candidate password reset by an admin
- Test activation and deactivation
- Saving and resuming answers
- Automatic timeout evaluation
- Candidate results and admin reports
- Leaderboard display and CSV generation
- Test and question management
- Creating and editing configured total marks
- Blocking activation when question marks do not match test total
- Displaying category and total marks in test lists
- Deleting an eligible test and its questions
- Blocking deletion of active tests and tests with attempts
- Registered candidate directory
- Candidate leaderboard display and input validation
- Case-insensitive category search
- Sorting available tests by total marks

Multi-candidate leaderboard ordering and tied-score ranks still
require a dedicated runtime check.

These checks do not replace the pending final test pass. Verify
cross-account access restrictions, repeat-attempt prevention,
deadline enforcement, concurrent operations and a fresh setup
before treating the project as release-ready.

## Team Conventions

- Write code comments and documentation in English.
- Keep SQL in DAO classes and business rules in service classes.
- Do not log passwords or commit credentials, exports or compiled files.
- Document database changes.
- Work on feature branches and submit pull requests for review.