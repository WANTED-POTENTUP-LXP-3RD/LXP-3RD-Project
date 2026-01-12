package com.lxp.aplus.review.application.result;

import com.lxp.aplus.review.domain.Reviews;
import lombok.Builder;

@Builder
public record ReviewUpsertResult(
        Long reviewId,
        int rating
) {
    public static ReviewUpsertResult from(Reviews review) {
        return ReviewUpsertResult.builder()
                .reviewId(review.getId())
                .rating(review.getRating()/2)
                .build();
    }
}
