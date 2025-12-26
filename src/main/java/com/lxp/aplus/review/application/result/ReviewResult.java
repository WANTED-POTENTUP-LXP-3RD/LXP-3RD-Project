package com.lxp.aplus.review.application.result;

import com.lxp.aplus.review.domain.Review;
import lombok.Builder;

import java.time.LocalDateTime;

public record ReviewResult(
        Long id,
        Long courseId,
        Long userId,
        Integer rating,      // 10점 만점 원본 데이터
        Double displayRating, // 5점 만점 환산 데이터 (rating / 2.0)
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
    public static ReviewResult from(Review review) {
        return ReviewResult.builder()
                .id(review.getId())
                .courseId(review.getCourseId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .displayRating(review.getRating() / 2.0) // 10점 -> 5점 환산
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}