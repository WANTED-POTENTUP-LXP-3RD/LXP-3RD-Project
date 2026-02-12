package com.lxp.aplus.review.presentation.response;

import com.lxp.aplus.review.application.result.ReviewForAnalysisResult;
import lombok.Builder;

public record ReviewAnalyzeResponse(
        Double rating,
        String mood,
        String insightSummary
) {
    @Builder
    public ReviewAnalyzeResponse {

    }

    public static ReviewAnalyzeResponse from(ReviewForAnalysisResult result) {

        return ReviewAnalyzeResponse.builder()
                .rating(result.rating())
                .mood(result.mood())
                .insightSummary(result.insightSummary())
                .build();
    }
}
