package com.lxp.aplus.course.application.result;

import com.lxp.aplus.review.infrastructure.persistence.dto.ReviewSummary;

public record ReviewStat(
        int reviewCount,
        Double avgRating
) {
    public static ReviewStat from(ReviewSummary reviewSummary) {
        return new ReviewStat(reviewSummary.totalReviews(), reviewSummary.avgRating());
    }

    public static ReviewStat of(int reviewCount, Double avgRating) {
        return new ReviewStat(reviewCount,avgRating);
    }

    public static ReviewStat defaultValue() {
        return new ReviewStat(0,0.0);
    }
}
