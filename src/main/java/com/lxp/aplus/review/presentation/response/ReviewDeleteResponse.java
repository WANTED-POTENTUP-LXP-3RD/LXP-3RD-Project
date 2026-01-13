package com.lxp.aplus.review.presentation.response;

import com.lxp.aplus.review.application.result.ReviewDeleteResult;

public record ReviewDeleteResponse(
        boolean isDeleted
) {
    public static ReviewDeleteResponse from(ReviewDeleteResult result) {
        return new ReviewDeleteResponse(result.isDeleted());
    }
}
