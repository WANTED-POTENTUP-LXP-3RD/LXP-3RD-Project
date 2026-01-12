package com.lxp.aplus.review.application.result;

import com.lxp.aplus.review.domain.Reviews;
import java.time.LocalDateTime;
import lombok.Builder;

public record ReviewResult(
        Long id,
        Long courseId,
        Long userId,
        Integer rating,
        Double displayRating,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
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
                .rating(review.getRating())
                .displayRating(review.getRating() / 2.0)
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}