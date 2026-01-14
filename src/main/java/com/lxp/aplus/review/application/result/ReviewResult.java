package com.lxp.aplus.review.application.result;

import com.lxp.aplus.review.domain.Reviews;
import com.lxp.aplus.review.domain.constant.ReviewStatus;
import java.time.LocalDateTime;
import lombok.Builder;

public record ReviewResult(
        Long id,
        Long courseId,
        Long userId,
        String nickName,
        Integer displayRating,
        String content,
        ReviewStatus status,
        Integer reported,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isMine
) {
    public static final Integer RATING_SCALE_FACTOR = 2;

    @Builder
    public ReviewResult {
    }

    /**
     * Entity를 Result DTO로 변환하는 정적 팩토리 메서드
     */
    public static ReviewResult of(Long userId, String nickName ,Reviews review) {

        return ReviewResult.builder()
                .id(review.getId())
                .courseId(review.getCourseId())
                .userId(review.getUserId())
                .nickName(nickName)
                .displayRating(review.getRating() / RATING_SCALE_FACTOR)
                .content(review.getContent())
                .status(review.getStatus())
                .reported(review.getReported())
                .isMine(isMine(userId, review.getUserId()))
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private static boolean isMine(Long userId, Long writerId) {
        if (userId == null) {
            return false;
        }
        return userId.equals(writerId);
    }
}