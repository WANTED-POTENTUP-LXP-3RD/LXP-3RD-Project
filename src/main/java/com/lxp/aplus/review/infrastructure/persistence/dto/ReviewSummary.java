package com.lxp.aplus.review.infrastructure.persistence.dto;

public record ReviewSummary(
        Long courseId,
        Double avgRating,
        int totalReviews
) {
    public static ReviewSummary of(Long courseId, Double avgRating, int totalReviews) {
        return new ReviewSummary(courseId, avgRating/2, totalReviews);
    }

    public static ReviewSummary defaultValue() {
        return new ReviewSummary(0L,0.0,0);
    }
}
