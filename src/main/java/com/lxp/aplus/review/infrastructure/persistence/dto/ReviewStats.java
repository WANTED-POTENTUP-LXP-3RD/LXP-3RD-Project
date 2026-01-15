package com.lxp.aplus.review.infrastructure.persistence.dto;

public record ReviewStats(
        Long courseId,
        Double avgRating,
        int totalReviews
) {
    public static ReviewStats of(Long courseId, Double avgRating, int totalReviews) {
        return new ReviewStats(courseId, avgRating/2, totalReviews);
    }

    public static ReviewStats defaultValue() {
        return new ReviewStats(0L,0.0,0);
    }
}
