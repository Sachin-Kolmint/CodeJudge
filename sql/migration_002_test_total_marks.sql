USE codejudge;

ALTER TABLE tests
    ADD COLUMN total_marks INT NOT NULL DEFAULT 1
        AFTER duration_minutes,
    ADD CONSTRAINT chk_test_total_marks
        CHECK (total_marks > 0);

UPDATE tests t
SET total_marks = COALESCE(
    (SELECT SUM(q.marks)
     FROM questions q
     WHERE q.test_id = t.test_id),
    1
);