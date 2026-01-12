package com.lxp.aplus.review.presentation.response;

import com.lxp.aplus.review.application.result.ReviewResult;
import com.lxp.aplus.review.domain.constant.ReviewStatus;
import java.time.LocalDateTime;
import lombok.Builder;

public record ReviewResponse(
        Long id,
        Long userId,
        Long courseId,
        Integer rating,
        String content,
        ReviewStatus status,
        Integer reported,
        LocalDateTime createAt,
        LocalDateTime updateAt
) {
    @Builder
    public ReviewResponse{

    }

    public static ReviewResponse from(ReviewResult reviewResult) {
        return ReviewResponse.builder()
                .id(reviewResult.id())
                .userId(reviewResult.userId())
                .courseId(reviewResult.courseId())
                .rating(reviewResult.displayRating())
                .content(reviewResult.content())
                .status(reviewResult.status())
                .reported(reviewResult.reported())
                .createAt(reviewResult.createdAt())
                .updateAt(reviewResult.updatedAt())
                .build();
    }
}
