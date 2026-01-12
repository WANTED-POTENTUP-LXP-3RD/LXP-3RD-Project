package com.lxp.aplus.review.application.result;

import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.constant.ReviewStatus;
import java.time.LocalDateTime;
import lombok.Builder;

public record ReviewResult(
        Long id,
        Long courseId,
        Long userId,
        Integer displayRating,
        String content,
        ReviewStatus status,
        Integer reported,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static final Integer RATING_SCALE_FACTOR = 2;

    @Builder
    public ReviewResult {
    }

    /**
     * Entity를 Result DTO로 변환하는 정적 팩토리 메서드
     */
    public static ReviewResult from(Reviews review) {
        return ReviewResult.builder()
                .id(review.getId())
                .courseId(review.getCourseId())
                .userId(review.getUserId())
                .displayRating(review.getRating() / RATING_SCALE_FACTOR)
                .content(review.getContent())
                .status(review.getStatus())
                .reported(review.getReported())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}