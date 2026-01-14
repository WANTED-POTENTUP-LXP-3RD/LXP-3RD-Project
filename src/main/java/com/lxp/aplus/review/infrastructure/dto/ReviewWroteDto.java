package com.lxp.aplus.review.infrastructure.dto;

import com.lxp.aplus.review.domain.Reviews;

public record ReviewWroteDto(
        Long courseId
) {
    public static ReviewWroteDto from(Reviews reviews) {
        return new ReviewWroteDto(reviews.getCourseId());
    }
}
