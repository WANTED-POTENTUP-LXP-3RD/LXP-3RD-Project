package com.lxp.aplus.review.application.result;

import com.lxp.aplus.review.domain.Review;
import lombok.Builder;

@Builder
public record ReviewUpsertResult(
        Long reviewId
) {
    public static ReviewUpsertResult from(Review review) {
        return ReviewUpsertResult.builder()
                .reviewId(review.getId())
                .build();
    }
}
