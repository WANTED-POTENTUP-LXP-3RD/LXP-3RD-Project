package com.lxp.aplus.review.application.command;

import java.time.LocalDateTime;

public record ReviewAnalyzeSaveCommand(
        Long courseId,
        int day,
        String mood,
        String insightSummary
) {
    public static ReviewAnalyzeSaveCommand of(
            Long courseId, int day, String mood, String insightSummary
    ) {
        return new ReviewAnalyzeSaveCommand(courseId, day, mood, insightSummary);
    }
}
