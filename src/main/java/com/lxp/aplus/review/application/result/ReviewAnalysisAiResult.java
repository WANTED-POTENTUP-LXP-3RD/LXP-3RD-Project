package com.lxp.aplus.review.application.result;

public record ReviewAnalysisAiResult(
        String mood,
        String insightSummary
) {
    public static ReviewAnalysisAiResult of (String mood, String insightSummary) {
        return new ReviewAnalysisAiResult (mood, insightSummary);
    }
}