package com.lxp.aplus.review.presentation.response;

import com.lxp.aplus.review.application.result.ReviewWroteResult;

public record ReviewWroteResponse(
        Long courseId,
        boolean isReviewed
) {
    public static ReviewWroteResponse from(ReviewWroteResult content) {
        return new ReviewWroteResponse(content.courseId(), content.isReviewed());
    }
}
