-- ============================================================
-- CodeJudge - Seed / Sample Data
-- Run AFTER schema.sql
-- Passwords below are placeholder BCrypt-style hashes for dev
-- only. Real hashing is Member 2 / Member 5's responsibility
-- in the Auth modules -- do not use these values in production.
-- ============================================================

USE codejudge;

-- Admins
INSERT INTO admins (username, password_hash, full_name) VALUES
('admin1', '$2a$10$devOnlyPlaceholderHashAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA', 'Ayush Admin'),
('admin2', '$2a$10$devOnlyPlaceholderHashBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB', 'Sachin Admin');

-- Candidates
INSERT INTO candidates (username, email, password_hash, full_name) VALUES
('candidate1', 'cand1@example.com', '$2a$10$devOnlyPlaceholderHashCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC', 'Riya Sharma'),
('candidate2', 'cand2@example.com', '$2a$10$devOnlyPlaceholderHashDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD', 'Aman Verma'),
('candidate3', 'cand3@example.com', '$2a$10$devOnlyPlaceholderHashEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE', 'Priya Singh');

-- Tests
INSERT INTO tests (title, description, duration_minutes, total_marks, status, created_by) VALUES
('Core Java Basics', 'MCQ test on Java fundamentals', 20, 20, 'PUBLISHED', 1),
('SQL Fundamentals', 'MCQ test on basic SQL concepts', 15, 10, 'DRAFT', 1);

-- Questions for Test 1 (Core Java Basics) -- 4 questions, 5 marks each = 20
INSERT INTO questions (test_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks) VALUES
(1, 'Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'super', 'B', 5),
(1, 'Which of these is NOT a Java primitive type?', 'int', 'boolean', 'String', 'char', 'C', 5),
(1, 'What is the default value of a boolean instance variable?', 'true', 'false', '0', 'null', 'B', 5),
(1, 'Which collection class allows duplicate elements and maintains insertion order?', 'HashSet', 'TreeSet', 'ArrayList', 'HashMap', 'C', 5);

-- Questions for Test 2 (SQL Fundamentals) -- 2 questions, 5 marks each = 10
INSERT INTO questions (test_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks) VALUES
(2, 'Which SQL clause is used to filter rows before grouping?', 'HAVING', 'WHERE', 'GROUP BY', 'ORDER BY', 'B', 5),
(2, 'Which keyword makes a column value unique across a table?', 'PRIMARY', 'UNIQUE', 'INDEX', 'DISTINCT', 'B', 5);

-- Sample completed attempt + answers + result for Test 1 / candidate1
INSERT INTO attempts (candidate_id, test_id, start_time, end_time, status) VALUES
(1, 1, NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 15 MINUTE, 'SUBMITTED');

INSERT INTO answers (attempt_id, question_id, selected_option, answered_at) VALUES
(1, 1, 'B', NOW() - INTERVAL 29 MINUTE),
(1, 2, 'C', NOW() - INTERVAL 28 MINUTE),
(1, 3, 'A', NOW() - INTERVAL 27 MINUTE),
(1, 4, NULL, NULL);

INSERT INTO results (attempt_id, candidate_id, test_id, total_questions, correct_count, wrong_count, unanswered_count, score, percentage, pass_fail) VALUES
(1, 1, 1, 4, 2, 1, 1, 10.00, 50.00, 'FAIL');
