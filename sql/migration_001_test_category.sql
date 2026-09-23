USE codejudge;

ALTER TABLE tests
    ADD COLUMN category VARCHAR(50) NOT NULL
    DEFAULT 'General' AFTER description;