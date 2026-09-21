package model;

import java.time.LocalDateTime;

public record AdminResult(
        int attemptId,
        int testId,
        String testTitle,
        int candidateId,
        String candidateName,
        String username,
        String status,
        int score,
        int totalMarks,
        LocalDateTime submittedAt
) {}