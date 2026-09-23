package model;

public record LeaderboardEntry(
        int candidateId,
        String candidateName,
        int score,
        int totalMarks
) {}
