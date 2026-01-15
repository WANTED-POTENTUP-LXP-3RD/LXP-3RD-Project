package com.lxp.aplus.course.application.dto;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewStats;

public record ReviewStat(
        int reviewCount,
        Double avgRating
) {
    public static ReviewStat from(ReviewStats reviewStats) {
        return new ReviewStat(reviewStats.totalReviews(), reviewStats.avgRating());
    }

    public static ReviewStat of(Long courseId, int reviewCount, Double avgRating) {
        return new ReviewStat(reviewCount,avgRating);
    }

    public static ReviewStat defaultValue() {
        return new ReviewStat(0,0.0);
    }
}
