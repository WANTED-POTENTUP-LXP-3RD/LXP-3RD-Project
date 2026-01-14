package com.lxp.aplus.review.application.result;

public record ReviewWroteResult(
        Long courseId,
        boolean isReviewed
) {
}
