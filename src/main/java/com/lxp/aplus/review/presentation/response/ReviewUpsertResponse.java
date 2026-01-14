package com.lxp.aplus.review.presentation.response;

import com.lxp.aplus.review.application.result.ReviewUpsertResult;
import lombok.Builder;

@Builder
public record ReviewUpsertResponse(
        Long reviewId
) {
    public static ReviewUpsertResponse from(ReviewUpsertResult result) {
        return ReviewUpsertResponse.builder()
                .reviewId(result.reviewId())
                .build();
    }
}
