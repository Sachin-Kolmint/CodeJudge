-- ============================================================
-- CodeJudge - Database Schema
-- Owner: Member 1 (Database & Shared Foundation)
-- Engine: MySQL 8.x
-- ============================================================
-- Run: mysql -u <user> -p < schema.sql
-- This script drops and recreates the schema so it can be
-- rebuilt cleanly from scratch at any time.
-- ============================================================

DROP DATABASE IF EXISTS codejudge;
CREATE DATABASE codejudge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE codejudge;

-- ------------------------------------------------------------
-- ADMINS
-- ------------------------------------------------------------
CREATE TABLE admins (
    admin_id      INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- CANDIDATES
-- ------------------------------------------------------------
CREATE TABLE candidates (
    candidate_id  INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- TESTS
-- Soft-delete via `status`. Physical delete of a published
-- test is intentionally restricted (see FKs below) so that
-- historical attempts/results are never orphaned.
-- ------------------------------------------------------------
CREATE TABLE tests (
    test_id          INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(150) NOT NULL,
    description      TEXT,
    duration_minutes INT NOT NULL,
    total_marks      INT NOT NULL DEFAULT 0,
    status           ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT',
    created_by       INT NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tests_admin FOREIGN KEY (created_by)
        REFERENCES admins(admin_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- QUESTIONS
-- Deleting a test cascades to its questions. A test with
-- attempts already in progress should be moved to ARCHIVED
-- (soft delete) rather than physically deleted -- enforced by
-- application logic in Member 3's module, not by the DB.
-- ------------------------------------------------------------
CREATE TABLE questions (
    question_id     INT AUTO_INCREMENT PRIMARY KEY,
    test_id         INT NOT NULL,
    question_text   TEXT NOT NULL,
    option_a        VARCHAR(255) NOT NULL,
    option_b        VARCHAR(255) NOT NULL,
    option_c        VARCHAR(255) NOT NULL,
    option_d        VARCHAR(255) NOT NULL,
    correct_option  ENUM('A', 'B', 'C', 'D') NOT NULL,
    marks           INT NOT NULL DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_test FOREIGN KEY (test_id)
        REFERENCES tests(test_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- ATTEMPTS
-- One row per candidate attempt of a test. Multiple attempts
-- per candidate/test are allowed at the DB level; if the team
-- decides to restrict repeat attempts, enforce it in Service
-- code (Member 6), not with a UNIQUE constraint here.
-- ------------------------------------------------------------
CREATE TABLE attempts (
    attempt_id   INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    test_id      INT NOT NULL,
    start_time   TIMESTAMP NULL,
    end_time     TIMESTAMP NULL,
    status       ENUM('IN_PROGRESS', 'SUBMITTED', 'TIMED_OUT') NOT NULL DEFAULT 'IN_PROGRESS',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempts_candidate FOREIGN KEY (candidate_id)
        REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    CONSTRAINT fk_attempts_test FOREIGN KEY (test_id)
        REFERENCES tests(test_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- ANSWERS
-- Candidate's selected option per question, per attempt.
-- selected_option is NULL until the candidate answers, and
-- stays NULL for questions left unanswered at submission.
-- One row per (attempt, question) -- enforced by UNIQUE key.
-- ------------------------------------------------------------
CREATE TABLE answers (
    answer_id       INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id      INT NOT NULL,
    question_id     INT NOT NULL,
    selected_option ENUM('A', 'B', 'C', 'D') NULL,
    answered_at     TIMESTAMP NULL,
    CONSTRAINT fk_answers_attempt FOREIGN KEY (attempt_id)
        REFERENCES attempts(attempt_id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id)
        REFERENCES questions(question_id) ON DELETE CASCADE,
    CONSTRAINT uq_answers_attempt_question UNIQUE (attempt_id, question_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- RESULTS
-- The single official result per attempt, created only by
-- Member 7's Evaluation module. One-to-one with attempts,
-- enforced by UNIQUE (attempt_id) -- prevents duplicate
-- official results from a repeated/duplicate submission.
-- ------------------------------------------------------------
CREATE TABLE results (
    result_id           INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id          INT NOT NULL UNIQUE,
    candidate_id        INT NOT NULL,
    test_id             INT NOT NULL,
    total_questions     INT NOT NULL,
    correct_count        INT NOT NULL,
    wrong_count          INT NOT NULL,
    unanswered_count     INT NOT NULL,
    score               DECIMAL(6,2) NOT NULL,
    percentage           DECIMAL(5,2) NOT NULL,
    pass_fail            ENUM('PASS', 'FAIL', 'NA') NOT NULL DEFAULT 'NA',
    submitted_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_attempt FOREIGN KEY (attempt_id)
        REFERENCES attempts(attempt_id) ON DELETE CASCADE,
    CONSTRAINT fk_results_candidate FOREIGN KEY (candidate_id)
        REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    CONSTRAINT fk_results_test FOREIGN KEY (test_id)
        REFERENCES tests(test_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Helpful indexes for common lookups
-- ------------------------------------------------------------
CREATE INDEX idx_questions_test        ON questions(test_id);
CREATE INDEX idx_attempts_candidate    ON attempts(candidate_id);
CREATE INDEX idx_attempts_test         ON attempts(test_id);
CREATE INDEX idx_answers_attempt       ON answers(attempt_id);
CREATE INDEX idx_results_candidate     ON results(candidate_id);
CREATE INDEX idx_results_test          ON results(test_id);
