CREATE DATABASE IF NOT EXISTS codejudge;

USE codejudge;

CREATE TABLE IF NOT EXISTS candidates (
    candidate_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS tests (
    test_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL DEFAULT 'General',
    duration_minutes INT NOT NULL,
    total_marks INT NOT NULL DEFAULT 1,
    created_by INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_test_duration
        CHECK (duration_minutes > 0),

    CONSTRAINT chk_test_total_marks
        CHECK (total_marks > 0),

    CONSTRAINT fk_test_admin
        FOREIGN KEY (created_by) REFERENCES admins(admin_id)
);
CREATE TABLE IF NOT EXISTS questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    test_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_option CHAR(1) NOT NULL,
    marks INT NOT NULL DEFAULT 1,

    CONSTRAINT chk_correct_option
        CHECK (correct_option IN ('A', 'B', 'C', 'D')),

    CONSTRAINT chk_question_marks
        CHECK (marks > 0),

    CONSTRAINT fk_question_test
        FOREIGN KEY (test_id) REFERENCES tests(test_id)
);
CREATE TABLE IF NOT EXISTS test_attempts (
    attempt_id INT PRIMARY KEY AUTO_INCREMENT,
    candidate_id INT NOT NULL,
    test_id INT NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deadline_at TIMESTAMP NOT NULL,
    submitted_at TIMESTAMP NULL DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    score INT NULL,
    total_marks INT NOT NULL,
    CONSTRAINT uq_candidate_test
        UNIQUE (candidate_id, test_id),

    CONSTRAINT fk_attempt_candidate
        FOREIGN KEY (candidate_id)
        REFERENCES candidates(candidate_id),

    CONSTRAINT fk_attempt_test
        FOREIGN KEY (test_id)
        REFERENCES tests(test_id),

    CONSTRAINT chk_attempt_status
        CHECK (status IN ('IN_PROGRESS', 'SUBMITTED', 'TIMED_OUT')),

    CONSTRAINT chk_attempt_total
        CHECK (total_marks > 0),

    CONSTRAINT chk_attempt_score
        CHECK (score IS NULL OR
               (score >= 0 AND score <= total_marks)),

    CONSTRAINT chk_attempt_deadline
        CHECK (deadline_at > started_at)
);
CREATE TABLE IF NOT EXISTS attempt_answers (
    answer_id INT PRIMARY KEY AUTO_INCREMENT,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option CHAR(1) NULL,
    marks_awarded INT NULL,

    CONSTRAINT uq_attempt_question
        UNIQUE (attempt_id, question_id),

    CONSTRAINT fk_answer_attempt
        FOREIGN KEY (attempt_id)
        REFERENCES test_attempts(attempt_id),

    CONSTRAINT fk_answer_question
        FOREIGN KEY (question_id)
        REFERENCES questions(question_id),

    CONSTRAINT chk_selected_option
        CHECK (selected_option IS NULL OR
               selected_option IN ('A', 'B', 'C', 'D')),

    CONSTRAINT chk_marks_awarded
        CHECK (marks_awarded IS NULL OR marks_awarded >= 0)
);
