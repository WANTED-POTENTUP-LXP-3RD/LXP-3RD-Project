package com.lxp.aplus.review.application.result;

import lombok.Builder;

public record ReviewForAnalysisResult(
        Double rating,
        String mood,
        String insightSummary
) {
    @Builder
    public ReviewForAnalysisResult {

    }

    public static ReviewForAnalysisResult of(Double rating, ReviewAnalysisAiResult result) {

        return ReviewForAnalysisResult.builder()
                .rating(rating)
                .mood(result.mood())
                .insightSummary(result.insightSummary())
                .build();
    }
}
