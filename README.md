# CodeJudge

Java + JDBC + MySQL assessment platform. Admins create tests and MCQ questions;
candidates register, take timed tests, and view results. Admins view reports,
leaderboard, and export CSV.

This README currently documents the **shared foundation** (database, JDBC
config, shared models) owned by Member 1. Each module owner should append
their own module's README section (see the plan doc) once their branch is
ready for integration.

## 1. Setup

### Prerequisites
- JDK 11+
- MySQL 8.x running locally (or accessible)
- MySQL Connector/J JAR on your classpath (download from
  https://dev.mysql.com/downloads/connector/j/ or via Maven if the project
  later adds a build tool)

### Steps
1. Create the database and tables:
   ```
   mysql -u root -p < sql/schema.sql
   ```
2. Load sample data (optional but recommended for development):
   ```
   mysql -u root -p < sql/seed.sql
   ```
3. Configure your local DB credentials:
   ```
   cp src/config/db.properties.example src/config/db.properties
   ```
   Edit `src/config/db.properties` with your MySQL username/password.
   This file is git-ignored — every developer keeps their own copy.
4. Compile and run the sanity check:
   ```
   javac -cp ".;path/to/mysql-connector-j.jar" -d out $(find src -name "*.java")
   java -cp "out;path/to/mysql-connector-j.jar" Main
   ```
   (On Linux/macOS use `:` instead of `;` as the classpath separator.)
   You should see `Database connection successful: codejudge`.

## 2. Database Schema & Relationships

| Table        | Purpose                                         | Key relationships |
|--------------|--------------------------------------------------|--------------------|
| `admins`     | Admin accounts                                   | — |
| `candidates` | Candidate accounts                               | — |
| `tests`      | Tests created by an admin                        | `created_by` → `admins.admin_id` |
| `questions`  | MCQ questions belonging to a test                | `test_id` → `tests.test_id` (CASCADE delete) |
| `attempts`   | One row per candidate attempt of a test          | `candidate_id` → `candidates`, `test_id` → `tests` |
| `answers`    | Candidate's selected option per question/attempt | `attempt_id` → `attempts`, `question_id` → `questions`; unique per (attempt, question) |
| `results`    | The single official result per attempt           | `attempt_id` → `attempts` (UNIQUE — one result per attempt), `candidate_id`, `test_id` |

**Flow:** `tests` + `questions` (Member 3) → candidate starts an `attempt`
(Member 6) → candidate submits `answers` (Member 6) → Evaluation (Member 7)
reads answers + correct options, computes the score, and writes the single
official row in `results` → Reports (Member 4) and Candidate Results
(Member 8) both read from `results` and must never recompute or duplicate it.

### Design decisions worth knowing
- **Soft delete for tests:** `tests.status` has `DRAFT` / `PUBLISHED` /
  `ARCHIVED`. Physically deleting a `PUBLISHED` test is blocked at the DB
  level (`ON DELETE RESTRICT` from `attempts` and `results`) so historical
  data is never orphaned. Use `ARCHIVED` instead of `DELETE` once a test has
  attempts.
- **Repeat attempts are allowed at the DB level.** There's no UNIQUE
  constraint stopping a candidate from attempting the same test twice. If
  the team wants to restrict this, enforce it in Member 6's Service layer.
- **`correct_option` on `questions` is sensitive.** DAOs/Services used while
  a candidate is taking a test must never expose this field to the UI.
- **One official result per attempt** is enforced by `UNIQUE (attempt_id)`
  on `results`, which also prevents duplicate results from a repeated
  submission (manual + timeout race, etc.).

## 3. Shared Models (`src/model/`)

`Admin`, `Candidate`, `Test`, `Question`, `Attempt`, `Answer`, `Result` — plain
data classes (fields + getters/setters + `toString`), one per table above.
**Do not create duplicate versions of these classes in your own module.**
Import them from `model.*`.

- `Test.Status` — enum: `DRAFT`, `PUBLISHED`, `ARCHIVED`
- `Question.Option` / `Answer.selectedOption` — enum: `A`, `B`, `C`, `D`
- `Attempt.Status` — enum: `IN_PROGRESS`, `SUBMITTED`, `TIMED_OUT`
- `Result.PassFail` — enum: `PASS`, `FAIL`, `NA`

## 4. JDBC Connection (`src/config/`)

- `DBConfig` — loads `db.properties` from the classpath.
- `DBConnection.getConnection()` — returns a new `java.sql.Connection`.

Use it in every DAO with try-with-resources:
```java
try (Connection conn = DBConnection.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setInt(1, someId);
    try (ResultSet rs = ps.executeQuery()) {
        // ...
    }
}
```
Never hold a single shared `Connection` across requests. Always use
`PreparedStatement`, never string-concatenated SQL.

## 5. Shared Utilities (`src/common/`)

- `DBUtil` — `closeQuietly(...)` overloads for `Connection`/`Statement`/`ResultSet`,
  for the rare case try-with-resources isn't practical.

## 6. What Other Modules Need From This Foundation

- Import shared models from `model.*` — do not redefine `Candidate`, `Admin`,
  `Test`, `Question`, `Attempt`, `Answer`, or `Result`.
- Get a connection via `config.DBConnection.getConnection()`.
- Each module's DAO owns its own SQL against these tables — this foundation
  does not provide generic CRUD DAOs, only the schema, models, and connection.
- If your module needs a column that doesn't exist yet, open an issue /
  message Member 1 before adding it yourself — don't modify `schema.sql`
  directly without coordinating, since every other branch is building against
  the current shape.

## 7. Testing Checklist (this module)

- [x] Schema builds from scratch (`schema.sql` drops + recreates cleanly)
- [x] Seed data inserts without FK errors
- [x] Sample query/connection round-trip via `Main.java`
- [x] Foreign keys verified (cascade on `questions`/`answers`/`attempts`→results;
      restrict on `tests` referenced by `attempts`/`results`)
- [ ] CRUD exercised by each downstream module as they integrate
- [ ] Invalid-reference tests (e.g. inserting a question against a
      non-existent test) — verified manually via `schema.sql` constraints;
      add automated tests under `tests/` as modules integrate

## Module Status

| Module | Owner | Status |
|--------|-------|--------|
| Database & Shared Foundation | Member 1 | Complete — ready for integration |
| Admin Authentication | Member 2 | Not started |
| Test & Question Management | Member 3 | Not started |
| Reports, Leaderboard & CSV | Member 4 | Not started |
| Candidate Authentication | Member 5 | Not started |
| Test Attempt | Member 6 | Not started |
| Timer, Submission & Evaluation | Member 7 | Not started |
| Candidate Results & History | Member 8 | Not started |
