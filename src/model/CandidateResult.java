package model;

import java.time.LocalDateTime;

public record CandidateResult(
        int attemptId,
        int testId,
        String testTitle,
        String status,
        int score,
        int totalMarks,
        LocalDateTime submittedAt
) {}